package org.nkjmlab.sorm4j.mapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.util.ArrayUtils;
import org.nkjmlab.sorm4j.util.PreparedStatementUtils;

public final class BatchOfMultiRowInOneStatementProcessor<T> extends MultiRowProcessor<T> {

  private final int multiRowSize;
  private final int batchSizeWithMultiRow;

  public BatchOfMultiRowInOneStatementProcessor(TableMapping<T> tableMapping, int batchSize,
      int multiRowSize, int batchSizeWithMultiRow) {
    super(tableMapping, batchSize);
    this.multiRowSize = multiRowSize;
    this.batchSizeWithMultiRow = batchSizeWithMultiRow;
  }

  @Override
  public int[] multiRowInsert(Connection con, T... objects) {
    return execIfValidObjects(con, objects,
        nonNullObjects -> procMultiRowOneStatementAndBatch(con,
            num -> PreparedStatementUtils.getPreparedStatement(con, tableMapping.getSql().getMultirowInsertSql(num)),
            (stmt, objs) -> tableMapping.setPrameters(stmt, objs), nonNullObjects));
  }

  @Override
  public int[] multiRowMerge(Connection con, T... objects) {
    return execIfValidObjects(con, objects,
        nonNullObjects -> procMultiRowOneStatementAndBatch(con,
            num -> PreparedStatementUtils.getPreparedStatement(con, tableMapping.getSql().getMultirowMergeSql(num)),
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
      Function<Integer, PreparedStatement> multiRowStatementCreator,
      BiConsumer<PreparedStatement, T[]> parametersSetter, T[] objects) {

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
    } catch (Exception e) {
      rollbackIfRequired(con, origAutoCommit);
      throw new OrmException(e);
    } finally {
      commitIfRequired(con, origAutoCommit);
      setAutoCommit(con, origAutoCommit);
    }

  }



}