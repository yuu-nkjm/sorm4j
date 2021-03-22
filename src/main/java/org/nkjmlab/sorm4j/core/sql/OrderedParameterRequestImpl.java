package org.nkjmlab.sorm4j.core.sql;

import java.sql.ResultSet;
import java.util.List;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.sql.OrderedParameterRequest;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;
import org.nkjmlab.sorm4j.sql.SqlStatement;

public class OrderedParameterRequestImpl implements OrderedParameterRequest {

  private final OrderedParameterSql sql;
  private final SqlExecutor sqlExecutor;

  public OrderedParameterRequestImpl(SqlExecutor sqlExecutor, String sql) {
    this.sql = OrderedParameterSql.from(sql);
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
