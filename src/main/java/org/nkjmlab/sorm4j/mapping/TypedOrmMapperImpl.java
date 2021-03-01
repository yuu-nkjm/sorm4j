package org.nkjmlab.sorm4j.mapping;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.nkjmlab.sorm4j.InsertResult;
import org.nkjmlab.sorm4j.LazyResultSet;
import org.nkjmlab.sorm4j.OrmMapper;
import org.nkjmlab.sorm4j.SqlStatement;
import org.nkjmlab.sorm4j.config.OrmConfigStore;

public class TypedOrmMapperImpl<T> extends AbstractOrmMapper implements TypedOrmMapper<T> {

  private Class<T> objectClass;

  protected TypedOrmMapperImpl(Class<T> objectClass, Connection connection,
      OrmConfigStore options) {
    super(connection, options);
    this.objectClass = objectClass;
  }

  public OrmMapper toUntyped() {
    return new OrmMapperImpl(getJdbcConnection(), getConfigStore());
  }

  public List<String> getAllColumns() {
    return getAllColumnsAux(objectClass);
  }

  public List<String> getPrimaryKeys() {
    return getPrimaryKeysAux(objectClass);
  }



  @Override
  public T readByPrimaryKey(Object... primaryKeyValues) {
    return readByPrimaryKeyAux(objectClass, primaryKeyValues);
  }


  @Override
  public T readFirst(String sql, Object... parameters) {
    return readFirstAux(objectClass, sql, parameters);
  }

  @Override
  public LazyResultSet<T> readLazy(String sql, Object... parameters) {
    return readLazyAux(objectClass, sql, parameters);
  }

  @Override
  public LazyResultSet<T> readAllLazy() {
    return readAllLazyAux(objectClass);
  }


  @Override
  public List<T> readList(String sql, Object... parameters) {
    return readListAux(objectClass, sql, parameters);
  }

  @Override
  public List<T> readAll() {
    return readAllAux(objectClass);
  }


  @Override
  public int insert(T object) {
    return getCastedTableMapping(object.getClass()).insert(getJdbcConnection(), object);
  }


  @Override
  public int insertOn(String tableName, T object) {
    return getCastedTableMapping(tableName, object.getClass()).insert(getJdbcConnection(), object);
  }



  @Override
  public InsertResult<T> insertAndGet(T object) {
    TableMapping<T> mapping = getCastedTableMapping(object.getClass());
    return mapping.insertAndGetResult(getJdbcConnection(), object);
  }


  @Override
  public InsertResult<T> insertAndGetOn(String tableName, T object) {
    TableMapping<T> mapping = getCastedTableMapping(tableName, object.getClass());
    return mapping.insertAndGetResult(getJdbcConnection(), object);
  }



  @Override
  public int delete(T object) {
    return getCastedTableMapping(object.getClass()).delete(getJdbcConnection(), object);
  }


  @Override
  public int deleteOn(String tableName, T object) {
    return getCastedTableMapping(tableName, object.getClass()).delete(getJdbcConnection(), object);
  }


  @Override
  public int update(T object) {
    return getCastedTableMapping(object.getClass()).update(getJdbcConnection(), object);
  }


  @Override
  public int updateOn(String tableName, T object) {
    return getCastedTableMapping(tableName, object.getClass()).update(getJdbcConnection(), object);
  }


  @Override
  public int merge(T object) {
    return getCastedTableMapping(object.getClass()).merge(getJdbcConnection(), object);
  }


  @Override
  public int mergeOn(String tableName, T object) {
    return getCastedTableMapping(tableName, object.getClass()).merge(getJdbcConnection(), object);
  }


  @Override
  public int[] delete(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects,
        mapping -> mapping.delete(getJdbcConnection(), objects), () -> new int[0]);
  }

  @Override
  public int deleteAll() {
    return deleteAllAux(objectClass);
  }


  @Override
  public int deleteOnAll(String tableName) {
    return deleteAllAux(tableName, objectClass);
  }


  @Override
  public int[] update(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects,
        mapping -> mapping.update(getJdbcConnection(), objects), () -> new int[0]);
  }


  @Override
  public int[] merge(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects, mapping -> mapping.merge(getJdbcConnection(), objects),
        () -> new int[0]);
  }

  @Override
  public int[] insert(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects,
        mapping -> mapping.insert(getJdbcConnection(), objects), () -> new int[0]);
  }


  @Override
  public InsertResult<T> insertAndGet(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects,
        mapping -> mapping.insertAndGetResult(getJdbcConnection(), objects),
        () -> InsertResult.empty());
  }



  @Override
  public int[] deleteOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects, tableName,
        mapping -> mapping.delete(getJdbcConnection(), objects), () -> new int[0]);
  }


  @Override
  public int[] updateOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects, tableName,
        mapping -> mapping.update(getJdbcConnection(), objects), () -> new int[0]);
  }


  @Override
  public int[] mergeOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects, tableName,
        mapping -> mapping.merge(getJdbcConnection(), objects), () -> new int[0]);
  }


  @Override
  public int[] insertOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects, tableName,
        mapping -> mapping.insert(getJdbcConnection(), objects), () -> new int[0]);
  }


  @Override
  public InsertResult<T> insertAndGetOn(String tableName,
      @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects, tableName,
        mapping -> mapping.insertAndGetResult(getJdbcConnection(), objects),
        () -> InsertResult.empty());
  }



  @Override
  public int[] delete(List<T> objects) {
    return applytoArray(objects, array -> delete(array));
  }

  @SuppressWarnings("unchecked")
  private static <S, R> R applytoArray(List<S> objects, Function<S[], R> sqlFunc) {
    return sqlFunc.apply((S[]) objects.toArray(Object[]::new));
  }


  @Override
  public int[] deleteOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> deleteOn(tableName, array));
  }


  @Override
  public int[] insert(List<T> objects) {
    return applytoArray(objects, array -> insert(array));
  }


  @Override
  public InsertResult<T> insertAndGet(List<T> objects) {
    return applytoArray(objects, array -> insertAndGet(array));
  }


  @Override
  public InsertResult<T> insertAndGetOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> insertAndGetOn(tableName, array));
  }


  @Override
  public int[] insertOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> insertOn(tableName, array));
  }


  @Override
  public int[] merge(List<T> objects) {
    return applytoArray(objects, array -> merge(array));
  }


  @Override
  public int[] mergeOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> mergeOn(tableName, array));
  }


  @Override
  public int[] updateOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> updateOn(tableName, array));
  }


  @Override
  public int[] update(List<T> objects) {
    return applytoArray(objects, array -> update(array));
  }

  @Override
  public boolean execute(SqlStatement sql) {
    return execute(sql.getSql(), sql.getParameters());
  }

  @Override
  public ResultSet executeQuery(SqlStatement sql) {
    return executeQuery(sql.getSql(), sql.getParameters());
  }

  @Override
  public int executeUpdate(SqlStatement sql) {
    return executeUpdate(sql.getSql(), sql.getParameters());
  }

  @Override
  public T readFirst(SqlStatement sql) {
    return readFirst(sql.getSql(), sql.getParameters());
  }

  @Override
  public LazyResultSet<T> readLazy(SqlStatement sql) {
    return readLazy(sql.getSql(), sql.getParameters());
  }

  @Override
  public List<T> readList(SqlStatement sql) {
    return readList(sql.getSql(), sql.getParameters());
  }

  @Override
  public T readOne(String sql, Object... parameters) {
    return readOneAux(objectClass, sql, parameters);
  }

  @Override
  public T readOne(SqlStatement sql) {
    return readOneAux(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public Map<String, Object> readMapOne(SqlStatement sql) {
    return readMapOne(sql.getSql(), sql.getParameters());
  }

  @Override
  public Map<String, Object> readMapFirst(SqlStatement sql) {
    return readMapFirst(sql.getSql(), sql.getParameters());
  }

  @Override
  public LazyResultSet<Map<String, Object>> readMapLazy(SqlStatement sql) {
    return readMapLazy(sql.getSql(), sql.getParameters());
  }

  @Override
  public List<Map<String, Object>> readMapList(SqlStatement sql) {
    return readMapList(sql.getSql(), sql.getParameters());
  }


}
