package org.nkjmlab.sorm4j.table.orm;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.util.sql.SqlKeyword;

public class TableBase<T> implements Table<T>, SqlKeyword {
  private final Sorm orm;
  private final Class<T> valueType;
  private final String tableName;

  /**
   * This table instance is bind to the table by the table name.
   *
   * @param orm
   * @param valueType
   * @param tableName
   */
  public TableBase(Sorm orm, Class<T> valueType, String tableName) {
    this.orm = orm;
    this.valueType = valueType;
    this.tableName = tableName;
  }

  /**
   * This table instance is bind to the table name is mapped to the given {@link valueType}.
   *
   * @param orm
   * @param valueType
   */
  public TableBase(Sorm orm, Class<T> valueType) {
    this(orm, valueType, orm.getTableName(valueType));
  }

  @Override
  public Class<T> getValueType() {
    return valueType;
  }

  @Override
  public String getTableName() {
    return tableName;
  }

  @Override
  public Sorm getOrm() {
    return orm;
  }
}
