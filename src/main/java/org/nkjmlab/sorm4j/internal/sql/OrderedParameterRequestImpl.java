package org.nkjmlab.sorm4j.internal.sql;

import java.sql.ResultSet;
import java.util.List;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.helper.OrderedParameterRequest;

public class OrderedParameterRequestImpl implements OrderedParameterRequest {

  private final OrderedParameterSql sql;
  private final SqlExecutor sqlExecutor;

  public OrderedParameterRequestImpl(SqlExecutor sqlExecutor, String sql) {
    this.sql = OrderedParameterSql.from(sql);
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
  public OrderedParameterRequest addParameter(Object parameter) {
    sql.addParameter(parameter);
    return this;
  }

  @Override
  public OrderedParameterRequest addParameter(Object... parameters) {
    sql.addParameter(parameters);
    return this;
  }

}
