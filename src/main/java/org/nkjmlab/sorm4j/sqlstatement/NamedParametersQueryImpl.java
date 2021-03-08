package org.nkjmlab.sorm4j.sqlstatement;

import java.util.Map;
import org.nkjmlab.sorm4j.TypedOrmReader;

public class NamedParametersQueryImpl<T> extends AbstQuery<T> implements NamedParametersQuery<T> {

  private final SqlWithNamedParameters sqlWithNamedParameters;

  NamedParametersQueryImpl(TypedOrmReader<T> conn, String sql) {
    super(conn);
    this.sqlWithNamedParameters = SqlWithNamedParameters.from(sql);
  }


  @Override
  public NamedParametersQuery<T> bindAll(Map<String, Object> namedParams) {
    sqlWithNamedParameters.bindAll(namedParams);
    return this;
  }

  @Override
  public NamedParametersQuery<T> bind(String key, Object value) {
    sqlWithNamedParameters.bind(key, value);
    return this;
  }

  @Override
  public SqlStatement toSqlStatement() {
    return sqlWithNamedParameters.toSqlStatement();
  }


}
