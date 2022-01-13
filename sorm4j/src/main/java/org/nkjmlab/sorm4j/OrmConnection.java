package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.mapping.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.mapping.SqlParametersSetter;
import org.nkjmlab.sorm4j.result.ResultSetStream;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

/**
 * Main API for object relation mapping.
 *
 * @author nkjm
 *
 */
public interface OrmConnection extends Orm, AutoCloseable {

  /**
   * Gets {@link Connection}.
   *
   * @return
   */
  Connection getJdbcConnection();

  /**
   * Begin transaction. The isolation level is corresponding to
   * {@link Sorm.Builder#setTransactionIsolationLevel(int)}.
   */
  void begin();

  /**
   * Begins transaction with the given transaction isolation level.
   *
   * @param isolationLevel
   */

  void begin(int isolationLevel);


  /**
   * Closes the {@link java.sql.Connection Connection} associated with this instance.
   *
   * @see java.sql.Connection#close()
   *
   */
  @Override
  void close();

  /**
   * Commits the {@link java.sql.Connection Connection} associated with this instance.
   *
   * @see java.sql.Connection#commit()
   *
   */
  void commit();

  /**
   * Rollback the {@link java.sql.Connection Connection} associated with this instance.
   *
   * @see java.sql.Connection#rollback()
   *
   */
  void rollback();

  /**
   * Sets the auto commit behavior for the {@link java.sql.Connection Connection} associated with
   * this instance.
   *
   * @see java.sql.Connection#setAutoCommit(boolean)
   *
   */
  void setAutoCommit(boolean autoCommit);

  /**
   * Returns {@link ResultSetStream} represents all rows from the table indicated by object class.
   *
   * @param <T>
   * @param type
   * @return
   */
  <T> ResultSetStream<T> readAllStream(Class<T> type);

  /**
   * Returns an {@link ResultSetStream}. It is able to convert to Stream, List, and so on.
   *
   * @param <T>
   * @param type
   * @param sql
   * @return
   */
  <T> ResultSetStream<T> readStream(Class<T> type, ParameterizedSql sql);

  /**
   * Returns an {@link ResultSetStream}. It is able to convert to Stream, List, and so on.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(PreparedStatement,Object[])}
   *
   * @param <T>
   * @param type
   * @param sql
   * @param parameters
   * @return
   */
  <T> ResultSetStream<T> readStream(Class<T> type, String sql, Object... parameters);

  /**
   * See {@link #readMapStream(String, Object...)}
   *
   * @param sql
   * @return
   */
  ResultSetStream<Map<String, Object>> readMapStream(ParameterizedSql sql);

  /**
   * Returns an {@link ResultSetStream} instance containing data from the execution of the provided
   * parameterized SQL and convert it to Stream, List, and so on.
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
  ResultSetStream<Map<String, Object>> readMapStream(String sql, Object... parameters);

}
