package org.nkjmlab.sorm4j;

import java.sql.Connection;

import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.SormContextImpl;
import org.nkjmlab.sorm4j.internal.SormImpl;
import org.nkjmlab.sorm4j.table.TableConnection;

/** @author yuu_nkjm */
public interface OrmConnection extends Orm, AutoCloseable {

  /**
   * Create a {@link OrmConnection} wrapping the given JDBC Connection.
   *
   * <p>You should always use try-with-resources block to ensure the database connection is
   * released.
   *
   * @param connection
   * @return
   */
  static OrmConnection of(Connection connection) {
    return of(connection, SormImpl.DEFAULT_CONTEXT);
  }

  /**
   * Create a {@link OrmConnection} wrapping the given JDBC Connection and the given context.
   *
   * <p>You should always use try-with-resources block to ensure the database connection is
   * released.
   *
   * @param connection
   * @param sormContext
   * @return
   */
  static OrmConnection of(Connection connection, SormContext sormContext) {
    return new OrmConnectionImpl(connection, SormContextImpl.class.cast(sormContext));
  }

  /**
   * Gets JDBC {@link Connection}.
   *
   * @return
   */
  Connection getJdbcConnection();

  /**
   * Closes the {@link java.sql.Connection Connection} associated with this instance.
   *
   * @see java.sql.Connection#close()
   */
  @Override
  void close();

  /**
   * Commits the {@link java.sql.Connection Connection} associated with this instance.
   *
   * @see java.sql.Connection#commit()
   */
  void commit();

  /**
   * Roll back the {@link java.sql.Connection Connection} associated with this instance.
   *
   * @see java.sql.Connection#rollback()
   */
  void rollback();

  /**
   * Sets the auto commit behavior for the {@link java.sql.Connection Connection} associated with
   * this instance.
   *
   * @see java.sql.Connection#setAutoCommit(boolean)
   */
  void setAutoCommit(boolean autoCommit);

  /**
   * Gets a {@link TableConnection} instance indicated the given parameter. The table name
   * is guessed from the given type.
   *
   * @param <T>
   * @param type
   * @return
   */
  <T> TableConnection<T> mapToTable(Class<T> type);

  /**
   * Gets a {@link TableConnection} instance indicated the given parameters.
   *
   * @param <T>
   * @param type
   * @param tableName
   * @return
   */
  <T> TableConnection<T> mapToTable(Class<T> type, String tableName);
}
