package org.nkjmlab.sorm4j.internal.mapping;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.TypedOrmConnection;
import org.nkjmlab.sorm4j.internal.sql.NamedParameterQueryImpl;
import org.nkjmlab.sorm4j.internal.sql.OrderedParameterQueryImpl;
import org.nkjmlab.sorm4j.internal.sql.QueryTypedOrmExecutor;
import org.nkjmlab.sorm4j.internal.sql.SelectQueryImpl;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.InsertResult;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.NamedParameterQuery;
import org.nkjmlab.sorm4j.sql.NamedParameterRequest;
import org.nkjmlab.sorm4j.sql.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sql.OrderedParameterRequest;
import org.nkjmlab.sorm4j.sql.SelectQuery;
import org.nkjmlab.sorm4j.sql.SqlStatement;

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

public class TypedOrmConnectionImpl<T> implements TypedOrmConnection<T> {
  // private static final org.slf4j.Logger log =
  // org.nkjmlab.sorm4j.internal.util.LoggerFactory.getLogger();

  protected Class<T> objectClass;
  protected OrmConnectionImpl ormConnection;

  public TypedOrmConnectionImpl(Class<T> objectClass, OrmConnectionImpl ormMapper) {
    this.ormConnection = ormMapper;
    this.objectClass = objectClass;
  }

  @Override
  public void begin() {
    ormConnection.begin();
  }

  @Override
  public void begin(int transactionIsolationLevel) {
    setAutoCommit(false);
    setTransactionIsolation(transactionIsolationLevel);
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
  public NamedParameterQuery<T> createNamedParameterQuery(String sql) {
    return NamedParameterQueryImpl.createFrom(new QueryTypedOrmExecutor<>(this), sql);
  }

  @Override
  public NamedParameterRequest createNamedParameterRequest(String sql) {
    return NamedParameterRequest.from(this, sql);
  }


  @Override
  public OrderedParameterQuery<T> createOrderedParameterQuery(String sql) {
    return OrderedParameterQueryImpl.createFrom(new QueryTypedOrmExecutor<>(this), sql);
  }


  @Override
  public OrderedParameterRequest createOrderedParameterRequest(String sql) {
    return OrderedParameterRequest.from(this, sql);
  }



  @Override
  public SelectQuery<T> createSelectQuery() {
    SelectQueryImpl<T> ret = new SelectQueryImpl<T>(new QueryTypedOrmExecutor<>(this));
    ret.from(getTableName());
    return ret;
  }


  @Override
  public int[] delete(List<T> objects) {
    return ormConnection.delete(objects);
  }



  @Override
  public int delete(T object) {
    return ormConnection.delete(object);
  }


  @Override
  public int[] delete(@SuppressWarnings("unchecked") T... objects) {
    return ormConnection.delete(objects);
  }


  @Override
  public int deleteAll() {
    return ormConnection.deleteAll(objectClass);
  }


  @Override
  public int deleteAllOn(String tableName) {
    return ormConnection.deleteAllOn(tableName);
  }


  @Override
  public int[] deleteOn(String tableName, List<T> objects) {
    return ormConnection.deleteOn(tableName, objects);
  }


  @Override
  public int deleteOn(String tableName, T object) {
    return ormConnection.deleteOn(tableName, object);
  }


  @Override
  public int[] deleteOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return ormConnection.deleteOn(tableName, objects);
  }

  @Override
  public <S> S executeQuery(SqlStatement sql, FunctionHandler<ResultSet, S> resultSetHandler) {
    return ormConnection.executeQuery(sql, resultSetHandler);
  }



  @Override
  public <S> List<S> executeQuery(SqlStatement sql, RowMapper<S> rowMapper) {
    return ormConnection.executeQuery(sql, rowMapper);
  }


  @Override
  public int executeUpdate(SqlStatement sql) {
    return ormConnection.executeUpdate(sql);
  }

  @Override
  public int executeUpdate(String sql, Object... parameters) {
    return ormConnection.executeUpdate(sql, parameters);
  }


  @Override
  public Connection getJdbcConnection() {
    return ormConnection.getJdbcConnection();
  }



  @Override
  public String getTableName() {
    return ormConnection.getTableName(objectClass);
  }


  @Override
  public int[] insert(List<T> objects) {
    return ormConnection.insert(objects);
  }


  @Override
  public int insert(T object) {
    return ormConnection.insert(object);
  }


  @Override
  public int[] insert(@SuppressWarnings("unchecked") T... objects) {
    return ormConnection.insert(objects);
  }


  @Override
  public InsertResult<T> insertAndGet(List<T> objects) {
    return ormConnection.insertAndGet(objects);
  }



  @Override
  public InsertResult<T> insertAndGet(T object) {
    return ormConnection.insertAndGet(object);
  }


  @Override
  public InsertResult<T> insertAndGet(@SuppressWarnings("unchecked") T... objects) {
    return ormConnection.insertAndGet(objects);
  }


  @Override
  public InsertResult<T> insertAndGetOn(String tableName, List<T> objects) {
    return ormConnection.insertAndGetOn(tableName, objects);
  }


  @Override
  public InsertResult<T> insertAndGetOn(String tableName, T object) {
    return ormConnection.insertAndGetOn(tableName, object);
  }


  @Override
  public InsertResult<T> insertAndGetOn(String tableName,
      @SuppressWarnings("unchecked") T... objects) {
    return ormConnection.insertAndGetOn(tableName, objects);
  }


  @Override
  public int[] insertOn(String tableName, List<T> objects) {
    return ormConnection.insertOn(tableName, objects);
  }


  @Override
  public int insertOn(String tableName, T object) {
    return ormConnection.insertOn(tableName, object);
  }


  @Override
  public int[] insertOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return ormConnection.insertOn(tableName, objects);
  }


  @Override
  public T mapRow(ResultSet resultSet) {
    return Try.getOrThrow(() -> ormConnection.mapRowAux(objectClass, resultSet), Try::rethrow);
  }


  @Override
  public List<T> mapRows(ResultSet resultSet) {
    return Try.getOrThrow(() -> ormConnection.mapRowsAux(objectClass, resultSet), Try::rethrow);
  }

  @Override
  public List<Map<String, Object>> mapRowsToMapList(ResultSet resultSet) {
    return Try.getOrThrow(() -> ormConnection.mapRowsAux(resultSet), Try::rethrow);
  }

  @Override
  public Map<String, Object> mapRowToMap(ResultSet resultSet) {
    return Try.getOrThrow(() -> ormConnection.mapRowAux(resultSet), Try::rethrow);
  }

  @Override
  public int[] merge(List<T> objects) {
    return ormConnection.merge(objects);
  }

  @Override
  public int merge(T object) {
    return ormConnection.merge(object);
  }

  @Override
  public int[] merge(@SuppressWarnings("unchecked") T... objects) {
    return ormConnection.merge(objects);
  }

  @Override
  public int[] mergeOn(String tableName, List<T> objects) {
    return ormConnection.mergeOn(tableName, objects);
  }

  @Override
  public int mergeOn(String tableName, T object) {
    return ormConnection.mergeOn(tableName, object);
  }

  @Override
  public int[] mergeOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return ormConnection.mergeOn(tableName, objects);
  }

  @Override
  public final List<T> readAll() {
    return ormConnection.readAll(objectClass);
  }

  @Override
  public LazyResultSet<T> readAllLazy() {
    return ormConnection.readAllLazy(objectClass);
  }

  @Override
  public T readByPrimaryKey(Object... primaryKeyValues) {
    return ormConnection.readByPrimaryKey(objectClass, primaryKeyValues);
  }

  @Override
  public T readFirst(SqlStatement sql) {
    return readFirst(sql.getSql(), sql.getParameters());
  }

  @Override
  public T readFirst(String sql, Object... parameters) {
    return ormConnection.readFirst(objectClass, sql, parameters);
  }

  @Override
  public LazyResultSet<T> readLazy(SqlStatement sql) {
    return readLazy(sql.getSql(), sql.getParameters());
  }

  @Override
  public LazyResultSet<T> readLazy(String sql, Object... parameters) {
    return ormConnection.readLazy(objectClass, sql, parameters);
  }

  @Override
  public List<T> readList(SqlStatement sql) {
    return readList(sql.getSql(), sql.getParameters());
  }

  @Override
  public List<T> readList(String sql, Object... parameters) {
    return ormConnection.readList(objectClass, sql, parameters);
  }

  @Override
  public Map<String, Object> readMapFirst(SqlStatement sql) {
    return ormConnection.readMapFirst(sql.getSql(), sql.getParameters());
  }

  @Override
  public Map<String, Object> readMapFirst(String sql, Object... parameters) {
    return ormConnection.readMapFirst(sql, parameters);
  }

  @Override
  public LazyResultSet<Map<String, Object>> readMapLazy(SqlStatement sql) {
    return ormConnection.readMapLazy(sql.getSql(), sql.getParameters());
  }

  @Override
  public LazyResultSet<Map<String, Object>> readMapLazy(String sql, Object... parameters) {
    return ormConnection.readMapLazy(sql, parameters);
  }

  @Override
  public List<Map<String, Object>> readMapList(SqlStatement sql) {
    return ormConnection.readMapList(sql.getSql(), sql.getParameters());
  }

  @Override
  public List<Map<String, Object>> readMapList(String sql, Object... parameters) {
    return ormConnection.readMapList(sql, parameters);
  }

  @Override
  public Map<String, Object> readMapOne(SqlStatement sql) {
    return ormConnection.readMapOne(sql.getSql(), sql.getParameters());
  }

  @Override
  public Map<String, Object> readMapOne(String sql, Object... parameters) {
    return ormConnection.readMapOne(sql, parameters);
  }

  @Override
  public T readOne(SqlStatement sql) {
    return ormConnection.readOne(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public T readOne(String sql, Object... parameters) {
    return ormConnection.readOne(objectClass, sql, parameters);
  }

  @Override
  public void rollback() {
    Try.runOrThrow(() -> getJdbcConnection().rollback(), Try::rethrow);
  }

  @Override
  public void setAutoCommit(final boolean autoCommit) {
    Try.runOrThrow(() -> getJdbcConnection().setAutoCommit(autoCommit), Try::rethrow);
  }

  private void setTransactionIsolation(int level) {
    Try.runOrThrow(() -> getJdbcConnection().setTransactionIsolation(level), Try::rethrow);
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
  public int[] update(List<T> objects) {
    return ormConnection.update(objects);
  }

  @Override
  public int update(T object) {
    return ormConnection.update(object);
  }

  @Override
  public int[] update(@SuppressWarnings("unchecked") T... objects) {
    return ormConnection.update(objects);
  }

  @Override
  public int[] updateOn(String tableName, List<T> objects) {
    return ormConnection.updateOn(tableName, objects);
  }

  @Override
  public int updateOn(String tableName, T object) {
    return ormConnection.updateOn(tableName, object);
  }

  @Override
  public int[] updateOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return ormConnection.updateOn(tableName, objects);
  }

}
