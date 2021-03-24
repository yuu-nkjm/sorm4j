package org.nkjmlab.sorm4j.sql;

import java.util.Map;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.internal.sql.NamedParameterRequestImpl;

public interface NamedParameterRequest extends NamedParameterSql, Request {


  @Override
  NamedParameterRequest bindAll(Map<String, Object> keyValuePairOfNamedParameters);

  @Override
  NamedParameterRequest bind(String key, Object value);

  static NamedParameterRequest from(SqlExecutor sqlExecutor, String sql) {
    return new NamedParameterRequestImpl(sqlExecutor, sql);
  }

}
