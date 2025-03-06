package org.nkjmlab.sorm4j.extension.h2.orm.table;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.table.orm.TableBase;

public abstract class H2TableBase<T> extends TableBase<T> implements H2Table<T> {
  public H2TableBase(Sorm orm, Class<T> valueType) {
    super(orm, valueType);
  }

  public H2TableBase(Sorm orm, Class<T> valueType, String tableName) {
    super(orm, valueType, tableName);
  }

  static final class H2SimpleTable<T> extends H2TableBase<T> {

    public H2SimpleTable(Sorm orm, Class<T> valueType) {
      super(orm, valueType);
    }

    public H2SimpleTable(Sorm orm, Class<T> valueType, String tableName) {
      super(orm, valueType, tableName);
    }
  }
}
