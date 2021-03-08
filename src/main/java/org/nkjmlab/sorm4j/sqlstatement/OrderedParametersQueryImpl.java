package org.nkjmlab.sorm4j.sqlstatement;

import org.nkjmlab.sorm4j.TypedOrmReader;

public class OrderedParametersQueryImpl<T> extends AbstQuery<T>
    implements OrderedParametersQuery<T> {

  private SqlWithOrderedParameters sqlWithOrderedParameters;

  public OrderedParametersQueryImpl(TypedOrmReader<T> conn, String sql) {
    super(conn);
    this.sqlWithOrderedParameters = SqlWithOrderedParameters.from(sql);
  }


  public OrderedParametersQuery<T> add(Object parameter) {
    sqlWithOrderedParameters.add(parameter);
    return this;
  }

  @Override
  public OrderedParametersQuery<T> add(Object... parameters) {
    sqlWithOrderedParameters.add(parameters);
    return this;
  }

  @Override
  public SqlStatement toSqlStatement() {
    return sqlWithOrderedParameters.toSqlStatement();
  }


}
