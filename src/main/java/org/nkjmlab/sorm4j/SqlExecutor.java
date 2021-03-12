package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.sqlstatement.SqlStatement;

/**
 * A interface for executing SQL with parameters.
 *
 * @author nkjm
 *
 */
public interface SqlExecutor {

  /**
   * Executes a SQL statement and returns the result.
   * <p>
   * This method wraps {@link PreparedStatement#execute(String)}
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParameterSetter#setParameters(PreparedStatement, Object...)}
   *
   * @param sql SQL code to be executed.
   * @param parameters Parameters to be used in the PreparedStatement.
   */
  boolean execute(String sql, Object... parameters);

  /**
   * Executes a query and returns the result set.
   * <p>
   * This method wraps {@link PreparedStatement#executeQuery(String)}
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParameterSetter#setParameters(PreparedStatement, Object...)}
   *
   * @param sql SQL code to be executed.
   * @param parameters Parameters to be used in the PreparedStatement.
   */
  ResultSet executeQuery(String sql, Object... parameters);

  /**
   * Executes an update and returns the number of rows modified.
   * <p>
   * This method wraps {@link PreparedStatement#executeUpdate(String)}
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParameterSetter#setParameters(PreparedStatement, Object...)}
   *
   * @param sql SQL code to be executed.
   * @param parameters Parameters to be used in the PreparedStatement.
   */
  int executeUpdate(String sql, Object... parameters);

  /**
   * {@link #execute(String, Object...)}
   *
   * @param sql
   * @return
   */
  boolean execute(SqlStatement sql);

  /**
   * {@link #executeQuery(String, Object...)}
   *
   * @param sql
   * @return
   */
  ResultSet executeQuery(SqlStatement sql);

  /**
   * {@link #executeUpdate(String, Object...)}
   *
   * @param sql
   * @return
   */
  int executeUpdate(SqlStatement sql);

  /**
   * Gets {@link Connection}.
   *
   * @return
   */
  Connection getJdbcConnection();

}
