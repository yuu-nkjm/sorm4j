package org.nkjmlab.sorm4j.typed;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import org.nkjmlab.sorm4j.ResultSetTraverser;
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.result.LazyResultSet;

/**
 * The typed interface of reading functions of object-relation mapping.
 *
 * @author nkjm
 *
 */

public interface TypedOrmReader<T> {

  /**
   * Reads all rows from the table indicated by object class.
   *
   * @return
   */
  List<T> readAll();

  /**
   * Returns {@link LazyResultSet} represents all rows from the table indicated by object class.
   *
   * @return
   */
  LazyResultSet<T> readAllLazy();

  /**
   * Reads an object by its primary keys from the table indicated by object class.
   *
   * @param primaryKeyValues
   * @return
   */
  T readByPrimaryKey(Object... primaryKeyValues);


  /**
   * Reads an object from the database.
   *
   * @param sql
   * @return
   */
  T readFirst(ParameterizedSql sql);

  /**
   * Reads an object from the database.
   *
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   * @return
   */
  T readFirst(String sql, Object... parameters);

  /**
   * Returns an {@link LazyResultSet}. It is able to convert to Stream, List, and so on.
   *
   * @param sql
   * @return
   */
  LazyResultSet<T> readLazy(ParameterizedSql sql);

  /**
   * Returns an {@link LazyResultSet}. It is able to convert to Stream, List, and so on.
   *
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   * @return
   */
  LazyResultSet<T> readLazy(String sql, Object... parameters);

  /**
   * Reads a list of objects from the database by mapping the results of the parameterized SQL query
   * into instances of the given object class. Only the columns returned from the SQL query will be
   * set into the object instance.
   *
   * @param sql
   * @return
   */
  List<T> readList(ParameterizedSql sql);

  /**
   * Reads a list of objects from the database by mapping the results of the parameterized SQL query
   * into instances of the given object class. Only the columns returned from the SQL query will be
   * set into the object instance.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(SormOptions, PreparedStatement, Object[])}
   *
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   * @return
   */
  List<T> readList(String sql, Object... parameters);

  /**
   * Reads only one object from the database.
   *
   * @param sql
   * @return
   */
  T readOne(ParameterizedSql sql);

  /**
   * Reads only one object from the database.
   *
   * @param sql
   * @param parameters
   * @return
   */
  T readOne(String sql, Object... parameters);

  /**
   * Gets a function which maps one row in the resultSet to an object. The method does not call
   * {@link ResultSet#next()}.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  RowMapper<T> getRowMapper();

  /**
   * Gets function which traverses and maps the all the rows in the given resultSet to an object
   * list.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  ResultSetTraverser<List<T>> getResultSetTraverser();


  /**
   * Returns the object which has same primary key exists or not.
   *
   * @param object
   * @return
   */
  boolean exists(T object);

}
