package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.sql.Connection;
import org.nkjmlab.sorm4j.internal.mapping.SqlParametersToTableMapping;
import org.nkjmlab.sorm4j.mapping.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.mapping.SqlParametersSetter;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;

public final class SimpleBatchProcessor<T> extends MultiRowProcessor<T> {
  public SimpleBatchProcessor(LoggerContext loggerContext, SqlParametersSetter sqlParametersSetter,
      PreparedStatementSupplier statementSupplier, SqlParametersToTableMapping<T> tableMapping,
      int batchSize) {
    super(loggerContext, sqlParametersSetter, statementSupplier, tableMapping, batchSize);
  }

  @Override
  @SafeVarargs
  public final int[] multiRowInsert(Connection con, T... objects) {
    return batch(con, tableMapping.getSql().getInsertSql(),
        obj -> tableMapping.getInsertParameters(obj), objects);
  }

  @Override
  @SafeVarargs
  public final int[] multiRowMerge(Connection con, T... objects) {
    return batch(con, tableMapping.getSql().getMergeSql(),
        obj -> tableMapping.getMergeParameters(obj), objects);
  }

}
