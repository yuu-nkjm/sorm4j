package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.config.OrmConfigStore.*;
import java.io.Closeable;
import java.sql.Connection;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.util.Try;

public class TypedOrmConnection<T> extends TypedOrmMapper<T> implements Closeable, AutoCloseable {
  // private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();


  TypedOrmConnection(Class<T> objectClass, Connection connection, OrmConfigStore options) {
    super(objectClass, connection, options);
  }

  public static <T> TypedOrmConnection<T> of(Class<T> objectClass, Connection conn) {
    return new TypedOrmConnection<T>(objectClass, conn, DEFAULT_CONFIGURATIONS);
  }

  public static <T> TypedOrmConnection<T> of(Class<T> objectClass, Connection connection,
      OrmConfigStore options) {
    return new TypedOrmConnection<T>(objectClass, connection, options);
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
   * Rolls back the {@link java.sql.Connection Connection} associated with this instance.
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
    begin(DEFAULT_ISOLATION_LEVEL);
  }

  private void setTransactionIsolation(int isolationLevel) {
    Try.runOrThrow(() -> getJdbcConnection().setTransactionIsolation(isolationLevel),
        OrmException::new);
  }


  @Override
  public OrmConnection toUntyped() {
    return new OrmConnection(getJdbcConnection(), getConfigStore());
  }


}
