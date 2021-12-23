package org.nkjmlab.sorm4j.basic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

/**
 * A interface for executing SQL with parameters.
 *
 * @author nkjm
 *
 */
public interface SqlExecutor {

  /**
   * Executes the query with the given PreparedStatement and applies the given RowMapper. If you
   * want to set parameters to a PreparedStatement object by yourself, you can use this method. You
   * can use your {@link ResultSetTraverser} or the object getting by
   * {@link Orm#getResultSetTraverser(Class)};
   *
   * @param <T>
   * @param statementSupplier
   * @param traverser
   * @return
   */
  @Experimental
  <T> T executeQuery(FunctionHandler<Connection, PreparedStatement> statementSupplier,
      ResultSetTraverser<T> traverser);


  /**
   * Executes the query with the given PreparedStatement and applies the given RowMapper. If you
   * want to set parameters to a PreparedStatement object by yourself, you can use this method. You
   * can use your {@link RowMapper} or the object getting by {@link Orm#getRowMapper(Class)};
   *
   * @param <T>
   * @param statementSupplier
   * @param rowMapper
   * @return
   */
  @Experimental
  <T> List<T> executeQuery(FunctionHandler<Connection, PreparedStatement> statementSupplier,
      RowMapper<T> rowMapper);


  /**
   * Executes a query and apply the given {@link ResultSetTraverser} to the returned result set.
   * <p>
   * This method wraps {@link PreparedStatement#executeQuery(String)}
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(SormOptions, PreparedStatement, Object...)}
   *
   * @param <T>
   * @param sql SQL code to be executed.
   * @param traverser
   * @return
   */
  <T> T executeQuery(ParameterizedSql sql, ResultSetTraverser<T> traverser);


  /**
   * Executes a query and apply the given {@link RowMapper} to the each row in returned result set.
   *
   * @param <T>
   * @param sql
   * @param mapper
   * @return
   */
  <T> List<T> executeQuery(ParameterizedSql sql, RowMapper<T> mapper);

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
  int executeUpdate(ParameterizedSql sql);


}
