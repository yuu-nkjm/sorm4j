package org.nkjmlab.sorm4j.core.sqlstatement;

import org.nkjmlab.sorm4j.sql.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;
import org.nkjmlab.sorm4j.sql.SqlStatement;

/**
 * An implementation of {@link OrderedParameterQuery}
 *
 * @author nkjm
 *
 * @param <T>
 */
public class OrderedParameterTypedQueryImpl<T> extends AbstractQuery<T>
    implements OrderedParameterQuery<T> {

  private final OrderedParameterSql orderedParameterSql;

  public OrderedParameterTypedQueryImpl(QueryExecutor<T> executor, String sql) {
    super(executor);
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


  public static <T> OrderedParameterQuery<T> createFrom(QueryExecutor<T> executor, String sql) {
    return new OrderedParameterTypedQueryImpl<>(executor, sql);
  }



}
