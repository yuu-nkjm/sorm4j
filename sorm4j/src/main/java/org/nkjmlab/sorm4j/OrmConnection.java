package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.common.LazyResultSet;
import org.nkjmlab.sorm4j.extension.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
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
   * Returns {@link LazyResultSet} represents all rows from the table indicated by object class.
   *
   * @param <T>
   * @param type
   * @return
   */
  <T> LazyResultSet<T> readAllLazy(Class<T> type);

  /**
   * Returns an {@link LazyResultSet}. It is able to convert to Stream, List, and so on.
   *
   * @param <T>
   * @param type
   * @param sql
   * @return
   */
  <T> LazyResultSet<T> readLazy(Class<T> type, ParameterizedSql sql);

  /**
   * Returns an {@link LazyResultSet}. It is able to convert to Stream, List, and so on.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(SormOptions,PreparedStatement, Object[])}
   *
   * @param <T>
   * @param type
   * @param sql
   * @param parameters
   * @return
   */
  <T> LazyResultSet<T> readLazy(Class<T> type, String sql, Object... parameters);

  /**
   * See {@link #readMapLazy(String, Object...)}
   *
   * @param sql
   * @return
   */
  LazyResultSet<Map<String, Object>> readMapLazy(ParameterizedSql sql);

  /**
   * Returns an {@link LazyResultSet} instance containing data from the execution of the provided
   * parameterized SQL and convert it to Stream, List, and so on.
   * <p>
   * Types returned from the database will be converted to Java types in the map according with the
   * correspondence defined in
   * {@link ColumnValueToJavaObjectConverters#toSingleMap(SormOptions, ResultSet, List, List)}.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(SormOptions, PreparedStatement, Object... )}
   *
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   */
  LazyResultSet<Map<String, Object>> readMapLazy(String sql, Object... parameters);

}
