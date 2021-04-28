package org.nkjmlab.sorm4j.sql.helper;

import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.internal.sql.OrderedParameterRequestImpl;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;

/**
 * An executable request with ordered parameters.
 *
 * @author nkjm
 *
 */
public interface OrderedParameterRequest extends OrderedParameterSql, Request {

  @Override
  OrderedParameterRequest addParameter(Object parameter);

  @Override
  OrderedParameterRequest addParameter(Object... parameters);

  /**
   * Creates a request from SQL string.
   *
   * @param sqlExecutor
   * @param sql
   * @return
   */
  static OrderedParameterRequest from(SqlExecutor sqlExecutor, String sql) {
    return new OrderedParameterRequestImpl(sqlExecutor, sql);
  }

}
