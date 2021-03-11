package org.nkjmlab.sorm4j.sqlstatement;

import org.nkjmlab.sorm4j.TypedOrmReader;

/**
 * An implementation of {@link OrderedParameterQuery}
 *
 * @author nkjm
 *
 * @param <T>
 */
public class OrderedParameterQueryImpl<T> extends AbstQuery<T>
    implements OrderedParameterQuery<T> {

  private OrderedParameterSql orderedParameterSql;

  public OrderedParameterQueryImpl(TypedOrmReader<T> conn, String sql) {
    super(conn);
    this.orderedParameterSql = OrderedParameterSql.from(sql);
  }


  @Override
  public OrderedParameterQuery<T> add(Object parameter) {
    orderedParameterSql.add(parameter);
    return this;
  }

  @Override
  public OrderedParameterQuery<T> add(Object... parameters) {
    orderedParameterSql.add(parameters);
    return this;
  }

  @Override
  public SqlStatement toSqlStatement() {
    return orderedParameterSql.toSqlStatement();
  }


}
