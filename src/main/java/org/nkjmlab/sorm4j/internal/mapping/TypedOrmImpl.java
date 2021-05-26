package org.nkjmlab.sorm4j.internal.mapping;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.ConsumerHandler;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.ResultSetTraverser;
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.TableMetaData;
import org.nkjmlab.sorm4j.sql.result.InsertResult;
import org.nkjmlab.sorm4j.typed.TypedOrm;


public class TypedOrmImpl<T> implements TypedOrm<T> {

  private Orm orm;
  private Class<T> objectClass;

  public TypedOrmImpl(Class<T> objectClass, Orm orm) {
    this.orm = orm;
    this.objectClass = objectClass;
  }

  @Override
  public List<T> readAll() {
    return orm.readAll(objectClass);
  }

  @Override
  public T readByPrimaryKey(Object... primaryKeyValues) {
    return orm.readByPrimaryKey(objectClass, primaryKeyValues);
  }

  @Override
  public T readFirst(ParameterizedSql sql) {
    return orm.readFirst(objectClass, sql);
  }

  @Override
  public T readFirst(String sql, Object... parameters) {
    return orm.readFirst(objectClass, sql, parameters);
  }


  @Override
  public List<T> readList(ParameterizedSql sql) {
    return orm.readList(objectClass, sql);
  }

  @Override
  public List<T> readList(String sql, Object... parameters) {
    return orm.readList(objectClass, sql, parameters);
  }

  @Override
  public T readOne(ParameterizedSql sql) {
    return orm.readOne(objectClass, sql);
  }

  @Override
  public T readOne(String sql, Object... parameters) {
    return orm.readOne(objectClass, sql, parameters);
  }

  @Override
  public RowMapper<T> getRowMapper() {
    return orm.getRowMapper(objectClass);
  }

  @Override
  public ResultSetTraverser<List<T>> getResultSetTraverser() {
    return orm.getResultSetTraverser(objectClass);
  }

  @Override
  public boolean exists(T object) {
    return orm.exists(object);
  }

  @Override
  public int[] delete(List<T> objects) {
    return orm.delete(objects);
  }

  @Override
  public int delete(T object) {
    return orm.delete(object);
  }

  @Override
  public int[] delete(T... objects) {
    return orm.delete(objects);
  }

  @Override
  public int[] deleteOn(String tableName, List<T> objects) {
    return orm.deleteOn(tableName, objects);
  }

  @Override
  public int deleteOn(String tableName, T object) {
    return orm.deleteOn(tableName, object);
  }

  @Override
  public int[] deleteOn(String tableName, T... objects) {
    return orm.deleteOn(tableName, objects);
  }

  @Override
  public int deleteAll() {
    return orm.deleteAll(objectClass);
  }

  @Override
  public int deleteAllOn(String tableName) {
    return orm.deleteAllOn(tableName);
  }


  @Override
  public int[] insert(List<T> objects) {
    return orm.insert(objects);
  }

  @Override
  public int insert(T object) {
    return orm.insert(object);
  }

  @Override
  public int[] insert(T... objects) {
    return orm.insert(objects);
  }

  @Override
  public int[] insertOn(String tableName, List<T> objects) {
    return orm.insertOn(tableName, objects);
  }

  @Override
  public int insertOn(String tableName, T object) {
    return orm.insertOn(tableName, object);
  }

  @Override
  public int[] insertOn(String tableName, T... objects) {
    return orm.insertOn(tableName, objects);
  }


  @Override
  public InsertResult<T> insertAndGet(List<T> objects) {
    return orm.insertAndGet(objects);
  }

  @Override
  public InsertResult<T> insertAndGet(T object) {
    return orm.insertAndGet(object);
  }

  @Override
  public InsertResult<T> insertAndGet(T... objects) {
    return orm.insertAndGet(objects);
  }

  @Override
  public InsertResult<T> insertAndGetOn(String tableName, List<T> objects) {
    return orm.insertAndGetOn(tableName, objects);
  }

  @Override
  public InsertResult<T> insertAndGetOn(String tableName, T object) {
    return orm.insertAndGetOn(tableName, object);
  }

  @Override
  public InsertResult<T> insertAndGetOn(String tableName, T... objects) {
    return orm.insertAndGetOn(tableName, objects);
  }

  @Override
  public int[] merge(List<T> objects) {
    return orm.merge(objects);
  }

  @Override
  public int merge(T object) {
    return orm.merge(object);
  }

  @Override
  public int[] merge(T... objects) {
    return orm.merge(objects);
  }

  @Override
  public int[] mergeOn(String tableName, List<T> objects) {
    return orm.mergeOn(tableName, objects);
  }

  @Override
  public int mergeOn(String tableName, T object) {
    return orm.mergeOn(tableName, object);
  }

  @Override
  public int[] mergeOn(String tableName, T... objects) {
    return orm.mergeOn(tableName, objects);
  }

  @Override
  public int[] update(List<T> objects) {
    return orm.update(objects);
  }

  @Override
  public int update(T object) {
    return orm.update(object);
  }

  @Override
  public int[] update(T... objects) {
    return orm.update(objects);
  }

  @Override
  public int[] updateOn(String tableName, List<T> objects) {
    return orm.updateOn(tableName, objects);
  }

  @Override
  public int updateOn(String tableName, T object) {
    return orm.updateOn(tableName, object);
  }

  @Override
  public int[] updateOn(String tableName, T... objects) {
    return orm.updateOn(tableName, objects);
  }

  @Override
  public RowMapper<Map<String, Object>> getRowToMapMapper() {
    return orm.getRowToMapMapper();
  }

  @Override
  public ResultSetTraverser<List<Map<String, Object>>> getResultSetToMapTraverser() {
    return orm.getResultSetToMapTraverser();
  }

  @Override
  public Map<String, Object> readMapFirst(ParameterizedSql sql) {
    return orm.readMapFirst(sql);
  }

  @Override
  public Map<String, Object> readMapFirst(String sql, Object... parameters) {
    return orm.readMapFirst(sql, parameters);
  }


  @Override
  public List<Map<String, Object>> readMapList(ParameterizedSql sql) {
    return orm.readMapList(sql);
  }

  @Override
  public List<Map<String, Object>> readMapList(String sql, Object... parameters) {
    return orm.readMapList(sql, parameters);
  }

  @Override
  public Map<String, Object> readMapOne(ParameterizedSql sql) {
    return orm.readMapOne(sql);
  }

  @Override
  public Map<String, Object> readMapOne(String sql, Object... parameters) {
    return orm.readMapOne(sql, parameters);
  }


  @Override
  public String getTableName() {
    return orm.getTableName(objectClass);
  }

  @Override
  public TableMetaData getTableMetaData() {
    return orm.getTableMetaData(objectClass);
  }

  @Override
  public TableMetaData getTableMetaData(String tableName) {
    return orm.getTableMetaData(objectClass, tableName);
  }

  @Override
  public void acceptPreparedStatementHandler(ParameterizedSql sql,
      ConsumerHandler<PreparedStatement> handler) {
    orm.acceptPreparedStatementHandler(sql, handler);
  }

  @Override
  public <S> S applyPreparedStatementHandler(ParameterizedSql sql,
      FunctionHandler<PreparedStatement, S> handler) {
    return orm.applyPreparedStatementHandler(sql, handler);
  }

  @Override
  public <S> S executeQuery(ParameterizedSql sql, ResultSetTraverser<S> traverser) {
    return orm.executeQuery(sql, traverser);
  }

  @Override
  public <S> List<S> executeQuery(ParameterizedSql sql, RowMapper<S> mapper) {
    return orm.executeQuery(sql, mapper);
  }

  @Override
  public int executeUpdate(String sql, Object... parameters) {
    return orm.executeUpdate(sql, parameters);
  }

  @Override
  public int executeUpdate(ParameterizedSql sql) {
    return orm.executeUpdate(sql);
  }

}
