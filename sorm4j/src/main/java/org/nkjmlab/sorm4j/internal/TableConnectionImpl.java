package org.nkjmlab.sorm4j.internal;

import java.sql.SQLException;

import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.table.TableConnection;

public class TableConnectionImpl<T> implements TableConnection<T> {

  private final OrmConnection ormConn;
  private final Class<T> valueType;
  private final String tableName;

  /**
   * This table instance is bind to the table name is mapped to the given {@link valueType}.
   *
   * @param ormConn
   * @param valueType
   * @param tableName
   */
  public TableConnectionImpl(OrmConnection ormConn, Class<T> valueType, String tableName) {
    this.ormConn = ormConn;
    this.valueType = valueType;
    this.tableName = tableName;
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

  @Override
  public void close() throws Exception {
    try {
      getOrm().getJdbcConnection().close();
    } catch (SQLException e) {
      getOrm()
          .getContext()
          .getLoggerContext()
          .getLogger(TableConnectionImpl.class)
          .warn("jdbc connection close error");
    }
  }
}
