package org.nkjmlab.sorm4j.internal.sql;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.sql.NamedParameterSql;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.helper.NamedParameterRequest;

public class NamedParameterRequestImpl implements NamedParameterRequest {

  private final NamedParameterSql sql;
  private final SqlExecutor sqlExecutor;

  public NamedParameterRequestImpl(SqlExecutor sqlExecutor, String sql) {
    this.sql = NamedParameterSql.from(sql);
    this.sqlExecutor = sqlExecutor;
  }

  @Override
  public <T> T executeQuery(FunctionHandler<ResultSet, T> resultSetHandler) {
    return sqlExecutor.executeQuery(parse(), resultSetHandler);
  }

  @Override
  public <T> List<T> executeQuery(RowMapper<T> rowMapper) {
    return sqlExecutor.executeQuery(parse(), rowMapper);
  }

  @Override
  public int executeUpdate() {
    return sqlExecutor.executeUpdate(parse());
  }

  @Override
  public ParameterizedSql parse() {
    return sql.parse();
  }

  @Override
  public NamedParameterRequest bindAll(Map<String, Object> keyValuePairOfNamedParameters) {
    sql.bindAll(keyValuePairOfNamedParameters);
    return this;
  }

  @Override
  public NamedParameterRequest bind(String key, Object value) {
    sql.bind(key, value);
    return this;
  }

  @Override
  public NamedParameterRequest bindBean(Object bean) {
    sql.bindBean(bean);
    return this;
  }

}
