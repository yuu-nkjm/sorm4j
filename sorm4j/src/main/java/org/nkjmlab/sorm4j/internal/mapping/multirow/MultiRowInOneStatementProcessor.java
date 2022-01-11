package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext;
import org.nkjmlab.sorm4j.internal.mapping.SqlParametersToTableMapping;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.internal.util.Try.ThrowableBiConsumer;
import org.nkjmlab.sorm4j.internal.util.Try.ThrowableFunction;

public final class MultiRowInOneStatementProcessor<T> extends MultiRowProcessor<T> {

  private final int multiRowSize;

  public MultiRowInOneStatementProcessor(LoggerContext loggerContext, SormOptions options,
      SqlParametersSetter sqlParametersSetter, SqlParametersToTableMapping<T> tableMapping, int batchSize,
      int multiRowSize) {
    super(loggerContext, options, sqlParametersSetter, tableMapping, batchSize);
    this.multiRowSize = multiRowSize;

  }

  @Override
  @SafeVarargs
  public final int[] multiRowInsert(Connection con, T... objects) {
    return execMultiRowProcIfValidObjects(con, objects,
        nonNullObjects -> procMultiRowOneStatement(con,
            num -> con.prepareStatement(tableMapping.getSql().getMultirowInsertSql(num)),
            (stmt, objs) -> tableMapping.setPrametersOfMultiRow(stmt, objs), nonNullObjects));
  }

  @Override
  @SafeVarargs
  public final int[] multiRowMerge(Connection con, T... objects) {
    return execMultiRowProcIfValidObjects(con, objects,
        nonNullObjects -> procMultiRowOneStatement(con,
            num -> con.prepareStatement(tableMapping.getSql().getMultirowMergeSql(num)),
            (stmt, objs) -> tableMapping.setPrametersOfMultiRow(stmt, objs), nonNullObjects));
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
        final int partitionSizeMinusOne = objsPartitions.size() - 1;
        for (int partitionNum = 0; partitionNum < partitionSizeMinusOne; partitionNum++) {
          final T[] objectsInOnePartition = objsPartitions.get(partitionNum);
          parametersSetter.accept(stmt, objectsInOnePartition);
          result[partitionNum] = stmt.executeUpdate();
        }
      }
      final int lastPartition = objsPartitions.size() - 1;
      final T[] objectsInLastPartition = objsPartitions.get(lastPartition);
      try (PreparedStatement stmt = multiRowStatementCreator.apply(objectsInLastPartition.length)) {
        parametersSetter.accept(stmt, objectsInLastPartition);
        result[lastPartition] = stmt.executeUpdate();
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
