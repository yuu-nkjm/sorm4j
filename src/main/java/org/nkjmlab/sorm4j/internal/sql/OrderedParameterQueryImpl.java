package org.nkjmlab.sorm4j.internal.sql;

import org.nkjmlab.sorm4j.sql.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

/**
 * An implementation of {@link OrderedParameterQuery}
 *
 * @author nkjm
 *
 * @param <T>
 */
public class OrderedParameterQueryImpl<T> extends AbstractQuery<T>
    implements OrderedParameterQuery<T> {

  private final OrderedParameterSql orderedParameterSql;

  public OrderedParameterQueryImpl(QueryExecutor<T> executor, String sql) {
    super(executor);
    this.orderedParameterSql = OrderedParameterSql.from(sql);
  }

  @Override
  public OrderedParameterQuery<T> addParameter(Object parameter) {
    orderedParameterSql.addParameter(parameter);
    return this;
  }

  @Override
  public OrderedParameterQuery<T> addParameter(Object... parameters) {
    orderedParameterSql.addParameter(parameters);
    return this;
  }

  @Override
  public ParameterizedSql parse() {
    return orderedParameterSql.parse();
  }


  public static <T> OrderedParameterQuery<T> createFrom(QueryExecutor<T> executor, String sql) {
    return new OrderedParameterQueryImpl<>(executor, sql);
  }



}
