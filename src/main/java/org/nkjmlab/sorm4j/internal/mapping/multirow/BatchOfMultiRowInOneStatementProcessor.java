package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.internal.mapping.ConfigStore;
import org.nkjmlab.sorm4j.internal.mapping.TableMapping;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.internal.util.Try.ThrowableBiConsumer;
import org.nkjmlab.sorm4j.internal.util.Try.ThrowableFunction;

/**
 * A sql statement processor for multirow update and batch. This object could be set ormapper via
 * {@link ConfigStore}
 *
 * @author nkjm
 *
 * @param <T>
 */
final class BatchOfMultiRowInOneStatementProcessor<T> extends MultiRowProcessor<T> {

  private final int multiRowSize;
  private final int batchSizeWithMultiRow;

  public BatchOfMultiRowInOneStatementProcessor(SqlParameterSetter sqlParameterSetter,
      TableMapping<T> tableMapping, int batchSize, int multiRowSize, int batchSizeWithMultiRow) {
    super(sqlParameterSetter, tableMapping, batchSize);
    this.multiRowSize = multiRowSize;
    this.batchSizeWithMultiRow = batchSizeWithMultiRow;
  }

  @Override
  @SafeVarargs
  public final int[] multiRowInsert(Connection con, T... objects) {
    return execMultiRowProcIfValidObjects(con, objects,
        nonNullObjects -> procMultiRowOneStatementAndBatch(con,
            num -> con.prepareStatement(tableMapping.getSql().getMultirowInsertSql(num)),
            (stmt, objs) -> tableMapping.setPrameters(stmt, objs), nonNullObjects));
  }

  @Override
  @SafeVarargs
  public final int[] multiRowMerge(Connection con, T... objects) {
    return execMultiRowProcIfValidObjects(con, objects,
        nonNullObjects -> procMultiRowOneStatementAndBatch(con,
            num -> con.prepareStatement(tableMapping.getSql().getMultirowMergeSql(num)),
            (stmt, objs) -> tableMapping.setPrameters(stmt, objs), nonNullObjects));
  }

  /**
   * addBatch with multi-row. In H2 addBatch is not effictive.
   *
   * @param sqlCreator
   * @param parameterCreator
   * @param objects
   * @return
   */
  private final int[] procMultiRowOneStatementAndBatch(Connection con,
      ThrowableFunction<Integer, PreparedStatement> multiRowStatementCreator,
      ThrowableBiConsumer<PreparedStatement, T[]> parametersSetter, T[] objects) {

    final List<T[]> objsPartitions = ArrayUtils.split(multiRowSize, objects);
    final int[] result = new int[objsPartitions.size()];
    final boolean origAutoCommit = getAutoCommit(con);

    try {
      setAutoCommit(con, false);
      try (PreparedStatement stmt = multiRowStatementCreator.apply(multiRowSize)) {
        final BatchHelper helper = new BatchHelper(batchSizeWithMultiRow, stmt);
        for (int partitionNum = 0; partitionNum < objsPartitions.size() - 1; partitionNum++) {
          T[] objectsInOnePartition = objsPartitions.get(partitionNum);
          parametersSetter.accept(stmt, objectsInOnePartition);
          helper.addBatchAndExecuteIfReachedThreshold();
        }
        int[] firstResult = helper.finish();
        System.arraycopy(firstResult, 0, result, 0, firstResult.length);
      }
      // Recreate PreparedStatement because last partition is different size probably.
      int lastPartition = objsPartitions.size() - 1;
      T[] objectsInLastPartition = objsPartitions.get(lastPartition);
      try (PreparedStatement lastStmt =
          multiRowStatementCreator.apply(objectsInLastPartition.length)) {
        parametersSetter.accept(lastStmt, objectsInLastPartition);
        result[lastPartition] = lastStmt.executeUpdate();
        return result;
      }
    } catch (Throwable e) {
      rollbackIfRequired(con, origAutoCommit);
      throw Try.rethrow(e);
    } finally {
      commitIfRequired(con, origAutoCommit);
      setAutoCommit(con, origAutoCommit);
    }

  }



}