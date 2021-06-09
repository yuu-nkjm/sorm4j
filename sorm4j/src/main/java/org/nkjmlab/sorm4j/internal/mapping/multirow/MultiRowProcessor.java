package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.function.Function;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext.LogPoint;
import org.nkjmlab.sorm4j.internal.mapping.TableMapping;
import org.nkjmlab.sorm4j.internal.util.Try;

public abstract class MultiRowProcessor<T> {

  private final int batchSize;
  private final SqlParametersSetter sqlParametersSetter;

  final TableMapping<T> tableMapping;
  final SormOptions options;
  final LoggerContext loggerContext;

  MultiRowProcessor(LoggerContext loggerContext, SormOptions options,
      SqlParametersSetter sqlParametersSetter, TableMapping<T> tableMapping, int batchSize) {
    this.loggerContext = loggerContext;
    this.options = options;
    this.sqlParametersSetter = sqlParametersSetter;
    this.tableMapping = tableMapping;
    this.batchSize = batchSize;
  }

  public abstract int[] multiRowInsert(Connection con, @SuppressWarnings("unchecked") T... objects);

  public abstract int[] multiRowMerge(Connection con, @SuppressWarnings("unchecked") T... objects);

  final void setAutoCommit(Connection connection, boolean autoCommit) {
    Try.runOrThrow(() -> connection.setAutoCommit(autoCommit), Try::rethrow);
  }

  final void commitIfRequired(Connection connection, boolean origAutoCommit) {
    if (origAutoCommit) {
      Try.runOrThrow(() -> connection.commit(), Try::rethrow);
    }
  }


  final void rollbackIfRequired(Connection connection, boolean origAutoCommit) {
    if (!origAutoCommit) {
      Try.runOrThrow(() -> connection.rollback(), Try::rethrow);
    }
  }

  final boolean getAutoCommit(Connection connection) {
    return Try.createSupplierWithThrow(() -> connection.getAutoCommit(), Try::rethrow).get();
  }

  public final int[] batch(SormOptions options, Connection con, String sql,
      Function<T, Object[]> parameterCreator, T[] objects) {
    return execMultiRowProcIfValidObjects(con, objects, nonNullObjects -> {
      int[] result = new int[0];
      boolean origAutoCommit = getAutoCommit(con);

      try (PreparedStatement stmt = con.prepareStatement(sql)) {
        setAutoCommit(con, false);
        final BatchHelper batchHelper = new BatchHelper(batchSize, stmt);
        for (int i = 0; i < objects.length; i++) {
          T obj = objects[i];
          this.sqlParametersSetter.setParameters(options, stmt, parameterCreator.apply(obj));
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
    Optional<LogPoint> lp = loggerContext.createLogPoint(LoggerContext.Category.MULTI_ROW);
    lp.ifPresent(_lp -> _lp.logBeforeMultiRow(con, objects[0].getClass(), objects.length,
        tableMapping.getTableMetaData().getTableName()));


    final int[] result = exec.apply(objects);

    lp.ifPresent(_lp -> _lp.logAfterMultiRow(result));
    return result;
  }


}