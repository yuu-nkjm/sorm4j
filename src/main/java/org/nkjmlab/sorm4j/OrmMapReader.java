package org.nkjmlab.sorm4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.result.LazyResultSet;
import org.nkjmlab.sorm4j.sqlstatement.SqlStatement;


/**
 * A interface for getting result of query as {@link Map}.
 *
 * @author nkjm
 *
 */
public interface OrmMapReader {
  /**
   * See {@link OrmMapReader#readMapFirst(String, Object...)}
   *
   * @param sql
   * @return
   */
  Map<String, Object> readMapFirst(SqlStatement sql);

  /**
   * Reads a first row from the database by mapping the results of the SQL query into an instance of
   * {@link java.util.Map}.
   * <p>
   * Types returned from the database will be converted to Java types in the map according with the
   * correspondence defined in {@link ResultSetConverter#toSingleMap(ResultSet, List, List)}.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParameterSetter#setParameters(PreparedStatement, Object... )}
   *
   */

  Map<String, Object> readMapFirst(String sql, Object... parameters);

  /**
   * See {@link OrmMapReader#readMapLazy(String, Object...)}
   *
   * @param sql
   * @return
   */
  LazyResultSet<Map<String, Object>> readMapLazy(SqlStatement sql);

  /**
   * Returns an {@link LazyResultSet} instance containing data from the execution of the provided
   * parametrized SQL and convert it to Stream, List, and so on.
   * <p>
   * Types returned from the database will be converted to Java types in the map according with the
   * correspondence defined in {@link ResultSetConverter#toSingleMap(ResultSet, List, List)}.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParameterSetter#setParameters(PreparedStatement, Object... )}
   *
   */
  LazyResultSet<Map<String, Object>> readMapLazy(String sql, Object... parameters);

  /**
   * See {@link OrmMapReader#readMapList(String, Object...)}
   *
   * @param sql
   * @return
   */
  List<Map<String, Object>> readMapList(SqlStatement sql);

  /**
   * Reads a list of objects from the database by mapping the SQL execution results to instances of
   * {@link java.util.Map} containing data from the execution of the provided parameterized SQL and
   * <p>
   * Types returned from the database will be converted to Java types in the map according with the
   * correspondence defined in {@link ResultSetConverter#toSingleMap(ResultSet, List, List)}.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParameterSetter#setParameters(PreparedStatement, Object... )}
   *
   */
  List<Map<String, Object>> readMapList(String sql, Object... parameters);

  /**
   * See {@link OrmMapReader#readMapOne(String, Object...)}
   *
   * @param sql
   * @return
   */
  Map<String, Object> readMapOne(SqlStatement sql);

  /**
   * Reads a first row from the database by mapping the results of the SQL query into an instance of
   * {@link java.util.Map}. If the given SQL statement gets non-unique result, {@link SormException}
   * is thrown.
   */
  Map<String, Object> readMapOne(String sql, Object... parameters);

}
