package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.nkjmlab.sorm4j.common.FunctionHandler;
import org.nkjmlab.sorm4j.common.ParameterizedSql;
import org.nkjmlab.sorm4j.common.Tuple.Tuple2;
import org.nkjmlab.sorm4j.common.Tuple.Tuple3;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.context.TableSql;
import org.nkjmlab.sorm4j.context.metadata.TableMetaData;
import org.nkjmlab.sorm4j.mapping.ResultSetTraverser;
import org.nkjmlab.sorm4j.mapping.RowMapper;
import org.nkjmlab.sorm4j.mapping.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.result.InsertResult;
import org.nkjmlab.sorm4j.result.ResultSetStream;
import org.nkjmlab.sorm4j.result.RowMap;
import org.nkjmlab.sorm4j.result.jdbc.JdbcDatabaseMetaData;

/**
 * Main API for object relation mapping.
 *
 * @author nkjm
 */
public interface Orm {

  /**
   * Gets the context of this object.
   *
   * @return
   */
  SormContext getContext();

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
   * Deletes all objects in the table corresponding to the given class.
   *
   * @param <T> the type to indicate the unique table.
   * @param type the type to indicate the unique table.
   * @return
   */
  <T> int deleteAll(Class<T> type);

  /**
   * Deletes all objects in the table corresponding to the given table name.
   *
   * @param tableName
   * @return
   */
  int deleteAllIn(String tableName);

  /**
   * Deletes a row from the table corresponding to the class the by primary key.
   *
   * @param <T>
   * @param primaryKeyValues the order should be the same as the column order.
   * @return
   */
  <T> int deleteByPrimaryKey(Class<T> type, Object... primaryKeyValues);

  /**
   * Deletes a row from the table corresponding to the table name the by primary key.
   *
   * @param <T>
   * @param tableName
   * @param primaryKeyValues the order should be the same as the column order.
   * @return
   */
  <T> int deleteByPrimaryKeyIn(String tableName, Object... primaryKeyValues);

  /**
   * Deletes objects in the table of the given table name.
   *
   * @param <T> the object's element type.
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] deleteIn(String tableName, List<T> objects);

  /**
   * Deletes object in the table of the given table name.
   *
   * @param <T> the object's type.
   * @param tableName
   * @param object
   * @return
   */
  <T> int deleteIn(String tableName, T object);

  /**
   * Deletes objects in the table of the given table name.
   *
   * @param <T> the object's element type.
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] deleteIn(String tableName, @SuppressWarnings("unchecked") T... objects);

  boolean execute(ParameterizedSql sql);

  boolean execute(String sql, Object... parameters);

  /**
   * Executes the query with the given PreparedStatement and applies the given {@link
   * ResultSetTraverser}. If you want to set parameters to a PreparedStatement object by yourself,
   * you can use this method. You can use your {@link ResultSetTraverser} or the object getting by
   * {@link Orm#getResultSetTraverser(Class)};
   *
   * @param <T>
   * @param statementSupplier initialize and supplies PreparedStatement
   * @param traverser
   * @return
   */
  <T> T executeQuery(
      FunctionHandler<Connection, PreparedStatement> statementSupplier,
      ResultSetTraverser<T> traverser);

  /**
   * Executes the query with the given PreparedStatement and applies the given {@link RowMapper}. If
   * you want to set parameters to a PreparedStatement object by yourself, you can use this method.
   * You can use your {@link RowMapper} or the object getting by {@link Orm#getRowMapper(Class)};
   *
   * @param <T>
   * @param statementSupplier
   * @param rowMapper
   * @return
   */
  <T> List<T> executeQuery(
      FunctionHandler<Connection, PreparedStatement> statementSupplier, RowMapper<T> rowMapper);

  /**
   * Executes a query and apply the given {@link ResultSetTraverser} to the returned result set.
   *
   * <p>This method wraps {@link PreparedStatement#executeQuery(String)}
   *
   * <p>Parameters will be set according with the correspondence defined in {@link
   * SqlParametersSetter#setParameters(PreparedStatement, Object...)}
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
   * {@link #executeUpdate(String, Object...)}
   *
   * @param sql
   * @return
   */
  int executeUpdate(ParameterizedSql sql);

  /**
   * Executes an update and returns the number of rows modified.
   *
   * <p>This method wraps {@link PreparedStatement#executeUpdate(String)}
   *
   * <p>Parameters will be set according with the correspondence defined in {@link
   * SqlParametersSetter#setParameters(PreparedStatement, Object...)}
   *
   * @param sql SQL code to be executed.
   * @param parameters Parameters to be used in the PreparedStatement.
   */
  int executeUpdate(String sql, Object... parameters);

  /**
   * Returns the object which has same primary key exists or not.
   *
   * @param <T> the object's type.
   * @param tableName
   * @param object
   * @return
   */
  <T> boolean existsIn(String tableName, T object);

  /**
   * Returns the object which has same primary key exists or not.
   *
   * @param <T> the object's type which is mapped to the unique table.
   * @param object
   * @return
   */
  <T> boolean exists(T object);

  /**
   * @param <T>
   * @param tableName
   * @param primaryKeyValues the order should be the same as the column order.
   * @return
   */
  <T> boolean existsByPrimaryKeyIn(String tableName, Object... primaryKeyValues);

  /**
   * @param <T>
   * @param type
   * @param primaryKeyValues the order should be the same as the column order.
   * @return
   */
  <T> boolean existsByPrimaryKey(Class<T> type, Object... primaryKeyValues);

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
   * Gets table metadata corresponding to the given object class.
   *
   * @param type
   * @return
   */
  TableMetaData getTableMetaData(Class<?> type);

  /**
   * Gets table metadata.
   *
   * @param tableName
   * @return
   */
  TableMetaData getTableMetaData(String tableName);

  /**
   * Gets table metadata corresponding to the given object class.
   *
   * @param type
   * @return
   */
  TableSql getTableSql(Class<?> type);

  /**
   * Gets table SQL to the given table name.
   *
   * @param tableName
   * @return
   */
  TableSql getTableSql(String tableName);

  /**
   * Gets JDBC database metadata.
   *
   * @return
   */
  JdbcDatabaseMetaData getJdbcDatabaseMetaData();

  /**
   * Gets table name corresponding to the given object class.
   *
   * @param type
   * @return
   */
  String getTableName(Class<?> type);

  /**
   * Inserts objects in the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] insert(List<T> objects);

  /**
   * Inserts object in the table corresponding to the class of the given object.
   *
   * @param <T>
   * @param object
   * @return
   */
  <T> int insert(T object);

  /**
   * Insert objects in the table corresponding to the class of the given objects.
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
  <T> InsertResult insertAndGet(List<T> objects);

  /**
   * Inserts an object and get the result.
   *
   * @param <T>
   * @param object
   * @return
   */
  <T> InsertResult insertAndGet(T object);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> InsertResult insertAndGet(@SuppressWarnings("unchecked") T... objects);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> InsertResult insertAndGetIn(String tableName, List<T> objects);

  /**
   * Inserts an object and get the insert result.
   *
   * @param <T>
   * @param tableName
   * @param object
   * @return
   */
  <T> InsertResult insertAndGetIn(String tableName, T object);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> InsertResult insertAndGetIn(String tableName, @SuppressWarnings("unchecked") T... objects);

  /**
   * This method is experimental.
   *
   * @param tableName
   * @param result
   * @return
   */
  int[] insertMapInto(String tableName, List<RowMap> result);

  /**
   * @param tableName
   * @param object
   * @return
   */
  int insertMapInto(String tableName, RowMap object);

  /**
   * This method is experimental.
   *
   * @param tableName
   * @param objects
   * @return
   */
  int[] insertMapInto(String tableName, RowMap... objects);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] insertInto(String tableName, List<T> objects);

  /**
   * Inserts an object and get the insert result.
   *
   * @param <T>
   * @param tableName
   * @param object
   * @return
   */
  <T> int insertInto(String tableName, T object);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] insertInto(String tableName, @SuppressWarnings("unchecked") T... objects);

  <T1, T2> List<Tuple2<T1, T2>> join(Class<T1> t1, Class<T2> t2, String sql, Object... parameters);

  <T1, T2, T3> List<Tuple3<T1, T2, T3>> join(
      Class<T1> t1, Class<T2> t2, Class<T3> t3, String sql, Object... parameters);

  <T1, T2> List<Tuple2<T1, T2>> joinOn(Class<T1> t1, Class<T2> t2, String onCondition);

  <T1, T2> List<Tuple2<T1, T2>> joinUsing(Class<T1> t1, Class<T2> t2, String... columns);

  <T1, T2, T3> List<Tuple3<T1, T2, T3>> joinOn(
      Class<T1> t1, Class<T2> t2, Class<T3> t3, String t1T2OnCondition, String t2T3OnCondition);

  <T1, T2> List<Tuple2<T1, T2>> leftJoinOn(Class<T1> t1, Class<T2> t2, String onCondition);

  <T1, T2, T3> List<Tuple3<T1, T2, T3>> leftJoinOn(
      Class<T1> t1, Class<T2> t2, Class<T3> t3, String t1T2OnCondition, String t2T3OnCondition);

  /**
   * Merges by objects in the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   * @see #merge(Object)
   */
  <T> int[] merge(List<T> objects);

  /**
   * Merges by an object in the table corresponding to the class of the given object.
   *
   * <p>Merge methods execute a SQL sentence as MERGE INTO of the H2 grammar. This operation may be
   * not working the other database system.
   *
   * <p>See, <a href="http://www.h2database.com/html/commands.html#merge_into">MERGE INTO -
   * Commands</a>
   *
   * @param <T>
   * @param object
   * @return
   */
  <T> int merge(T object);

  /**
   * Merges by objects in the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   * @see #merge(Object)
   */
  <T> int[] merge(@SuppressWarnings("unchecked") T... objects);

  /**
   * Merges by objects in the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] mergeIn(String tableName, List<T> objects);

  /**
   * Merges by an object in the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param object
   * @return
   */
  <T> int mergeIn(String tableName, T object);

  /**
   * Merges by objects in the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] mergeIn(String tableName, @SuppressWarnings("unchecked") T... objects);

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
   *     parameter) could not be used.
   * @param parameters are ordered parameter.
   * @return
   */
  <T> T readFirst(Class<T> type, String sql, Object... parameters);

  /**
   * Reads a list of objects from the database by mapping the results of the parameterized SQL query
   * into instances of the given object class. Only the columns returned from the SQL query will be
   * set into the object instance.
   *
   * <p><b>Example: </b>
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
   *
   * <p>Parameters will be set according with the correspondence defined in {@link
   * SqlParametersSetter#setParameters(PreparedStatement, Object[])}
   */
  <T> List<T> readList(Class<T> type, String sql, Object... parameters);

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
   *     parameter) could not be used.
   * @param parameters are ordered parameter.
   * @return
   */
  <T> T readOne(Class<T> type, String sql, Object... parameters);

  /**
   * Reads results as List of {@link Tuple3} for reading JOIN SQL results typically.
   *
   * @see {@link OrmColumnAliasPrefix} for use column alias prefix.
   * @param <T1>
   * @param <T2>
   * @param <T3>
   * @param t1
   * @param t2
   * @param t3
   * @param sql
   * @return
   */
  <T1, T2, T3> List<Tuple3<T1, T2, T3>> readTupleList(
      Class<T1> t1, Class<T2> t2, Class<T3> t3, ParameterizedSql sql);

  /**
   * Reads results as List of {@link Tuple3} for reading JOIN SQL results typically.
   *
   * @see {@link OrmColumnAliasPrefix} for use column alias prefix.
   * @param <T1>
   * @param <T2>
   * @param t1
   * @param t2
   * @param sql
   * @param parameters
   * @return
   */
  <T1, T2, T3> List<Tuple3<T1, T2, T3>> readTupleList(
      Class<T1> t1, Class<T2> t2, Class<T3> t3, String sql, Object... parameters);

  /**
   * Reads results as List of {@link Tuple2} for reading JOIN SQL results typically.
   *
   * @see {@link OrmColumnAliasPrefix} for use column alias prefix.
   * @param <T1>
   * @param <T2>
   * @param t1
   * @param t2
   * @param sql
   * @return
   */
  <T1, T2> List<Tuple2<T1, T2>> readTupleList(Class<T1> t1, Class<T2> t2, ParameterizedSql sql);

  /**
   * Reads results as List of {@link Tuple2} for reading JOIN SQL results typically.
   *
   * @see {@link OrmColumnAliasPrefix} for use column alias prefix.
   * @param <T1>
   * @param <T2>
   * @param t1
   * @param t2
   * @param sql
   * @param parameters
   * @return
   */
  <T1, T2> List<Tuple2<T1, T2>> readTupleList(
      Class<T1> t1, Class<T2> t2, String sql, Object... parameters);

  /**
   * Reads all rows from the table indicated by object class.
   *
   * @param <T>
   * @param type
   * @return
   */
  <T> List<T> selectAll(Class<T> type);

  /**
   * Reads an object by its primary keys from the table indicated by object class.
   *
   * @param <T>
   * @param type
   * @param primaryKeyValues the order should be the same as the column order.
   * @return
   */
  <T> T selectByPrimaryKey(Class<T> type, Object... primaryKeyValues);

  /**
   * Updates with objects in the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] update(List<T> objects);

  /**
   * Updates with an object in the table corresponding to the class of the given object.
   *
   * @param <T>
   * @param object
   * @return
   */
  <T> int update(T object);

  /**
   * Updates with objects in the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] update(@SuppressWarnings("unchecked") T... objects);

  /**
   * Updates with map in the table corresponding to the class. the map should not be included
   * primary keys.
   *
   * @param <T>
   * @param clazz
   * @param object should not include primary keys.
   * @param primaryKeyValues the order should be the same as the column order.
   * @return
   */
  <T> int updateByPrimaryKey(Class<T> clazz, RowMap object, Object... primaryKeyValues);

  /**
   * Updates with map in the table corresponding to the given table name. the map should not be
   * included primary keys.
   *
   * @param tableName
   * @param object should not include primary keys.
   * @param primaryKeyValues the order should be the same as the column order.
   * @return
   */
  int updateByPrimaryKeyIn(String tableName, RowMap object, Object... primaryKeyValues);

  /**
   * Updates with objects in the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] updateWith(String tableName, List<T> objects);

  /**
   * Updates with an object in the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param object
   * @return
   */
  <T> int updateWith(String tableName, T object);

  /**
   * Updates with objects in the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] updateWith(String tableName, @SuppressWarnings("unchecked") T... objects);

  /**
   * Returns {@link ResultSetStream} represents all rows from the table indicated by object class.
   *
   * @param <T>
   * @param type
   * @return
   */
  <T> ResultSetStream<T> streamAll(Class<T> type);

  /**
   * Returns an {@link ResultSetStream}. It is able to convert to Stream, List, and so on. *
   *
   * @param <T>
   * @param type
   * @param sql
   * @return
   */
  <T> ResultSetStream<T> stream(Class<T> type, ParameterizedSql sql);

  /**
   * Returns an {@link ResultSetStream}. It is able to convert to Stream, List, and so on.
   *
   * <p>Parameters will be set according with the correspondence defined in {@link
   * SqlParametersSetter#setParameters(PreparedStatement,Object[])} *
   *
   * @param <T>
   * @param type
   * @param sql
   * @param parameters
   * @return
   */
  <T> ResultSetStream<T> stream(Class<T> type, String sql, Object... parameters);
}
