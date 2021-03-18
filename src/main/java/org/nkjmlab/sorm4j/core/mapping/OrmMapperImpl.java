
package org.nkjmlab.sorm4j.core.mapping;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.nkjmlab.sorm4j.sql.InsertResult;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.SqlStatement;

/**
 * The main class for the ORMapper engine.
 *
 */
class OrmMapperImpl extends AbstractOrmMapper implements OrmMapper {

  /**
   * Creates a instance that will use the default cache for table-object and column-object mappings.
   *
   * @param connection {@link java.sql.Connection} object to be used
   */

  public OrmMapperImpl(Connection connection, ConfigStore defaultConfigurations) {
    super(connection, defaultConfigurations);
  }


  @Override
  public <T> int insert(T object) {
    return getCastedTableMapping(object.getClass()).insert(getJdbcConnection(), object);
  }


  @Override
  public <T> int insertOn(String tableName, T object) {
    return getCastedTableMapping(tableName, object.getClass()).insert(getJdbcConnection(), object);
  }



  @Override
  public <T> InsertResult<T> insertAndGet(T object) {
    TableMapping<T> mapping = getCastedTableMapping(object.getClass());
    return mapping.insertAndGet(getJdbcConnection(), object);
  }


  @Override
  public <T> InsertResult<T> insertAndGetOn(String tableName, T object) {
    TableMapping<T> mapping = getCastedTableMapping(tableName, object.getClass());
    return mapping.insertAndGet(getJdbcConnection(), object);
  }



  @Override
  public <T> int delete(T object) {
    return getCastedTableMapping(object.getClass()).delete(getJdbcConnection(), object);
  }


  @Override
  public <T> int deleteOn(String tableName, T object) {
    return getCastedTableMapping(tableName, object.getClass()).delete(getJdbcConnection(), object);
  }


  @Override
  public <T> int update(T object) {
    return getCastedTableMapping(object.getClass()).update(getJdbcConnection(), object);
  }


  @Override
  public <T> int updateOn(String tableName, T object) {
    return getCastedTableMapping(tableName, object.getClass()).update(getJdbcConnection(), object);
  }


  @Override
  public <T> int merge(T object) {
    return getCastedTableMapping(object.getClass()).merge(getJdbcConnection(), object);
  }


  @Override
  public <T> int mergeOn(String tableName, T object) {
    return getCastedTableMapping(tableName, object.getClass()).merge(getJdbcConnection(), object);
  }


  @Override
  public <T> int[] delete(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects,
        mapping -> mapping.delete(getJdbcConnection(), objects), () -> new int[0]);
  }


  @Override
  public <T> int[] update(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects,
        mapping -> mapping.update(getJdbcConnection(), objects), () -> new int[0]);
  }


  @Override
  public <T> int[] merge(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects, mapping -> mapping.merge(getJdbcConnection(), objects),
        () -> new int[0]);
  }


  @Override
  public <T> int[] insert(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects,
        mapping -> mapping.insert(getJdbcConnection(), objects), () -> new int[0]);
  }


  @Override
  public <T> InsertResult<T> insertAndGet(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects,
        mapping -> mapping.insertAndGet(getJdbcConnection(), objects),
        () -> InsertResultImpl.emptyInsertResult());
  }



  @Override
  public <T> int[] deleteOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.delete(getJdbcConnection(), objects), () -> new int[0]);
  }


  @Override
  public <T> int[] updateOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.update(getJdbcConnection(), objects), () -> new int[0]);
  }


  @Override
  public <T> int[] mergeOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.merge(getJdbcConnection(), objects), () -> new int[0]);
  }


  @Override
  public <T> int[] insertOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.insert(getJdbcConnection(), objects), () -> new int[0]);
  }


  @Override
  public <T> InsertResult<T> insertAndGetOn(String tableName,
      @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.insertAndGet(getJdbcConnection(), objects),
        () -> InsertResultImpl.emptyInsertResult());
  }



  @Override
  public <T> int[] delete(List<T> objects) {
    return applytoArray(objects, array -> delete(array));
  }

  @SuppressWarnings("unchecked")
  private static <T, R> R applytoArray(List<T> objects, Function<T[], R> sqlFunc) {
    return sqlFunc.apply((T[]) objects.toArray(Object[]::new));
  }


  @Override
  public <T> int[] deleteOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> deleteOn(tableName, array));
  }

  @Override
  public <T> int[] insert(List<T> objects) {
    return applytoArray(objects, array -> insert(array));
  }


  @Override
  public <T> InsertResult<T> insertAndGet(List<T> objects) {
    return applytoArray(objects, array -> insertAndGet(array));
  }


  @Override
  public <T> InsertResult<T> insertAndGetOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> insertAndGetOn(tableName, array));
  }


  @Override
  public <T> int[] insertOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> insertOn(tableName, array));
  }


  @Override
  public <T> int[] merge(List<T> objects) {
    return applytoArray(objects, array -> merge(array));
  }


  @Override
  public <T> int[] mergeOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> mergeOn(tableName, array));
  }


  @Override
  public <T> int[] updateOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> updateOn(tableName, array));
  }


  @Override
  public <T> int[] update(List<T> objects) {
    return applytoArray(objects, array -> update(array));
  }

  @Override
  public <T> T readByPrimaryKey(Class<T> objectClass, Object... primaryKeyValues) {
    return readByPrimaryKeyAux(objectClass, primaryKeyValues);
  }

  @Override
  public <T> List<T> readList(Class<T> objectClass, String sql, Object... parameters) {
    return readListAux(objectClass, sql, parameters);
  }


  @Override
  public final <T> List<T> readAll(Class<T> objectClass) {
    return readAllAux(objectClass);
  }


  @Override
  public <T> LazyResultSet<T> readAllLazy(Class<T> objectClass) {
    return readAllLazyAux(objectClass);
  }

  @Override
  public <T> T readFirst(Class<T> objectClass, String sql, Object... parameters) {
    return readFirstAux(objectClass, sql, parameters);
  }

  @Override
  public <T> LazyResultSet<T> readLazy(Class<T> objectClass, String sql, Object... parameters) {
    return readLazyAux(objectClass, sql, parameters);
  }


  @Override
  public <T> T readFirst(Class<T> objectClass, SqlStatement sql) {
    return readFirst(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T> LazyResultSet<T> readLazy(Class<T> objectClass, SqlStatement sql) {
    return readLazy(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T> List<T> readList(Class<T> objectClass, SqlStatement sql) {
    return readList(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public Map<String, Object> readMapFirst(SqlStatement sql) {
    return readMapFirst(sql.getSql(), sql.getParameters());
  }

  @Override
  public Map<String, Object> readMapOne(SqlStatement sql) {
    return readMapOne(sql.getSql(), sql.getParameters());
  }

  @Override
  public LazyResultSet<Map<String, Object>> readMapLazy(SqlStatement sql) {
    return readMapLazy(sql.getSql(), sql.getParameters());
  }

  @Override
  public List<Map<String, Object>> readMapList(SqlStatement sql) {
    return readMapList(sql.getSql(), sql.getParameters());
  }

  @Override
  public <T> T readOne(Class<T> objectClass, String sql, Object... parameters) {
    return readOneAux(objectClass, sql, parameters);
  }

  @Override
  public <T> T readOne(Class<T> objectClass, SqlStatement sql) {
    return readOneAux(objectClass, sql.getSql(), sql.getParameters());
  }

}

