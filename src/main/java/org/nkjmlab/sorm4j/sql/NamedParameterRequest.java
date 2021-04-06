package org.nkjmlab.sorm4j.sql;

import java.util.Map;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.sql.NamedParameterRequestImpl;


/**
 * An executable request with named parameters.
 *
 * @author nkjm
 *
 */
public interface NamedParameterRequest extends NamedParameterSql, Request {


  @Override
  NamedParameterRequest bindAll(Map<String, Object> keyValuePairOfNamedParameters);

  @Override
  NamedParameterRequest bind(String key, Object value);

  @Override
  @Experimental
  NamedParameterRequest bindBean(Object bean);

  /**
   * Creates a request from SQL string.
   *
   * @param sqlExecutor
   * @param sql
   * @return
   */
  static NamedParameterRequest from(SqlExecutor sqlExecutor, String sql) {
    return new NamedParameterRequestImpl(sqlExecutor, sql);
  }

}
