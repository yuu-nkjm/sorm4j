package org.nkjmlab.sorm4j.util.h2.table;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.table.TableBase;

public abstract class H2TableBase<T> extends TableBase<T> implements H2Table<T> {
  public H2TableBase(Sorm orm, Class<T> valueType) {
    super(orm, valueType);
  }

  public H2TableBase(Sorm orm, Class<T> valueType, String tableName) {
    super(orm, valueType, tableName);
  }
}
