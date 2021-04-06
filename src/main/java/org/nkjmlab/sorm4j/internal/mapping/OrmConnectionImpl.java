package org.nkjmlab.sorm4j.internal.mapping;

import java.sql.Connection;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.TypedOrmConnection;
import org.nkjmlab.sorm4j.internal.sql.NamedParameterQueryImpl;
import org.nkjmlab.sorm4j.internal.sql.OrderedParameterQueryImpl;
import org.nkjmlab.sorm4j.internal.sql.QueryOrmExecutor;
import org.nkjmlab.sorm4j.internal.sql.SelectQueryImpl;
import org.nkjmlab.sorm4j.sql.NamedParameterQuery;
import org.nkjmlab.sorm4j.sql.NamedParameterRequest;
import org.nkjmlab.sorm4j.sql.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sql.OrderedParameterRequest;
import org.nkjmlab.sorm4j.sql.SelectQuery;

/**
 * A database connection with object-relation mapping function. The main class for the ORMapper
 * engine.
 *
 * This instance wraps a {@link java.sql.Connection} object. OrmMapper instances are not thread
 * safe, in particular because {@link java.sql.Connection} objects are not thread safe.
 *
 * @author nkjm
 *
 */
public class OrmConnectionImpl extends OrmMapperImpl implements OrmConnection {

  public OrmConnectionImpl(Connection connection, ConfigStore options) {
    super(connection, options);
  }

  @Override
  public <T> NamedParameterQuery<T> createNamedParameterQuery(Class<T> objectClass, String sql) {
    return NamedParameterQueryImpl.createFrom(new QueryOrmExecutor<>(this, objectClass), sql);
  }

  @Override
  public NamedParameterRequest createNamedParameterRequest(String sql) {
    return NamedParameterRequest.from(this, sql);
  }

  @Override
  public <T> OrderedParameterQuery<T> createOrderedParameterQuery(Class<T> objectClass,
      String sql) {
    return OrderedParameterQueryImpl.createFrom(new QueryOrmExecutor<>(this, objectClass), sql);
  }

  @Override
  public OrderedParameterRequest createOrderedParameterRequest(String sql) {
    return OrderedParameterRequest.from(this, sql);
  }

  @Override
  public <T> SelectQuery<T> createSelectQuery(Class<T> objectClass) {
    SelectQueryImpl<T> ret = new SelectQueryImpl<T>(new QueryOrmExecutor<>(this, objectClass));
    ret.from(getTableName(objectClass));
    return ret;
  }


  @Override
  public <S> TypedOrmConnection<S> type(Class<S> objectClass) {
    return new TypedOrmConnectionImpl<>(objectClass, this);
  }



}
