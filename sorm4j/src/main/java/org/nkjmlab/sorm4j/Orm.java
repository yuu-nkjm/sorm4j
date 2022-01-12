package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.lowlevel_orm.FunctionHandler;
import org.nkjmlab.sorm4j.lowlevel_orm.ResultSetTraverser;
import org.nkjmlab.sorm4j.lowlevel_orm.RowMapper;
import org.nkjmlab.sorm4j.mapping.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.mapping.SqlParametersSetter;
import org.nkjmlab.sorm4j.result.InsertResult;
import org.nkjmlab.sorm4j.result.TableMetaData;
import org.nkjmlab.sorm4j.result.Tuple2;
import org.nkjmlab.sorm4j.result.Tuple3;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.util.command.CommandExecutor;

@Experimental
public interface Orm extends CommandExecutor {

  /**
   * Executes the query with the given PreparedStatement and applies the given RowMapper. If you
   * want to set parameters to a PreparedStatement object by yourself, you can use this method. You
   * can use your {@link ResultSetTraverser} or the object getting by
   * {@link Orm#getResultSetTraverser(Class)};
   *
   * @param <T>
   * @param statementSupplier
   * @param traverser
   * @return
   */
  @Experimental
  <T> T executeQuery(FunctionHandler<Connection, PreparedStatement> statementSupplier,
      ResultSetTraverser<T> traverser);


  /**
   * Executes the query with the given PreparedStatement and applies the given RowMapper. If you
   * want to set parameters to a PreparedStatement object by yourself, you can use this method. You
   * can use your {@link RowMapper} or the object getting by {@link Orm#getRowMapper(Class)};
   *
   * @param <T>
   * @param statementSupplier
   * @param rowMapper
   * @return
   */
  @Experimental
  <T> List<T> executeQuery(FunctionHandler<Connection, PreparedStatement> statementSupplier,
      RowMapper<T> rowMapper);


  /**
   * Executes a query and apply the given {@link ResultSetTraverser} to the returned result set.
   * <p>
   * This method wraps {@link PreparedStatement#executeQuery(String)}
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(PreparedStatement, Object...)}
   *
   * @param <T>
   * @param sql SQL code to be executed.
   * @param traverser
   * @return
   */
  <T> T executeQuery(ParameterizedSql sql, ResultSetTraverser<T> traverser);


  /**
   * Executes a query and apply the given {@link RowMapper} to the each row in returned result set.
   *
   * @param <T>
   * @param sql
   * @param mapper
   * @return
   */
  <T> List<T> executeQuery(ParameterizedSql sql, RowMapper<T> mapper);

  /**
   * Executes an update and returns the number of rows modified.
   * <p>
   * This method wraps {@link PreparedStatement#executeUpdate(String)}
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(PreparedStatement, Object...)}
   *
   * @param sql SQL code to be executed.
   * @param parameters Parameters to be used in the PreparedStatement.
   */
  int executeUpdate(String sql, Object... parameters);


  /**
   * {@link #executeUpdate(String, Object...)}
   *
   * @param sql
   * @return
   */
  int executeUpdate(ParameterizedSql sql);

  /**
   * Deletes objects from the table corresponding to the class of the given objects.
   *
   * @param <T> the object's element type which is mapped to the unique table.
   * @param objects the objects to delete to
   * @return the number of affected rows
   */
  <T> int[] delete(List<T> objects);


  /**
   * Deletes an object from the table corresponding to the class of the given objects.
   *
   * @param <T> the object's type which is mapped to the unique table.
   * @param object the object to delete to
   * @return the number of affected rows
   */
  <T> int delete(T object);

  /**
   * Deletes objects.
   *
   * @param <T> the object's element type which is mapped to the unique table.
   * @param objects
   * @return
   */
  <T> int[] delete(@SuppressWarnings("unchecked") T... objects);

  /**
   * Deletes all objects on the table corresponding to the given class.
   *
   * @param <T> the type to indicate the unique table.
   * @param type the type to indicate the unique table.
   * @return
   */
  <T> int deleteAll(Class<T> type);

  /**
   * Deletes all objects on the table corresponding to the given table name.
   *
   * @param tableName
   * @return
   */
  int deleteAllOn(String tableName);

  /**
   * Deletes objects on the table of the given table name.
   *
   * @param <T> the object's element type.
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] deleteOn(String tableName, List<T> objects);

  /**
   * Deletes object on the table of the given table name.
   *
   * @param <T> the object's type.
   * @param tableName
   * @param object
   * @return
   */
  <T> int deleteOn(String tableName, T object);

  /**
   * Deletes objects on the table of the given table name.
   *
   * @param <T> the object's element type.
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] deleteOn(String tableName, @SuppressWarnings("unchecked") T... objects);


  /**
   * Returns the object which has same primary key exists or not.
   *
   * @param <T> the object's type.
   * @param tableName
   * @param object
   * @return
   */
  <T> boolean exists(String tableName, T object);

  /**
   * Returns the object which has same primary key exists or not.
   *
   * @param <T> the object's type which is mapped to the unique table.
   * @param object
   * @return
   */
  <T> boolean exists(T object);

  /**
   * Gets function which traverses and maps the all the rows in the given resultSet to an object
   * list.
   *
   * @return
   */
  ResultSetTraverser<List<Map<String, Object>>> getResultSetToMapTraverser();

  /**
   * Gets function which traverses and maps the all the rows in the given resultSet to an object
   * list.
   *
   * @param <T> the read object's type.
   * @param type
   * @return
   */
  <T> ResultSetTraverser<List<T>> getResultSetTraverser(Class<T> type);


  /**
   * Gets a function which maps one row in the resultSet to an object. The method does not call
   * {@link ResultSet#next()}.
   *
   * @param <T>
   * @param type
   * @return
   */
  <T> RowMapper<T> getRowMapper(Class<T> type);

  /**
   * Gets a function which maps one row in the resultSet to an object. The method does not call
   * {@link ResultSet#next()}.
   *
   * @return
   */
  RowMapper<Map<String, Object>> getRowToMapMapper();



  /**
   * Gets table metadata corresponding to the given object class.
   *
   * @param type
   * @return
   */
  TableMetaData getTableMetaData(Class<?> type);

  /**
   * Gets table metadata to the given object class and the table name.
   *
   * @param tableName
   * @return
   */
  TableMetaData getTableMetaData(String tableName);


  /**
   * Gets table name corresponding to the given object class.
   *
   * @param type
   * @return
   */
  String getTableName(Class<?> type);

  /**
   * Inserts objects on the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] insert(List<T> objects);

  /**
   * Inserts object on the table corresponding to the class of the given object.
   *
   * @param <T>
   * @param object
   * @return
   */
  <T> int insert(T object);

  /**
   * Insert objects on the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] insert(@SuppressWarnings("unchecked") T... objects);


  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> InsertResult<T> insertAndGet(List<T> objects);

  /**
   * Inserts an object and get the result.
   *
   * @param <T>
   * @param object
   * @return
   */
  <T> InsertResult<T> insertAndGet(T object);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> InsertResult<T> insertAndGet(@SuppressWarnings("unchecked") T... objects);


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
   * This method is experimental.
   *
   * @param tableName
   * @param objects
   * @return
   */
  @Experimental
  int[] insertMapOn(String tableName, List<Map<String, Object>> objects);

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
   * Merges by objects on the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   * @see #merge(Object)
   */
  <T> int[] merge(List<T> objects);

  /**
   * Merges by an object on the table corresponding to the class of the given object.
   * <p>
   * Merge methods execute a SQL sentence as MERGE INTO of the H2 grammar. This operation may be not
   * working the other database system.
   *
   * See, <a href="http://www.h2database.com/html/commands.html#merge_into">MERGE INTO -
   * Commands</a>
   * </p>
   *
   * @param <T>
   * @param object
   * @return
   */
  <T> int merge(T object);

  /**
   * Merges by objects on the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   * @see #merge(Object)
   */
  <T> int[] merge(@SuppressWarnings("unchecked") T... objects);

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
   * Reads all rows from the table indicated by object class.
   *
   * @param <T>
   * @param type
   * @return
   */
  <T> List<T> readAll(Class<T> type);



  /**
   * Reads an object by its primary keys from the table indicated by object class.
   *
   * @param <T>
   * @param type
   * @param primaryKeyValues
   * @return
   */
  <T> T readByPrimaryKey(Class<T> type, Object... primaryKeyValues);

  /**
   * Reads an object from the database.
   *
   * @param <T>
   * @param type
   * @param sql
   * @return
   */
  <T> T readFirst(Class<T> type, ParameterizedSql sql);

  /**
   * Reads an object from the database.
   *
   * @param <T>
   * @param type
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   * @return
   */
  <T> T readFirst(Class<T> type, String sql, Object... parameters);

  /**
   * Reads a list of objects from the database by mapping the results of the parameterized SQL query
   * into instances of the given object class. Only the columns returned from the SQL query will be
   * set into the object instance.
   *
   * <b>Example: </b>
   *
   * <pre>
   * ParameterizedSql sql = ParameterizedSql.from("select * from customer");
   * sorm.readList(Customer.class, sql);
   * </pre>
   *
   * @param <T>
   * @param type
   * @param sql
   * @return
   */

  <T> List<T> readList(Class<T> type, ParameterizedSql sql);

  /**
   * Reads a list of objects from the database by mapping the results of the parameterized SQL query
   * into instances of the given object class. Only the columns returned from the SQL query will be
   * set into the object instance.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(PreparedStatement, Object[])}
   *
   */
  <T> List<T> readList(Class<T> type, String sql, Object... parameters);

  /**
   * See {@link #readMapFirst(String, Object...)}
   *
   * @param sql
   * @return
   */
  Map<String, Object> readMapFirst(ParameterizedSql sql);


  /**
   * Reads a first row from the database by mapping the results of the SQL query into an instance of
   * {@link java.util.Map}.
   * <p>
   * Letter case of the key in the Map depends on
   * {@link ColumnValueToJavaObjectConverters#toSingleMap}
   * <p>
   * Types returned from the database will be converted to Java types in the map according with the
   * correspondence defined in
   * {@link ColumnValueToJavaObjectConverters#toSingleMap(SormResultSet, List, List)}.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(PreparedStatement, Object... )}
   *
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   */
  Map<String, Object> readMapFirst(String sql, Object... parameters);


  /**
   * See {@link #readMapList(String, Object...)}
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
   * Letter case of the key in the Map depends on
   * {@link ColumnValueToJavaObjectConverters#toSingleMap}
   *
   * <p>
   * Types of value returned from the database will be converted to Java types in the map according
   * with the correspondence defined in
   * {@link ColumnValueToJavaObjectConverters#toSingleMap(SormResultSet, List, List)}.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(PreparedStatement, Object... )}
   *
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   */
  List<Map<String, Object>> readMapList(String sql, Object... parameters);

  /**
   * See {@link #readMapOne(String, Object...)}
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
   * Letter case of the key in the Map depends on
   * {@link ColumnValueToJavaObjectConverters#toSingleMap}
   * <p>
   * Types of value returned from the database will be converted to Java types in the map according
   * with the correspondence defined in
   * {@link ColumnValueToJavaObjectConverters#toSingleMap(SormResultSet, List, List)}.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(PreparedStatement, Object... )}
   *
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   */
  Map<String, Object> readMapOne(String sql, Object... parameters);

  /**
   * Reads only one object from the database.
   *
   * @param <T> the type to map the result set rows to
   * @param type the type to map the result set rows to
   * @param sql
   * @return
   */
  <T> T readOne(Class<T> type, ParameterizedSql sql);


  /**
   * Reads only one object from the database.
   *
   * @param <T> the type to map the result set rows to
   * @param type the type to map the result set rows to
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   * @return
   */
  <T> T readOne(Class<T> type, String sql, Object... parameters);


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

  @Experimental
  <T1, T2> List<Tuple2<T1, T2>> join(Class<T1> t1, Class<T2> t2, String onCondition);

  @Experimental
  <T1, T2, T3> List<Tuple3<T1, T2, T3>> join(Class<T1> t1, Class<T2> t2, String t1T2OnCondition,
      Class<T3> t3, String t2T3OnCondition);

  @Experimental
  <T1, T2> List<Tuple2<T1, T2>> leftJoin(Class<T1> t1, Class<T2> t2, String onCondition);

  @Experimental
  <T1, T2, T3> List<Tuple3<T1, T2, T3>> leftJoin(Class<T1> t1, Class<T2> t2, String t1T2OnCondition,
      Class<T3> t3, String t2T3OnCondition);


  /**
   *
   * Updates by objects on the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] update(List<T> objects);

  /**
   * Updates by an object on the table corresponding to the class of the given object.
   *
   * @param <T>
   * @param object
   * @return
   */
  <T> int update(T object);

  /**
   * Updates by objects on the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] update(@SuppressWarnings("unchecked") T... objects);

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
