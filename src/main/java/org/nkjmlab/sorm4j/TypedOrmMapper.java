package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.util.List;
import java.util.function.Function;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.mapping.TableMapping;

public class TypedOrmMapper<T> extends AbstractOrmMapper
    implements TypeOrmReader<T>, TypedOrmUpdater<T> {
  public static <T> TypedOrmConnection<T> of(Class<T> objectClass, Connection conn) {
    return new TypedOrmConnection<T>(objectClass, conn, OrmConfigStore.DEFAULT_CONFIGURATIONS);
  }

  public static <T> TypedOrmConnection<T> of(Class<T> objectClass, Connection connection,
      OrmConfigStore options) {
    return new TypedOrmConnection<T>(objectClass, connection, options);
  }

  private Class<T> objectClass;

  TypedOrmMapper(Class<T> objectClass, Connection connection, OrmConfigStore options) {
    super(connection, options);
    this.objectClass = objectClass;
  }

  public OrmUpdater toUntyped() {
    return new OrmMapper(getJdbcConnection(), getConfigStore());
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
  public ReadResultSet<T> readLazy(String sql, Object... parameters) {
    return readLazyAux(objectClass, sql, parameters);
  }

  @Override
  public ReadResultSet<T> readAllLazy() {
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


}
