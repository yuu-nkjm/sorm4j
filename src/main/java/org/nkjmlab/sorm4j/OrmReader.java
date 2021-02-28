package org.nkjmlab.sorm4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.config.JavaToSqlDataConverter;
import org.nkjmlab.sorm4j.config.SqlToJavaDataConverter;
import org.nkjmlab.sorm4j.helper.SqlStatement;

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

  <T> ReadResultSet<T> readAllLazy(Class<T> objectClass);

  /**
   * Reads an object from the database by its primary keys.
   */
  <T> T readByPrimaryKey(Class<T> objectClass, Object... primaryKeyValues);

  <T> T readFirst(Class<T> objectClass, String sql, Object... parameters);

  /**
   * Returns an {@link org.nkjmlab.sorm4j.ReadResultSet} and convert it to Stream, List, and so on.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link JavaToSqlDataConverter#setParameters(PreparedStatement, int[], Object[])}
   *
   * @since 1.0
   */
  <T> ReadResultSet<T> readLazy(Class<T> objectClass, String sql, Object... parameters);

  /**
   * Reads a list of objects from the database by mapping the results of the parameterized SQL query
   * into instances of the given object class. Only the columns returned from the SQL query will be
   * set into the object instance.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link JavaToSqlDataConverter#setParameters(PreparedStatement, int[], Object[])}
   *
   */
  <T> List<T> readList(Class<T> objectClass, String sql, Object... parameters);

  /**
   * Reads a first row from the database by mapping the results of the SQL query into an instance of
   * {@link java.util.Map}.
   * <p>
   * Types returned from the database will be converted to Java types in the map according with the
   * correspondence defined in
   * {@link SqlToJavaDataConverter#getValueBySqlType(ResultSet, int, int)}.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link JavaToSqlDataConverter#setParameters(PreparedStatement, int[], Object[])}
   *
   * @since 1.0
   */

  Map<String, Object> readMapFirst(String sql, Object... parameters);


  /**
   * Returns an {@link org.nkjmlab.sorm4j.ReadResultSet} instance containing data from the execution
   * of the provided parametrized SQL and convert it to Stream, List, and so on.
   * <p>
   * Types returned from the database will be converted to Java types in the map according with the
   * correspondence defined in
   * {@link SqlToJavaDataConverter#getValueBySqlType(ResultSet, int, int)}.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link JavaToSqlDataConverter#setParameters(PreparedStatement, int[], Object[])}
   *
   * @since 1.0
   */
  ReadResultSet<Map<String, Object>> readMapLazy(String sql, Object... parameters);

  /**
   * Reads a list of objects from the database by mapping the SQL execution results to instances of
   * {@link java.util.Map} containing data from the execution of the provided parametrized SQL and
   * <p>
   * Types returned from the database will be converted to Java types in the map according with the
   * correspondence defined in
   * {@link SqlToJavaDataConverter#getValueBySqlType(ResultSet, int, int)}.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link JavaTOSqlDataConverter#setParameters(PreparedStatement, int[], Object[])}
   *
   * @since 1.0
   */
  List<Map<String, Object>> readMapList(String sql, Object... parameters);


  <T> T readFirst(Class<T> objectClass, SqlStatement sql);

  <T> ReadResultSet<T> readLazy(Class<T> objectClass, SqlStatement sql);

  <T> List<T> readList(Class<T> objectClass, SqlStatement sql);

  Map<String, Object> readMapFirst(SqlStatement sql);

  ReadResultSet<Map<String, Object>> readMapLazy(SqlStatement sql);

  List<Map<String, Object>> readMapList(SqlStatement sql);



}
