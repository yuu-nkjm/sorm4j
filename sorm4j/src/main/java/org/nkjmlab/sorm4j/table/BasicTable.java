package org.nkjmlab.sorm4j.table;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.util.sql.SqlKeyword;

public class BasicTable<T> implements Table<T>, SqlKeyword {

  private final Sorm sorm;
  private final Class<T> valueType;
  private final String tableName;

  public BasicTable(Sorm sorm, Class<T> valueType, String tableName) {
    this.sorm = sorm;
    this.valueType = valueType;
    this.tableName = tableName;
  }

  /**
   * This table instance is bind to the table name is mapped to the given {@link valueType}.
   *
   * @param sorm
   * @param valueType
   */
  public BasicTable(Sorm sorm, Class<T> valueType) {
    this(sorm, valueType, sorm.getTableName(valueType));
  }

  @Override
  public Class<T> getValueType() {
    return valueType;
  }

  @Override
  public Sorm getOrm() {
    return sorm;
  }

  @Override
  public String getTableName() {
    return tableName;
  }

}
