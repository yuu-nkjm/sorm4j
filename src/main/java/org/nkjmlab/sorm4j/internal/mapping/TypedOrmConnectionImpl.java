package org.nkjmlab.sorm4j.internal.mapping;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.TypedOrmConnection;
import org.nkjmlab.sorm4j.internal.sql.NamedParameterQueryImpl;
import org.nkjmlab.sorm4j.internal.sql.OrderedParameterQueryImpl;
import org.nkjmlab.sorm4j.internal.sql.QueryTypedOrmExecutor;
import org.nkjmlab.sorm4j.internal.sql.SelectQueryImpl;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.NamedParameterQuery;
import org.nkjmlab.sorm4j.sql.NamedParameterRequest;
import org.nkjmlab.sorm4j.sql.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sql.OrderedParameterRequest;
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
  // private static final org.slf4j.Logger log =
  // org.nkjmlab.sorm4j.internal.util.LoggerFactory.getLogger();

  public TypedOrmConnectionImpl(Class<T> objectClass, OrmConnectionImpl ormMapper) {
    super(objectClass, ormMapper);
  }

  @Override
  public String getTableName() {
    return ormConnection.getTableName(objectClass);
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
    begin(ormConnection.getTransactionIsolationLevel());
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
    return new TypedOrmConnectionImpl<>(objectClass, ormConnection);
  }

  @Override
  public OrmConnection untype() {
    return ormConnection;
  }

  @Override
  public <S> S mapRow(Class<S> objectClass, ResultSet resultSet) {
    return ormConnection.mapRow(objectClass, resultSet);
  }

  @Override
  public Map<String, Object> mapRow(ResultSet resultSet) {
    return ormConnection.mapRow(resultSet);
  }

  @Override
  public <S> List<S> mapRows(Class<S> objectClass, ResultSet resultSet) {
    return ormConnection.mapRows(objectClass, resultSet);
  }

  @Override
  public List<Map<String, Object>> mapRows(ResultSet resultSet) {
    return ormConnection.mapRows(resultSet);
  }

  @Override
  public NamedParameterRequest createNamedParameterRequest(String sql) {
    return NamedParameterRequest.from(this, sql);
  }

  @Override
  public OrderedParameterRequest createOrderedParameterRequest(String sql) {
    return OrderedParameterRequest.from(this, sql);
  }

}
