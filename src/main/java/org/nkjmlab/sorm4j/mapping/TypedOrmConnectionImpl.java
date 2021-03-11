package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.mapping.OrmConfigStore.*;
import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Function;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.TypedOrmConnection;
import org.nkjmlab.sorm4j.sqlstatement.NamedParameterQuery;
import org.nkjmlab.sorm4j.sqlstatement.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sqlstatement.SelectQuery;
import org.nkjmlab.sorm4j.util.Try;

/**
 * A database connection with object-relation mapping function with type. The main class for the
 * ORMapper engine.
 *
 * This instance wraps a {@link java.sql.Connection} object. OrmMapper instances are not thread
 * safe, in particular because {@link java.sql.Connection} objects are not thread safe.
 *
 * @author nkjm
 *
 */

public class TypedOrmConnectionImpl<T> extends TypedOrmMapperImpl<T>
    implements TypedOrmConnection<T> {
  // private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();


  public TypedOrmConnectionImpl(Class<T> objectClass, Connection connection,
      OrmConfigStore options) {
    super(objectClass, connection, options);
  }

  @Override
  public String getTableName() {
    return getTableMapping(objectClass).getTableName();
  }

  @Override
  public void close() {
    Try.runOrThrow(() -> {
      if (getJdbcConnection().isClosed()) {
        return;
      }
      getJdbcConnection().close();
    }, Try::rethrow);
  }

  @Override
  public void commit() {
    Try.runOrThrow(() -> getJdbcConnection().commit(), Try::rethrow);
  }

  @Override
  public void rollback() {
    Try.runOrThrow(() -> getJdbcConnection().rollback(), Try::rethrow);
  }

  @Override
  public void setAutoCommit(final boolean autoCommit) {
    Try.runOrThrow(() -> getJdbcConnection().setAutoCommit(autoCommit), Try::rethrow);
  }

  @Override
  public void begin(int isolationLevel) {
    setAutoCommit(false);
    setTransactionIsolation(isolationLevel);
  }

  @Override
  public void runTransaction(Consumer<TypedOrmConnection<T>> handler) {
    setAutoCommit(false);
    setTransactionIsolation(DEFAULT_ISOLATION_LEVEL);
    handler.accept(this);
    rollback();
  }

  @Override
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
        Try::rethrow);
  }

  @Override
  public SelectQuery<T> createSelectQuery() {
    return new SelectQuery<T>(this);
  }

  @Override
  public NamedParameterQuery<T> createNamedParametersQuery(String sql) {
    return NamedParameterQuery.createFrom(this, sql);
  }

  @Override
  public OrderedParameterQuery<T> createOrderedParametersQuery(String sql) {
    return OrderedParameterQuery.createFrom(this, sql);
  }



}
