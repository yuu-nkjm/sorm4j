package org.nkjmlab.sorm4j.core.sqlstatement;

import org.nkjmlab.sorm4j.TypedOrmReader;
import org.nkjmlab.sorm4j.sqlstatement.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sqlstatement.OrderedParameterSql;
import org.nkjmlab.sorm4j.sqlstatement.SqlStatement;

/**
 * An implementation of {@link OrderedParameterQuery}
 *
 * @author nkjm
 *
 * @param <T>
 */
public class OrderedParameterQueryImpl<T> extends AbstTypedQuery<T>
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


  public static <T> OrderedParameterQuery<T> createFrom(TypedOrmReader<T> conn, String sql) {
    return new OrderedParameterQueryImpl<>(conn, sql);
  }


}
