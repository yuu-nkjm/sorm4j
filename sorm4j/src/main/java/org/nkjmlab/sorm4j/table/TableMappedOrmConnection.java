package org.nkjmlab.sorm4j.table;

import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.TableMappedOrmConnectionImpl;

@Experimental
public interface TableMappedOrmConnection<T> extends TableMappedOrm<T> {

  /**
   * Gets OrmConnection object
   *
   * @return
   */
  @Override
  OrmConnection getOrm();

  /**
   * Gets a {@link TableMappedOrmConnection} wrapping the given JDBC Connection and the given
   * context.
   *
   * @param <T>
   * @param connection
   * @param valueType
   * @return
   */
  static <T> TableMappedOrmConnection<T> of(OrmConnection connection, Class<T> valueType) {
    return new TableMappedOrmConnectionImpl<T>(connection, valueType);
  }

  /**
   * Gets a {@link TableMappedOrmConnection} wrapping the given JDBC Connection and the given
   * context.
   *
   * @param <T>
   * @param connection
   * @param valueType
   * @param tableName
   * @return
   */
  static <T> TableMappedOrmConnection<T> of(
      OrmConnection connection, Class<T> valueType, String tableName) {
    return new TableMappedOrmConnectionImpl<T>(connection, valueType, tableName);
  }
}
