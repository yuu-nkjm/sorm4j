package org.nkjmlab.sorm4j.core.mapping;

import java.sql.Connection;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.TypedOrmConnection;
import org.nkjmlab.sorm4j.core.sqlstatement.NamedParameterQueryImpl;
import org.nkjmlab.sorm4j.core.sqlstatement.OrderedParameterQueryImpl;
import org.nkjmlab.sorm4j.core.sqlstatement.SelectQueryImpl;
import org.nkjmlab.sorm4j.core.sqlstatement.QueryTypedOrmExecutor;
import org.nkjmlab.sorm4j.core.util.Try;
import org.nkjmlab.sorm4j.sql.NamedParameterQuery;
import org.nkjmlab.sorm4j.sql.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sql.SelectQuery;

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


  public TypedOrmConnectionImpl(Class<T> objectClass, Connection connection, ConfigStore options) {
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
  public void begin(int transactionIsolationLevel) {
    setAutoCommit(false);
    setTransactionIsolation(transactionIsolationLevel);
  }

  @Override
  public void begin() {
    begin(getTransactionIsolationLevel());
  }

  private void setTransactionIsolation(int level) {
    Try.runOrThrow(() -> getJdbcConnection().setTransactionIsolation(level), Try::rethrow);
  }

  @Override
  public SelectQuery<T> createSelectQuery() {
    SelectQueryImpl<T> ret = new SelectQueryImpl<T>(new QueryTypedOrmExecutor<>(this));
    ret.from(getTableName());
    return ret;
  }

  @Override
  public NamedParameterQuery<T> createNamedParameterQuery(String sql) {
    return NamedParameterQueryImpl.createFrom(new QueryTypedOrmExecutor<>(this), sql);
  }

  @Override
  public OrderedParameterQuery<T> createOrderedParameterQuery(String sql) {
    return OrderedParameterQueryImpl.createFrom(new QueryTypedOrmExecutor<>(this), sql);
  }

  @Override
  public <S> TypedOrmConnection<S> type(Class<S> objectClass) {
    return new TypedOrmConnectionImpl<>(objectClass, getJdbcConnection(), getConfigStore());
  }

  @Override
  public OrmConnection untype() {
    return new OrmConnectionImpl(getJdbcConnection(), getConfigStore());
  }



}
