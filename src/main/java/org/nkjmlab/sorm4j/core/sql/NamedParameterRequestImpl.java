package org.nkjmlab.sorm4j.core.sql;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.sql.NamedParameterRequest;
import org.nkjmlab.sorm4j.sql.NamedParameterSql;
import org.nkjmlab.sorm4j.sql.SqlStatement;

public class NamedParameterRequestImpl implements NamedParameterRequest {

  private final NamedParameterSql sql;
  private final SqlExecutor sqlExecutor;

  public NamedParameterRequestImpl(SqlExecutor sqlExecutor, String sql) {
    this.sql = NamedParameterSql.from(sql);
    this.sqlExecutor = sqlExecutor;
  }

  public NamedParameterRequestImpl(SqlExecutor sqlExecutor, String sql, String prefix,
      String suffix) {
    this.sql = NamedParameterSql.from(sql, prefix, suffix);
    this.sqlExecutor = sqlExecutor;
  }

  @Override
  public <T> T executeQuery(FunctionHandler<ResultSet, T> resultSetHandler) {
    return sqlExecutor.executeQuery(toSqlStatement(), resultSetHandler);
  }

  @Override
  public <T> List<T> executeQuery(RowMapper<T> rowMapper) {
    return sqlExecutor.executeQuery(toSqlStatement(), rowMapper);
  }

  @Override
  public int executeUpdate() {
    return sqlExecutor.executeUpdate(toSqlStatement());
  }

  @Override
  public SqlStatement toSqlStatement() {
    return sql.toSqlStatement();
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


}
