package org.nkjmlab.sorm4j.core;

import java.sql.Connection;

final class SimpleBatchProcessor<T> extends MultiRowProcessor<T> {
  public SimpleBatchProcessor(TableMapping<T> tableMapping, int batchSize) {
    super(tableMapping, batchSize);
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
