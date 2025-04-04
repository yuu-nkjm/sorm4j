package org.nkjmlab.sorm4j.table.orm;

import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.internal.TableConnectionImpl;

public interface TableConnection<T> extends TableOrm<T>, AutoCloseable {

  /**
   * Gets {@link OrmConnection} object
   *
   * @return
   */
  @Override
  OrmConnection getOrm();

  /**
   * Gets a {@link TableConnection} wrapping the given JDBC Connection and the given context.
   *
   * @param <T>
   * @param connection
   * @param valueType
   * @return
   */
  static <T> TableConnection<T> of(OrmConnection connection, Class<T> valueType) {
    return of(connection, valueType, connection.getTableName(valueType));
  }

  /**
   * Gets a {@link TableConnection} wrapping the given JDBC Connection and the given context.
   *
   * @param <T>
   * @param connection
   * @param valueType
   * @param tableName
   * @return
   */
  static <T> TableConnection<T> of(OrmConnection connection, Class<T> valueType, String tableName) {
    return new TableConnectionImpl<T>(connection, valueType, tableName);
  }

  @Override
  void close();
}
