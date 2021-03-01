package org.nkjmlab.sorm4j;

import java.sql.PreparedStatement;
import java.util.List;
import org.nkjmlab.sorm4j.config.PreparedStatementParametersSetter;

/**
 * The main interface of Reading functions of ORM reader.
 *
 * This interface based on <a href=
 * "https://github.com/r5v9/persist/blob/master/src/main/net/sf/persist/Persist.java">Persist.java</a>
 *
 * @author nkjm
 *
 */
public interface OrmReader {

  <T> List<T> readAll(Class<T> objectClass);

  <T> LazyResultSet<T> readAllLazy(Class<T> objectClass);

  /**
   * Reads an object from the database by its primary keys.
   */
  <T> T readByPrimaryKey(Class<T> objectClass, Object... primaryKeyValues);

  <T> T readOne(Class<T> objectClass, String sql, Object... parameters);

  <T> T readFirst(Class<T> objectClass, String sql, Object... parameters);

  /**
   * Returns an {@link org.nkjmlab.sorm4j.LazyResultSet} and convert it to Stream, List, and so on.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link PreparedStatementParametersSetter#setParameters(PreparedStatement, int[], Object[])}
   *
   * @since 1.0
   */
  <T> LazyResultSet<T> readLazy(Class<T> objectClass, String sql, Object... parameters);

  /**
   * Reads a list of objects from the database by mapping the results of the parameterized SQL query
   * into instances of the given object class. Only the columns returned from the SQL query will be
   * set into the object instance.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link PreparedStatementParametersSetter#setParameters(PreparedStatement, int[], Object[])}
   *
   */
  <T> List<T> readList(Class<T> objectClass, String sql, Object... parameters);



  <T> T readOne(Class<T> objectClass, SqlStatement sql);

  <T> T readFirst(Class<T> objectClass, SqlStatement sql);

  <T> LazyResultSet<T> readLazy(Class<T> objectClass, SqlStatement sql);

  <T> List<T> readList(Class<T> objectClass, SqlStatement sql);



}
