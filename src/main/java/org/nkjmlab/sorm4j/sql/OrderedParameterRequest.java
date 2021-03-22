package org.nkjmlab.sorm4j.sql;

import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.core.sql.OrderedParameterRequestImpl;

public interface OrderedParameterRequest extends OrderedParameterSql, Request {

  @Override
  OrderedParameterRequest addParameter(Object parameter);

  @Override
  OrderedParameterRequest addParameter(Object... parameters);

  static OrderedParameterRequest from(SqlExecutor sqlExecutor, String sql) {
    return new OrderedParameterRequestImpl(sqlExecutor, sql);
  }

}
