package org.nkjmlab.sorm4j.internal.table.orm;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.table.orm.TableBase;

public final class SimpleTable<T> extends TableBase<T> {

  public SimpleTable(Sorm orm, Class<T> valueType) {
    super(orm, valueType);
  }

  public SimpleTable(Sorm orm, Class<T> valueType, String tableName) {
    super(orm, valueType, tableName);
  }
}
