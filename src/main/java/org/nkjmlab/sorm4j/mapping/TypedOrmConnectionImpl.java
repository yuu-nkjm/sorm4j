package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.config.OrmConfigStore.*;
import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Function;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.TypedOrmConnection;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.util.Try;

public class TypedOrmConnectionImpl<T> extends TypedOrmMapperImpl<T>
    implements TypedOrmConnection<T> {
  // private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();


  public TypedOrmConnectionImpl(Class<T> objectClass, Connection connection,
      OrmConfigStore options) {
    super(objectClass, connection, options);
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

  @Override
  public void commit() {
    Try.runOrThrow(() -> getJdbcConnection().commit(), OrmException::new);
  }

  @Override
  public void rollback() {
    Try.runOrThrow(() -> getJdbcConnection().rollback(), OrmException::new);
  }

  @Override
  public void setAutoCommit(final boolean autoCommit) {
    Try.runOrThrow(() -> getJdbcConnection().setAutoCommit(autoCommit), OrmException::new);
  }

  @Override
  public void begin(int isolationLevel) {
    setAutoCommit(false);
    setTransactionIsolation(isolationLevel);
  }

  public void runTransaction(Consumer<TypedOrmConnection<T>> handler) {
    setAutoCommit(false);
    setTransactionIsolation(DEFAULT_ISOLATION_LEVEL);
    handler.accept(this);
    rollback();
  }

  public <R> R executeTransaction(Function<TypedOrmConnection<T>, R> handler) {
    setAutoCommit(false);
    setTransactionIsolation(DEFAULT_ISOLATION_LEVEL);
    R ret = handler.apply(this);
    rollback();
    return ret;
  }


  @Override
  public void begin() {
    begin(DEFAULT_ISOLATION_LEVEL);
  }

  private void setTransactionIsolation(int isolationLevel) {
    Try.runOrThrow(() -> getJdbcConnection().setTransactionIsolation(isolationLevel),
        OrmException::new);
  }


  @Override
  public OrmConnection toUntyped() {
    return OrmConnection.of(getJdbcConnection(), getConfigStore());
  }

}
