package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.sql.Connection;

import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext;
import org.nkjmlab.sorm4j.internal.mapping.SqlParametersToTableMapping;

public final class SimpleBatchProcessor<T> extends MultiRowProcessor<T> {
  public SimpleBatchProcessor(LoggerContext loggerContext, 
      SqlParametersSetter sqlParametersSetter, SqlParametersToTableMapping<T> tableMapping, int batchSize) {
    super(loggerContext, sqlParametersSetter, tableMapping, batchSize);
  }

  @Override
  @SafeVarargs
  public final int[] multiRowInsert(Connection con, T... objects) {
    return batch(con, tableMapping.getSql().getInsertSql(), obj -> tableMapping.getInsertParameters(obj),
        objects);
  }

  @Override
  @SafeVarargs
  public final int[] multiRowMerge(Connection con, T... objects) {
    return batch(con, tableMapping.getSql().getMergeSql(), obj -> tableMapping.getMergeParameters(obj),
        objects);
  }

}
