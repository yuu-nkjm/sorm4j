package org.nkjmlab.sorm4j;

import java.io.Closeable;
import java.sql.Connection;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.util.Try;

public class OrmConnection extends OrmMapper implements Closeable, AutoCloseable {
  OrmConnection(Connection connection, OrmConfigStore options) {
    super(connection, options);
  }

  public static OrmConnection of(Connection conn) {
    return new OrmConnection(conn, OrmConfigStore.DEFAULT_CONFIGURATIONS);
  }

  public static OrmConnection of(Connection connection, OrmConfigStore options) {
    return new OrmConnection(connection, options);
  }

  @Override
  public void close() {
    Try.runOrThrow(() -> {
      if (getJdbcConnection().isClosed()) {
        return;
      }
      getJdbcConnection().close();
    }, OrmException::new);
  }

  /**
   * Commits the {@link java.sql.Connection Connection} associated with this instance.
   *
   * @see java.sql.Connection#commit()
   * @since 1.0
   */
  public void commit() {
    Try.runOrThrow(() -> getJdbcConnection().commit(), OrmException::new);
  }

  /**
   * Rollback the {@link java.sql.Connection Connection} associated with this instance.
   *
   * @see java.sql.Connection#rollback()
   * @since 1.0
   */
  public void rollback() {
    Try.runOrThrow(() -> getJdbcConnection().rollback(), OrmException::new);
  }


  /**
   * Sets the auto commit behavior for the {@link java.sql.Connection Connection} associated with
   * this instance.
   *
   * @see java.sql.Connection#setAutoCommit(boolean)
   * @since 1.0
   */
  public void setAutoCommit(final boolean autoCommit) {
    Try.runOrThrow(() -> getJdbcConnection().setAutoCommit(autoCommit), OrmException::new);
  }

  public void begin(int isolationLevel) {
    setAutoCommit(false);
    setTransactionIsolation(isolationLevel);
  }

  public void begin() {
    begin(OrmConfigStore.DEFAULT_ISOLATION_LEVEL);
  }

  private void setTransactionIsolation(int isolationLevel) {
    Try.runOrThrow(() -> getJdbcConnection().setTransactionIsolation(isolationLevel),
        OrmException::new);
  }


  @Override
  public <T> TypedOrmConnection<T> toTyped(Class<T> objectClass) {
    return new TypedOrmConnection<>(objectClass, getJdbcConnection(), getConfigStore());
  }


}
