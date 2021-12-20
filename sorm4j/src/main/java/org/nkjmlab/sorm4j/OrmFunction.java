package org.nkjmlab.sorm4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.TableMetaData;
import org.nkjmlab.sorm4j.sql.result.InsertResult;
import org.nkjmlab.sorm4j.sql.result.Tuple2;
import org.nkjmlab.sorm4j.sql.result.Tuple3;

/**
 * The interface of functions of object-relation mapping with table name in query.
 *
 * @author nkjm
 *
 */
public interface OrmFunction {

  /**
   * Returns the object which has same primary key exists or not.
   *
   * @param object
   * @return
   */
  <T> boolean exists(String tableName, T object);


  /**
   * Gets table metadata to the given object class and the table name.
   *
   * @param objectClass
   * @return
   */
  TableMetaData getTableMetaData(Class<?> objectClass, String tableName);

  /**
   * Reads an object from the database.
   *
   * @param <T>
   * @param objectClass
   * @param sql
   * @return
   */
  <T> T readFirst(Class<T> objectClass, ParameterizedSql sql);

  /**
   * Reads an object from the database.
   *
   * @param <T>
   * @param objectClass
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   * @return
   */
  <T> T readFirst(Class<T> objectClass, String sql, Object... parameters);

  /**
   * Reads a list of objects from the database by mapping the results of the parameterized SQL query
   * into instances of the given object class. Only the columns returned from the SQL query will be
   * set into the object instance.
   *
   * @param <T>
   * @param objectClass
   * @param sql
   * @return
   */

  <T> List<T> readList(Class<T> objectClass, ParameterizedSql sql);

  /**
   * Reads a list of objects from the database by mapping the results of the parameterized SQL query
   * into instances of the given object class. Only the columns returned from the SQL query will be
   * set into the object instance.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(SormOptions, PreparedStatement, Object[])}
   *
   */
  <T> List<T> readList(Class<T> objectClass, String sql, Object... parameters);

  /**
   * Reads only one object from the database.
   *
   * @param <T>
   * @param objectClass
   * @param sql
   * @return
   */
  <T> T readOne(Class<T> objectClass, ParameterizedSql sql);

  /**
   * Reads only one object from the database.
   *
   * @param <T>
   * @param objectClass
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   * @return
   */
  <T> T readOne(Class<T> objectClass, String sql, Object... parameters);


  /**
   * Reads results as List of {@link Tuple3} for reading JOIN SQL results typically.
   *
   * @see {@link OrmColumnAliasPrefix} for use column alias prefix.
   *
   * @param <T1>
   * @param <T2>
   * @param <T3>
   * @param t1
   * @param t2
   * @param t3
   * @param sql
   * @return
   */
  @Experimental
  <T1, T2, T3> List<Tuple3<T1, T2, T3>> readTupleList(Class<T1> t1, Class<T2> t2, Class<T3> t3,
      ParameterizedSql sql);

  /**
   * Reads results as List of {@link Tuple3} for reading JOIN SQL results typically.
   *
   * @see {@link OrmColumnAliasPrefix} for use column alias prefix.
   *
   * @param <T1>
   * @param <T2>
   * @param t1
   * @param t2
   * @param sql
   * @param parameters
   * @return
   */

  @Experimental
  <T1, T2, T3> List<Tuple3<T1, T2, T3>> readTupleList(Class<T1> t1, Class<T2> t2, Class<T3> t3,
      String sql, Object... parameters);

  /**
   * Reads results as List of {@link Tuple2} for reading JOIN SQL results typically.
   *
   * @see {@link OrmColumnAliasPrefix} for use column alias prefix.
   *
   * @param <T1>
   * @param <T2>
   * @param t1
   * @param t2
   * @param sql
   * @return
   */
  @Experimental
  <T1, T2> List<Tuple2<T1, T2>> readTupleList(Class<T1> t1, Class<T2> t2, ParameterizedSql sql);

  /**
   * Reads results as List of {@link Tuple2} for reading JOIN SQL results typically.
   *
   * @see {@link OrmColumnAliasPrefix} for use column alias prefix.
   *
   * @param <T1>
   * @param <T2>
   * @param t1
   * @param t2
   * @param sql
   * @param parameters
   * @return
   */
  @Experimental
  <T1, T2> List<Tuple2<T1, T2>> readTupleList(Class<T1> t1, Class<T2> t2, String sql,
      Object... parameters);


  /**
   * Gets a function which maps one row in the resultSet to an object. The method does not call
   * {@link ResultSet#next()}.
   *
   * @return
   */
  RowMapper<Map<String, Object>> getRowToMapMapper();

  /**
   * Gets function which traverses and maps the all the rows in the given resultSet to an object
   * list.
   *
   * @return
   */
  ResultSetTraverser<List<Map<String, Object>>> getResultSetToMapTraverser();



  /**
   * See {@link OrmMapReader#readMapFirst(String, Object...)}
   *
   * @param sql
   * @return
   */
  Map<String, Object> readMapFirst(ParameterizedSql sql);

  /**
   * Reads a first row from the database by mapping the results of the SQL query into an instance of
   * {@link java.util.Map}.
   * <p>
   * Letter case of the key in the Map depends on {@link ResultSetConverter#toSingleMap}
   * <p>
   * Types returned from the database will be converted to Java types in the map according with the
   * correspondence defined in
   * {@link ResultSetConverter#toSingleMap(SormOptions, ResultSet, List, List)}.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(SormOptions, PreparedStatement, Object... )}
   *
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   */
  Map<String, Object> readMapFirst(String sql, Object... parameters);


  /**
   * See {@link OrmMapReader#readMapList(String, Object...)}
   *
   * @param sql
   * @return
   */
  List<Map<String, Object>> readMapList(ParameterizedSql sql);

  /**
   * Reads a list of objects from the database by mapping the SQL execution results to instances of
   * {@link java.util.Map} containing data from the execution of the provided parameterized SQL.
   *
   * <p>
   * Letter case of the key in the Map depends on {@link ResultSetConverter#toSingleMap}
   *
   * <p>
   * Types of value returned from the database will be converted to Java types in the map according
   * with the correspondence defined in
   * {@link ResultSetConverter#toSingleMap(SormOptions, ResultSet, List, List)}.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(SormOptions, PreparedStatement, Object... )}
   *
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   */
  List<Map<String, Object>> readMapList(String sql, Object... parameters);

  /**
   * See {@link OrmMapReader#readMapOne(String, Object...)}
   *
   * @param sql
   * @return
   */
  Map<String, Object> readMapOne(ParameterizedSql sql);

  /**
   * Reads a first row from the database by mapping the results of the SQL query into an instance of
   * {@link java.util.Map}. If the given SQL statement gets non-unique result, {@link SormException}
   * is thrown.
   * <p>
   * Letter case of the key in the Map depends on {@link ResultSetConverter#toSingleMap}
   * <p>
   * Types of value returned from the database will be converted to Java types in the map according
   * with the correspondence defined in
   * {@link ResultSetConverter#toSingleMap(SormOptions, ResultSet, List, List)}.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(SormOptions, PreparedStatement, Object... )}
   *
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   */
  Map<String, Object> readMapOne(String sql, Object... parameters);


  /**
   * Deletes objects on the table of the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] deleteOn(String tableName, List<T> objects);

  /**
   * Deletes object on the table of the given table name.
   *
   * @param <T>
   * @param tableName
   * @param object
   * @return
   */
  <T> int deleteOn(String tableName, T object);

  /**
   * Deletes objects on the table of the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] deleteOn(String tableName, @SuppressWarnings("unchecked") T... objects);


  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] insertOn(String tableName, List<T> objects);

  /**
   * Inserts an object and get the insert result.
   *
   * @param <T>
   * @param tableName
   * @param object
   * @return
   */
  <T> int insertOn(String tableName, T object);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] insertOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  /**
   *
   * @param tableName
   * @param object
   * @return
   */
  @Experimental
  int insertMapOn(String tableName, Map<String, Object> object);

  /**
   * This method is experimental.
   *
   * @param tableName
   * @param objects
   * @return
   */
  @Experimental
  int[] insertMapOn(String tableName,
      @SuppressWarnings("unchecked") Map<String, Object>... objects);

  /**
   * This method is experimental.
   *
   * @param tableName
   * @param objects
   * @return
   */
  @Experimental
  int[] insertMapOn(String tableName, List<Map<String, Object>> objects);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> InsertResult<T> insertAndGetOn(String tableName, List<T> objects);

  /**
   * Inserts an object and get the insert result.
   *
   * @param <T>
   * @param tableName
   * @param object
   * @return
   */
  <T> InsertResult<T> insertAndGetOn(String tableName, T object);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> InsertResult<T> insertAndGetOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  /**
   * Merges by objects on the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] mergeOn(String tableName, List<T> objects);

  /**
   * Merges by an object on the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param object
   * @return
   */
  <T> int mergeOn(String tableName, T object);

  /**
   * Merges by objects on the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] mergeOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  /**
   * Updates by objects on the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] updateOn(String tableName, List<T> objects);

  /**
   * Updates by an object on the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param object
   * @return
   */
  <T> int updateOn(String tableName, T object);

  /**
   * Updates by objects on the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] updateOn(String tableName, @SuppressWarnings("unchecked") T... objects);


}
