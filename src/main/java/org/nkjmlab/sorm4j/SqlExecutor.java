package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.sql.SqlStatement;

/**
 * A interface for executing SQL with parameters.
 *
 * @author nkjm
 *
 */
public interface SqlExecutor {

  /**
   * Applys handler for {@link PreparedStatement} which has sets the given parameters.
   *
   * @param <T>
   * @param sql
   * @param handler
   * @return
   */
  @Experimental
  <T> T applyPreparedStatementHandler(SqlStatement sql,
      FunctionHandler<PreparedStatement, T> handler);

  /**
   * Accepts handler for {@link PreparedStatement} which has sets the given parameters.
   *
   * @param sql
   * @param handler
   */
  @Experimental
  void acceptPreparedStatementHandler(SqlStatement sql, ConsumerHandler<PreparedStatement> handler);


  /**
   * Executes a query and apply the given handler to the returned result set.
   * <p>
   * This method wraps {@link PreparedStatement#executeQuery(String)}
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(SormOptions, PreparedStatement, Object...)}
   *
   * @param <T>
   * @param sql SQL code to be executed.
   * @param resultSetHandler
   * @return
   */
  <T> T executeQuery(SqlStatement sql, FunctionHandler<ResultSet, T> resultSetHandler);


  /**
   * Executes a query and apply the given mapper to the each row in returned result set.
   *
   * @param <T>
   * @param sql
   * @param rowMapper
   * @return
   */
  <T> List<T> executeQuery(SqlStatement sql, RowMapper<T> rowMapper);

  /**
   * Executes an update and returns the number of rows modified.
   * <p>
   * This method wraps {@link PreparedStatement#executeUpdate(String)}
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(SormOptions, PreparedStatement, Object...)}
   *
   * @param sql SQL code to be executed.
   * @param parameters Parameters to be used in the PreparedStatement.
   */
  int executeUpdate(String sql, Object... parameters);


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
