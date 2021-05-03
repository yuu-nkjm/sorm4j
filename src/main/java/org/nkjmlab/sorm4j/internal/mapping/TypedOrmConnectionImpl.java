package org.nkjmlab.sorm4j.internal.mapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.ConsumerHandler;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.ResultSetTraverser;
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.sql.BasicCommand;
import org.nkjmlab.sorm4j.sql.NamedParameterCommand;
import org.nkjmlab.sorm4j.sql.OrderedParameterCommand;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.TableMetaData;
import org.nkjmlab.sorm4j.sql.result.InsertResult;
import org.nkjmlab.sorm4j.sql.result.LazyResultSet;
import org.nkjmlab.sorm4j.typed.TypedOrmConnection;

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

  protected OrmConnectionImpl conn;
  protected Class<T> objectClass;

  public TypedOrmConnectionImpl(Class<T> objectClass, OrmConnectionImpl ormMapper) {
    this.conn = ormMapper;
    this.objectClass = objectClass;
  }

  @Override
  public void acceptPreparedStatementHandler(ParameterizedSql sql,
      ConsumerHandler<PreparedStatement> handler) {
    conn.acceptPreparedStatementHandler(sql, handler);
  }

  @Override
  public <S> S applyPreparedStatementHandler(ParameterizedSql sql,
      FunctionHandler<PreparedStatement, S> handler) {
    return conn.applyPreparedStatementHandler(sql, handler);
  }

  @Override
  public void begin() {
    conn.begin();
  }

  @Override
  public void begin(int transactionIsolationLevel) {
    conn.begin(transactionIsolationLevel);
  }



  @Override
  public void close() {
    conn.close();
  }



  @Override
  public void commit() {
    conn.commit();
  }


  @Override
  public BasicCommand createCommand(String sql) {
    return BasicCommand.from(conn, sql);
  }


  @Override
  public NamedParameterCommand createCommand(String sql, Map<String, Object> parameters) {
    return NamedParameterCommand.from(conn, sql).bindAll(parameters);
  }


  @Override
  public OrderedParameterCommand createCommand(String sql, Object... parameters) {
    return OrderedParameterCommand.from(conn, sql).addParameter(parameters);
  }


  @Override
  public int[] delete(List<T> objects) {
    return conn.delete(objects);
  }


  @Override
  public int delete(T object) {
    return conn.delete(object);
  }


  @Override
  public int[] delete(@SuppressWarnings("unchecked") T... objects) {
    return conn.delete(objects);
  }

  @Override
  public int deleteAll() {
    return conn.deleteAll(objectClass);
  }



  @Override
  public int deleteAllOn(String tableName) {
    return conn.deleteAllOn(tableName);
  }


  @Override
  public int[] deleteOn(String tableName, List<T> objects) {
    return conn.deleteOn(tableName, objects);
  }

  @Override
  public int deleteOn(String tableName, T object) {
    return conn.deleteOn(tableName, object);
  }

  @Override
  public int[] deleteOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return conn.deleteOn(tableName, objects);
  }

  @Override
  public <S> S executeQuery(ParameterizedSql sql, ResultSetTraverser<S> resultSetTraverser) {
    return conn.executeQuery(sql, resultSetTraverser);
  }

  @Override
  public <S> List<S> executeQuery(ParameterizedSql sql, RowMapper<S> rowMapper) {
    return conn.executeQuery(sql, rowMapper);
  }


  @Override
  public int executeUpdate(ParameterizedSql sql) {
    return conn.executeUpdate(sql);
  }


  @Override
  public int executeUpdate(String sql, Object... parameters) {
    return conn.executeUpdate(sql, parameters);
  }


  @Override
  public boolean exists(T object) {
    return conn.exists(object);
  }



  @Override
  public Connection getJdbcConnection() {
    return conn.getJdbcConnection();
  }


  @Override
  public ResultSetTraverser<List<T>> getResultSetTraverser() {
    return conn.getResultSetTraverser(objectClass);
  }


  @Override
  public RowMapper<T> getRowMapper() {
    return conn.getRowMapper(objectClass);
  }


  @Override
  public TableMetaData getTableMetaData() {
    return conn.getTableMetaData(objectClass);
  }


  @Override
  public TableMetaData getTableMetaData(String tableName) {
    return conn.getTableMetaData(objectClass, tableName);
  }


  @Override
  public String getTableName() {
    return conn.getTableName(objectClass);
  }


  @Override
  public int[] insert(List<T> objects) {
    return conn.insert(objects);
  }


  @Override
  public int insert(T object) {
    return conn.insert(object);
  }

  @Override
  public int[] insert(@SuppressWarnings("unchecked") T... objects) {
    return conn.insert(objects);
  }

  @Override
  public InsertResult<T> insertAndGet(List<T> objects) {
    return conn.insertAndGet(objects);
  }

  @Override
  public InsertResult<T> insertAndGet(T object) {
    return conn.insertAndGet(object);
  }

  @Override
  public InsertResult<T> insertAndGet(@SuppressWarnings("unchecked") T... objects) {
    return conn.insertAndGet(objects);
  }

  @Override
  public InsertResult<T> insertAndGetOn(String tableName, List<T> objects) {
    return conn.insertAndGetOn(tableName, objects);
  }

  @Override
  public InsertResult<T> insertAndGetOn(String tableName, T object) {
    return conn.insertAndGetOn(tableName, object);
  }

  @Override
  public InsertResult<T> insertAndGetOn(String tableName,
      @SuppressWarnings("unchecked") T... objects) {
    return conn.insertAndGetOn(tableName, objects);
  }

  @Override
  public int[] insertOn(String tableName, List<T> objects) {
    return conn.insertOn(tableName, objects);
  }

  @Override
  public int insertOn(String tableName, T object) {
    return conn.insertOn(tableName, object);
  }

  @Override
  public int[] insertOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return conn.insertOn(tableName, objects);
  }

  @Override
  public Map<String, Object> mapRowToMap(ResultSet resultSet) {
    return conn.mapRowToMap(resultSet);
  }

  @Override
  public int[] merge(List<T> objects) {
    return conn.merge(objects);
  }

  @Override
  public int merge(T object) {
    return conn.merge(object);
  }

  @Override
  public int[] merge(@SuppressWarnings("unchecked") T... objects) {
    return conn.merge(objects);
  }

  @Override
  public int[] mergeOn(String tableName, List<T> objects) {
    return conn.mergeOn(tableName, objects);
  }

  @Override
  public int mergeOn(String tableName, T object) {
    return conn.mergeOn(tableName, object);
  }

  @Override
  public int[] mergeOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return conn.mergeOn(tableName, objects);
  }

  @Override
  public final List<T> readAll() {
    return conn.readAll(objectClass);
  }

  @Override
  public LazyResultSet<T> readAllLazy() {
    return conn.readAllLazy(objectClass);
  }

  @Override
  public T readByPrimaryKey(Object... primaryKeyValues) {
    return conn.readByPrimaryKey(objectClass, primaryKeyValues);
  }

  @Override
  public T readFirst(ParameterizedSql sql) {
    return conn.readFirst(objectClass, sql);
  }

  @Override
  public T readFirst(String sql, Object... parameters) {
    return conn.readFirst(objectClass, sql, parameters);
  }

  @Override
  public LazyResultSet<T> readLazy(ParameterizedSql sql) {
    return conn.readLazy(objectClass, sql);
  }

  @Override
  public LazyResultSet<T> readLazy(String sql, Object... parameters) {
    return conn.readLazy(objectClass, sql, parameters);
  }

  @Override
  public List<T> readList(ParameterizedSql sql) {
    return conn.readList(objectClass, sql);
  }

  @Override
  public List<T> readList(String sql, Object... parameters) {
    return conn.readList(objectClass, sql, parameters);
  }

  @Override
  public Map<String, Object> readMapFirst(ParameterizedSql sql) {
    return conn.readMapFirst(sql.getSql(), sql.getParameters());
  }

  @Override
  public Map<String, Object> readMapFirst(String sql, Object... parameters) {
    return conn.readMapFirst(sql, parameters);
  }

  @Override
  public LazyResultSet<Map<String, Object>> readMapLazy(ParameterizedSql sql) {
    return conn.readMapLazy(sql.getSql(), sql.getParameters());
  }

  @Override
  public LazyResultSet<Map<String, Object>> readMapLazy(String sql, Object... parameters) {
    return conn.readMapLazy(sql, parameters);
  }

  @Override
  public List<Map<String, Object>> readMapList(ParameterizedSql sql) {
    return conn.readMapList(sql.getSql(), sql.getParameters());
  }

  @Override
  public List<Map<String, Object>> readMapList(String sql, Object... parameters) {
    return conn.readMapList(sql, parameters);
  }

  @Override
  public Map<String, Object> readMapOne(ParameterizedSql sql) {
    return conn.readMapOne(sql.getSql(), sql.getParameters());
  }

  @Override
  public Map<String, Object> readMapOne(String sql, Object... parameters) {
    return conn.readMapOne(sql, parameters);
  }

  @Override
  public T readOne(ParameterizedSql sql) {
    return conn.readOne(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public T readOne(String sql, Object... parameters) {
    return conn.readOne(objectClass, sql, parameters);
  }

  @Override
  public void rollback() {
    conn.rollback();
  }

  @Override
  public void setAutoCommit(final boolean autoCommit) {
    conn.setAutoCommit(autoCommit);
  }

  @Override
  public List<Map<String, Object>> traverseAndMapToMapList(ResultSet resultSet) {
    return conn.traverseAndMapToMapList(resultSet);
  }

  @Override
  public <S> TypedOrmConnection<S> type(Class<S> objectClass) {
    return new TypedOrmConnectionImpl<>(objectClass, conn);
  }

  @Override
  public OrmConnection untype() {
    return conn;
  }

  @Override
  public int[] update(List<T> objects) {
    return conn.update(objects);
  }


  @Override
  public int update(T object) {
    return conn.update(object);
  }

  @Override
  public int[] update(@SuppressWarnings("unchecked") T... objects) {
    return conn.update(objects);
  }

  @Override
  public int[] updateOn(String tableName, List<T> objects) {
    return conn.updateOn(tableName, objects);
  }

  @Override
  public int updateOn(String tableName, T object) {
    return conn.updateOn(tableName, object);
  }

  @Override
  public int[] updateOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return conn.updateOn(tableName, objects);
  }

}
