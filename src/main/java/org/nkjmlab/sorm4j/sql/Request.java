package org.nkjmlab.sorm4j.sql;

import java.sql.ResultSet;
import java.util.List;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.RowMapper;


/**
 * An executable request
 *
 * @author nkjm
 *
 */
public interface Request {

  /**
   * Executes a query and apply the given handler to the returned result set.
   *
   * @param <T>
   * @param resultSetHandler
   * @return
   */
  <T> T executeQuery(FunctionHandler<ResultSet, T> resultSetHandler);


  /**
   * Executes a query and apply the given mapper to the each row in returned result set.
   *
   * @param <T>
   * @param rowMapper
   * @return
   */
  <T> List<T> executeQuery(RowMapper<T> rowMapper);

  /**
   * Executes an update and returns the number of rows modified.
   *
   * @return
   */
  int executeUpdate();
}
