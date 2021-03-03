package org.nkjmlab.sorm4j.mapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.util.ArrayUtils;
import org.nkjmlab.sorm4j.util.PreparedStatementUtils;
import org.nkjmlab.sorm4j.util.Try.ThrowableBiConsumer;
import org.nkjmlab.sorm4j.util.Try.ThrowableFunction;

public final class MultiRowInOneStatementProcessor<T> extends MultiRowProcessor<T> {

  private final int multiRowSize;

  public MultiRowInOneStatementProcessor(TableMapping<T> tableMapping, int batchSize,
      int multiRowSize) {
    super(tableMapping, batchSize);
    this.multiRowSize = multiRowSize;

  }

  @Override
  @SafeVarargs
  public final int[] multiRowInsert(Connection con, T... objects) {
    return execIfValidObjects(con, objects,
        nonNullObjects -> procMultiRowOneStatement(con,
            num -> PreparedStatementUtils.getPreparedStatement(con,
                tableMapping.getSql().getMultirowInsertSql(num)),
            (stmt, objs) -> tableMapping.setPrameters(stmt, objs), nonNullObjects));
  }

  @Override
  @SafeVarargs
  public final int[] multiRowMerge(Connection con, T... objects) {
    return execIfValidObjects(con, objects,
        nonNullObjects -> procMultiRowOneStatement(con,
            num -> PreparedStatementUtils.getPreparedStatement(con,
                tableMapping.getSql().getMultirowMergeSql(num)),
            (stmt, objs) -> tableMapping.setPrameters(stmt, objs), nonNullObjects));
  }


  private final int[] procMultiRowOneStatement(Connection con,
      ThrowableFunction<Integer, PreparedStatement> multiRowStatementCreator,
      ThrowableBiConsumer<PreparedStatement, T[]> parametersSetter, T[] objects) {
    final List<T[]> objsPartitions = ArrayUtils.split(multiRowSize, objects);
    final int[] result = new int[objsPartitions.size()];
    final boolean origAutoCommit = getAutoCommit(con);

    try {
      setAutoCommit(con, false);
      try (PreparedStatement stmt = multiRowStatementCreator.apply(multiRowSize)) {
        for (int partitionNum = 0; partitionNum < objsPartitions.size() - 1; partitionNum++) {
          T[] objectsInOnePartition = objsPartitions.get(partitionNum);
          parametersSetter.accept(stmt, objectsInOnePartition);
          result[partitionNum] = stmt.executeUpdate();
        }
      }
      int lastPartition = objsPartitions.size() - 1;
      T[] objectsInLastPartition = objsPartitions.get(lastPartition);
      try (PreparedStatement stmt = multiRowStatementCreator.apply(objectsInLastPartition.length)) {
        parametersSetter.accept(stmt, objectsInLastPartition);
        result[lastPartition] = stmt.executeUpdate();
        return result;
      }
    } catch (Throwable e) {
      rollbackIfRequired(con, origAutoCommit);
      throw new OrmException(e);
    } finally {
      commitIfRequired(con, origAutoCommit);
      setAutoCommit(con, origAutoCommit);
    }
  }



}
