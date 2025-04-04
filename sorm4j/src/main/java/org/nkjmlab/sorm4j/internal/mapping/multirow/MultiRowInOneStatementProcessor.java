package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.nkjmlab.sorm4j.context.logging.LogContext;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.internal.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.internal.mapping.ContainerToTableMapper;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.util.function.exception.Try;
import org.nkjmlab.sorm4j.util.function.exception.TryBiConsumer;
import org.nkjmlab.sorm4j.util.function.exception.TryFunction;

public final class MultiRowInOneStatementProcessor<T> extends MultiRowProcessorBase<T> {

  private final int multiRowSize;

  public MultiRowInOneStatementProcessor(
      LogContext loggerContext,
      SqlParametersSetter sqlParametersSetter,
      PreparedStatementSupplier statementSupplier,
      ContainerToTableMapper<T> tableMapping,
      int batchSize,
      int multiRowSize) {
    super(loggerContext, sqlParametersSetter, statementSupplier, tableMapping, batchSize);
    this.multiRowSize = multiRowSize;
  }

  @Override
  public final int[] multiRowInsert(Connection con, T[] objects) {
    return execMultiRowProcIfValidObjects(
        con,
        objects,
        nonNullObjects ->
            procMultiRowOneStatement(
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
            procMultiRowOneStatement(
                con,
                num -> prepareStatement(con, getSql().getMultirowMergeSql(num)),
                (stmt, objs) -> setPrametersOfMultiRow(stmt, objs),
                nonNullObjects));
  }

  private final int[] procMultiRowOneStatement(
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
      throw Try.rethrow(e);
    } finally {
      OrmConnectionImpl.commitOrRollback(con, origAutoCommit);
      OrmConnectionImpl.setAutoCommit(con, origAutoCommit);
    }
  }
}
