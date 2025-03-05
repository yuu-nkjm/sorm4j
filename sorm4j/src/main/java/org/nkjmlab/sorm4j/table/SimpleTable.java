package org.nkjmlab.sorm4j.table;

import org.nkjmlab.sorm4j.Sorm;

public final class SimpleTable<T> extends TableBase<T> {

  public SimpleTable(Sorm orm, Class<T> valueType) {
    super(orm, valueType);
  }

  public SimpleTable(Sorm orm, Class<T> valueType, String tableName) {
    super(orm, valueType, tableName);
  }
}
