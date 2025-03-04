package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.sql.Connection;
import org.nkjmlab.sorm4j.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.context.logging.LogContext;
import org.nkjmlab.sorm4j.internal.mapping.SqlParametersToTableMapping;

final class SimpleBatchProcessor<T> extends MultiRowProcessor<T> {
  public SimpleBatchProcessor(
      LogContext loggerContext,
      SqlParametersSetter sqlParametersSetter,
      PreparedStatementSupplier statementSupplier,
      SqlParametersToTableMapping<T> tableMapping,
      int batchSize) {
    super(loggerContext, sqlParametersSetter, statementSupplier, tableMapping, batchSize);
  }

  @Override
  public final int[] multiRowInsert(Connection con, T[] objects) {
    return batch(con, getSql().getInsertSql(), obj -> getInsertParameters(obj), objects);
  }

  @Override
  public final int[] multiRowMerge(Connection con, T[] objects) {
    return batch(con, getSql().getMergeSql(), obj -> getMergeParameters(obj), objects);
  }
}
