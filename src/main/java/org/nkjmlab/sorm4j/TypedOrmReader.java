package org.nkjmlab.sorm4j;

import java.sql.PreparedStatement;
import java.util.List;
import org.nkjmlab.sorm4j.mapping.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.result.LazyResultSet;
import org.nkjmlab.sorm4j.sqlstatement.SqlStatement;

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
  T readFirst(SqlStatement sql);

  /**
   * Reads an object from the database.
   *
   * @param sql
   * @param parameters
   * @return
   */
  T readFirst(String sql, Object... parameters);

  /**
   * Returns an {@link LazyResultSet}. It is able to convert to Stream, List, and so on.
   *
   * @param sql
   * @return
   */
  LazyResultSet<T> readLazy(SqlStatement sql);

  /**
   * Returns an {@link LazyResultSet}. It is able to convert to Stream, List, and so on.
   *
   * @param sql
   * @param parameters
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
  List<T> readList(SqlStatement sql);

  /**
   * Reads a list of objects from the database by mapping the results of the parameterized SQL query
   * into instances of the given object class. Only the columns returned from the SQL query will be
   * set into the object instance.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParameterSetter#setParameters(PreparedStatement, Object[])}
   *
   * @param sql
   * @param parameters
   * @return
   */
  List<T> readList(String sql, Object... parameters);

  /**
   * Reads only one object from the database.
   *
   * @param sql
   * @return
   */
  T readOne(SqlStatement sql);

  /**
   * Reads only one object from the database.
   *
   * @param sql
   * @param parameters
   * @return
   */
  T readOne(String sql, Object... parameters);

}
