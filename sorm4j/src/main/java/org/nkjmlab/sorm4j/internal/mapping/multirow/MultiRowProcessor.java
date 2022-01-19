package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;
import org.nkjmlab.sorm4j.internal.mapping.SqlParametersToTableMapping;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.mapping.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.mapping.SqlParametersSetter;
import org.nkjmlab.sorm4j.util.logger.LogPoint;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;

public abstract class MultiRowProcessor<T> {

  private final int batchSize;
  private final PreparedStatementSupplier statementSupplier;
  private final SqlParametersSetter sqlParametersSetter;

  final SqlParametersToTableMapping<T> tableMapping;
  final LoggerContext loggerContext;

  MultiRowProcessor(LoggerContext loggerContext, SqlParametersSetter sqlParametersSetter,
      PreparedStatementSupplier statementSupplier, SqlParametersToTableMapping<T> tableMapping,
      int batchSize) {
    this.loggerContext = loggerContext;
    this.statementSupplier = statementSupplier;
    this.sqlParametersSetter = sqlParametersSetter;
    this.tableMapping = tableMapping;
    this.batchSize = batchSize;
  }

  public abstract int[] multiRowInsert(Connection con, @SuppressWarnings("unchecked") T... objects);

  public abstract int[] multiRowMerge(Connection con, @SuppressWarnings("unchecked") T... objects);

  public static final void setAutoCommit(Connection connection, boolean autoCommit) {
    Try.runOrElseThrow(() -> connection.setAutoCommit(autoCommit), Try::rethrow);
  }

  public static final void commitIfRequired(Connection connection, boolean origAutoCommit) {
    if (origAutoCommit) {
      Try.runOrElseThrow(() -> connection.commit(), Try::rethrow);
    }
  }


  public static final void rollbackIfRequired(Connection connection, boolean origAutoCommit) {
    if (!origAutoCommit) {
      Try.runOrElseThrow(() -> connection.rollback(), Try::rethrow);
    }
  }

  public static final boolean getAutoCommit(Connection connection) {
    return Try.createSupplierWithThrow(() -> connection.getAutoCommit(), Try::rethrow).get();
  }

  public final int[] batch(Connection con, String sql, Function<T, Object[]> parameterCreator,
      T[] objects) {
    return execMultiRowProcIfValidObjects(con, objects, nonNullObjects -> {
      int[] result = new int[0];
      boolean origAutoCommit = getAutoCommit(con);

      try (PreparedStatement stmt = statementSupplier.prepareStatement(con, sql)) {
        setAutoCommit(con, false);
        final BatchHelper batchHelper = new BatchHelper(batchSize, stmt);
        for (int i = 0; i < objects.length; i++) {
          T obj = objects[i];
          this.sqlParametersSetter.setParameters(stmt, parameterCreator.apply(obj));
          batchHelper.addBatchAndExecuteIfReachedThreshold();
        }
        result = batchHelper.finish();
        return result;
      } catch (Exception e) {
        rollbackIfRequired(con, origAutoCommit);
        throw Try.rethrow(e);
      } finally {
        commitIfRequired(con, origAutoCommit);
        setAutoCommit(con, origAutoCommit);
      }
    });
  }



  /**
   * Execute multirow sql function. objects when objects[0] is null, {@code NullPointerException}
   * are throw.
   */
  final int[] execMultiRowProcIfValidObjects(Connection con, T[] objects,
      Function<T[], int[]> exec) {
    if (objects == null || objects.length == 0) {
      return new int[0];
    }
    Optional<LogPoint> lp =
        loggerContext.createLogPoint(LoggerContext.Category.MULTI_ROW, MultiRowProcessor.class);
    lp.ifPresent(_lp -> _lp.logBeforeMultiRow(con, objects[0].getClass(), objects.length,
        tableMapping.getTableMetaData().getTableName()));


    final int[] result = exec.apply(objects);

    lp.ifPresent(_lp -> _lp.logAfterMultiRow(result));
    return result;
  }

  protected PreparedStatement prepareStatement(Connection con, String sql) throws SQLException {
    return statementSupplier.prepareStatement(con, sql);
  }


}
