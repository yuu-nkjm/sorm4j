package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.context.logging.LogContext;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.internal.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.internal.mapping.ContainerToTableMapper;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.util.function.exception.Try;
import org.nkjmlab.sorm4j.util.function.exception.TryBiConsumer;
import org.nkjmlab.sorm4j.util.function.exception.TryFunction;

/**
 * A sql statement processor for multirow update and batch. This object could be set ormapper via
 * {@link SormContext}
 *
 * @author nkjm
 * @param <T>
 */
public final class BatchOfMultiRowInOneStatementProcessor<T> extends MultiRowProcessorBase<T> {

  private final int multiRowSize;
  private final int batchSizeWithMultiRow;

  public BatchOfMultiRowInOneStatementProcessor(
      LogContext loggerContext,
      SqlParametersSetter sqlParametersSetter,
      PreparedStatementSupplier statementSupplier,
      ContainerToTableMapper<T> tableMapping,
      int batchSize,
      int multiRowSize,
      int batchSizeWithMultiRow) {
    super(loggerContext, sqlParametersSetter, statementSupplier, tableMapping, batchSize);
    this.multiRowSize = multiRowSize;
    this.batchSizeWithMultiRow = batchSizeWithMultiRow;
  }

  @Override
  public final int[] multiRowInsert(Connection con, T[] objects) {
    return execMultiRowProcIfValidObjects(
        con,
        objects,
        nonNullObjects ->
            procMultiRowOneStatementAndBatch(
                con,
                num -> prepareStatement(con, getSql().getMultirowInsertSql(num)),
                (stmt, objs) -> setPrametersOfMultiRow(stmt, objs),
                nonNullObjects));
  }

  @Override
  public final int[] multiRowMerge(Connection con, T[] objects) {
    return execMultiRowProcIfValidObjects(
        con,
        objects,
        nonNullObjects ->
            procMultiRowOneStatementAndBatch(
                con,
                num -> prepareStatement(con, getSql().getMultirowMergeSql(num)),
                (stmt, objs) -> setPrametersOfMultiRow(stmt, objs),
                nonNullObjects));
  }

  /**
   * addBatch with multi-row. In H2 addBatch is not effictive.
   *
   * @param sqlCreator
   * @param parameterCreator
   * @param objects
   * @return
   */
  private final int[] procMultiRowOneStatementAndBatch(
      Connection con,
      TryFunction<Integer, PreparedStatement> multiRowStatementCreator,
      TryBiConsumer<PreparedStatement, T[]> parametersSetter,
      T[] objects) {

    final List<T[]> objsPartitions = ArrayUtils.split(multiRowSize, objects);
    final int[] result = new int[objsPartitions.size()];
    final boolean origAutoCommit = OrmConnectionImpl.getAutoCommit(con);

    try {
      OrmConnectionImpl.setAutoCommit(con, false);
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
      throw Try.rethrow(e);
    } finally {
      OrmConnectionImpl.commitOrRollback(con, origAutoCommit);
      OrmConnectionImpl.setAutoCommit(con, origAutoCommit);
    }
  }
}
