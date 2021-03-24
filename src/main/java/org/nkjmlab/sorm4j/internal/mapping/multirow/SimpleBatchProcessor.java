package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.sql.Connection;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.internal.mapping.TableMapping;

final class SimpleBatchProcessor<T> extends MultiRowProcessor<T> {
  public SimpleBatchProcessor(SqlParameterSetter sqlParameterSetter, TableMapping<T> tableMapping,
      int batchSize) {
    super(sqlParameterSetter, tableMapping, batchSize);
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
