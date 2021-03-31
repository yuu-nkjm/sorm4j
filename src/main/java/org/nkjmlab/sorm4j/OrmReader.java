package org.nkjmlab.sorm4j;

import java.sql.PreparedStatement;
import java.util.List;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.SqlStatement;

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
  <T> T readFirst(Class<T> objectClass, SqlStatement sql);

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
  <T> LazyResultSet<T> readLazy(Class<T> objectClass, SqlStatement sql);

  /**
   * Returns an {@link LazyResultSet}. It is able to convert to Stream, List, and so on.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParameterSetter#setParameters(PreparedStatement,Object[])}
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

  <T> List<T> readList(Class<T> objectClass, SqlStatement sql);

  /**
   * Reads a list of objects from the database by mapping the results of the parameterized SQL query
   * into instances of the given object class. Only the columns returned from the SQL query will be
   * set into the object instance.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParameterSetter#setParameters(PreparedStatement, Object[])}
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
  <T> T readOne(Class<T> objectClass, SqlStatement sql);

  /**
   * Reads only one object from the database.
   *
   * @param <T>
   * @param objectClass
   * @param sql
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   * @return
   */
  <T> T readOne(Class<T> objectClass, String sql, Object... parameters);



}
