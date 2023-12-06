package org.nkjmlab.sorm4j.util.h2;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.table.SimpleTable;

@Experimental
public class SimpleH2Table<T> extends SimpleTable<T> implements H2Table<T> {

  /**
   * A table instance binded to the table by the value type.
   *
   * @param sorm
   * @param valueType
   */
  public SimpleH2Table(Sorm orm, Class<T> valueType) {
    super(orm, valueType);
  }

  /**
   * A table instance binded to the table by the table name.
   *
   * @param orm
   * @param valueType
   * @param tableName
   */
  public SimpleH2Table(Sorm orm, Class<T> valueType, String tableName) {
    super(orm, valueType, tableName);
  }
}
