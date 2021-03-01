package org.nkjmlab.sorm4j.mapping;

import java.sql.Connection;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.TypedOrmConnection;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.util.Try;

public class OrmConnectionImpl extends OrmMapperImpl implements OrmConnection {

  public OrmConnectionImpl(Connection connection, OrmConfigStore options) {
    super(connection, options);
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
  @Override
  public void commit() {
    Try.runOrThrow(() -> getJdbcConnection().commit(), OrmException::new);
  }

  /**
   * Rollback the {@link java.sql.Connection Connection} associated with this instance.
   *
   * @see java.sql.Connection#rollback()
   * @since 1.0
   */
  @Override
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
  @Override
  public void setAutoCommit(final boolean autoCommit) {
    Try.runOrThrow(() -> getJdbcConnection().setAutoCommit(autoCommit), OrmException::new);
  }

  @Override
  public void begin(int isolationLevel) {
    setAutoCommit(false);
    setTransactionIsolation(isolationLevel);
  }

  @Override
  public void begin() {
    begin(OrmConfigStore.DEFAULT_ISOLATION_LEVEL);
  }

  private void setTransactionIsolation(int isolationLevel) {
    Try.runOrThrow(() -> getJdbcConnection().setTransactionIsolation(isolationLevel),
        OrmException::new);
  }


  @Override
  public <T> TypedOrmConnection<T> toTyped(Class<T> objectClass) {
    return new TypedOrmConnectionImpl<>(objectClass, getJdbcConnection(), getConfigStore());
  }


}
