package org.nkjmlab.sorm4j.internal;

import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.table.TableMappedOrmConnection;

public class TableMappedOrmConnectionImpl<T> implements TableMappedOrmConnection<T> {

  private final String tableName;
  private final OrmConnection ormConn;
  private final Class<T> valueType;

  public TableMappedOrmConnectionImpl(OrmConnection ormConn, Class<T> valueType, String tableName) {
    this.ormConn = ormConn;
    this.valueType = valueType;
    this.tableName = tableName;
  }

  /**
   * This table instance is bind to the table name is mapped to the given {@link valueType}.
   *
   * @param sorm
   * @param valueType
   *
   */
  public TableMappedOrmConnectionImpl(OrmConnection ormConn, Class<T> valueType) {
    this(ormConn, valueType, ormConn.getTableName(valueType));
  }

  @Override
  public OrmConnection getOrm() {
    return ormConn;
  }

  @Override
  public String getTableName() {
    return tableName;
  }

  @Override
  public Class<T> getValueType() {
    return valueType;
  }

}
