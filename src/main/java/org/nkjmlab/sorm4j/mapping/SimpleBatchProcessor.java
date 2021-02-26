package org.nkjmlab.sorm4j.mapping;

import java.sql.Connection;

public final class SimpleBatchProcessor<T> extends MultiRowProcessor<T> {
  public SimpleBatchProcessor(TableMapping<T> tableMapping, int batchSize) {
    super(tableMapping, batchSize);
  }

  @Override
  public int[] multiRowInsert(Connection con, T... objects) {
    return batch(con, tableMapping.getSql().getInsertSql(),
        obj -> tableMapping.getInsertParameters(obj), objects);
  }

  @Override
  public int[] multiRowMerge(Connection con, T... objects) {
    return batch(con, tableMapping.getSql().getMergeSql(),
        obj -> tableMapping.getMergeParameters(obj), objects);
  }

}
