package org.nkjmlab.sorm4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.result.LazyResultSet;
import org.nkjmlab.sorm4j.sql.result.Tuple2;
import org.nkjmlab.sorm4j.sql.result.Tuple3;

/**
 * The interface of reading functions of object-relation mapping.
 *
 * @author nkjm
 *
 */
public interface OrmReader {

  /**
   * Reads all rows from the table indicated by object class.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  <T> List<T> readAll(Class<T> objectClass);

  /**
   * Returns {@link LazyResultSet} represents all rows from the table indicated by object class.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  <T> LazyResultSet<T> readAllLazy(Class<T> objectClass);

  /**
   * Reads an object by its primary keys from the table indicated by object class.
   *
   * @param <T>
   * @param objectClass
   * @param primaryKeyValues
   * @return
   */
  <T> T readByPrimaryKey(Class<T> objectClass, Object... primaryKeyValues);

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
   * Returns an {@link LazyResultSet}. It is able to convert to Stream, List, and so on.
   *
   * @param <T>
   * @param objectClass
   * @param sql
   * @return
   */
  <T> LazyResultSet<T> readLazy(Class<T> objectClass, ParameterizedSql sql);

  /**
   * Returns an {@link LazyResultSet}. It is able to convert to Stream, List, and so on.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(SormOptions,PreparedStatement, Object[])}
   *
   *
   */
  <T> LazyResultSet<T> readLazy(Class<T> objectClass, String sql, Object... parameters);

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
   * @param <T>
   * @param objectClass
   * @return
   */
  <T> RowMapper<T> getRowMapper(Class<T> objectClass);

  /**
   * Gets function which traverses and maps the all the rows in the given resultSet to an object
   * list.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  <T> ResultSetTraverser<List<T>> getResultSetTraverser(Class<T> objectClass);



  /**
   * Returns the object which has same primary key exists or not.
   *
   * @param object
   * @return
   */
  <T> boolean exists(T object);

}
