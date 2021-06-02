package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.sql.Connection;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext;
import org.nkjmlab.sorm4j.internal.mapping.TableMapping;

final class SimpleBatchProcessor<T> extends MultiRowProcessor<T> {
  public SimpleBatchProcessor(LoggerContext loggerContext, SormOptions options,
      SqlParametersSetter sqlParametersSetter, TableMapping<T> tableMapping, int batchSize) {
    super(loggerContext, options, sqlParametersSetter, tableMapping, batchSize);
  }

  @Override
  @SafeVarargs
  public final int[] multiRowInsert(Connection con, T... objects) {
    return batch(options, con, tableMapping.getSql().getInsertSql(),
        obj -> tableMapping.getInsertParameters(obj), objects);
  }

  @Override
  @SafeVarargs
  public final int[] multiRowMerge(Connection con, T... objects) {
    return batch(options, con, tableMapping.getSql().getMergeSql(),
        obj -> tableMapping.getMergeParameters(obj), objects);
  }

}
