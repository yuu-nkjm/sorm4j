package org.nkjmlab.sorm4j.core.mapping;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.sql.InsertResult;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.SqlStatement;

class TypedOrmMapperImpl<T> implements TypedOrmMapper<T> {

  protected Class<T> objectClass;
  protected OrmConnectionImpl ormConnection;

  public TypedOrmMapperImpl(Class<T> objectClass, OrmConnectionImpl ormMapper) {
    this.ormConnection = ormMapper;
    this.objectClass = objectClass;
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
