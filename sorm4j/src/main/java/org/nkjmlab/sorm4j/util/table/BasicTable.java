package org.nkjmlab.sorm4j.util.table;

import java.sql.Connection;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.util.sql.SqlKeyword;

public class BasicTable<T> implements Table<T>, SqlKeyword {

  private final String tableName;
  private final Orm orm;
  private final Class<T> valueType;

  public BasicTable(Orm orm, Class<T> valueType, String tableName) {
    this.orm = orm;
    this.valueType = valueType;
    this.tableName = tableName;
  }

  /**
   * This table instance is bind to the table name is mapped to the given {@link valueType}.
   *
   * @param orm
   * @param valueType
   *
   */
  public BasicTable(Orm orm, Class<T> valueType) {
    this(orm, valueType, orm.getTableName(valueType));
  }

  /**
   * This table instance is bind to the table name is mapped to the given {@link valueType}.
   *
   * @param dataSource
   * @param valueType
   */
  public BasicTable(DataSource dataSource, Class<T> valueType) {
    this(Sorm.create(dataSource), valueType);
  }

  /**
   * This table instance is bind to the table name is mapped to the given {@link valueType}.
   *
   * @param connection
   * @param valueType
   */
  public BasicTable(Connection connection, Class<T> valueType) {
    this(OrmConnection.of(connection), valueType);
  }

  /**
   * This table instance is bind to the given table name.
   *
   * @param dataSouce
   * @param valueType
   * @param tableName
   */
  public BasicTable(DataSource dataSouce, Class<T> valueType, String tableName) {
    this(Sorm.create(dataSouce), valueType);
  }

  /**
   * This table instance is bind to the given table name.
   *
   * @param connection
   * @param valueType
   * @param tableName
   */
  public BasicTable(Connection connection, Class<T> valueType, String tableName) {
    this(OrmConnection.of(connection), valueType);
  }

  @Override
  public Class<T> getValueType() {
    return valueType;
  }

  @Override
  public Orm getOrm() {
    return orm;
  }

  @Override
  public String getTableName() {
    return tableName;
  }

}
