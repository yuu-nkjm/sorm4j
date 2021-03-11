package org.nkjmlab.sorm4j.sqlstatement;

import org.nkjmlab.sorm4j.TypedOrmReader;

public interface OrderedParameterQuery<T> extends Query<T>, OrderedParameterSql {

  static <T> OrderedParameterQuery<T> createFrom(TypedOrmReader<T> conn, String sql) {
    return new OrderedParameterQueryImpl<>(conn, sql);
  }

  @Override
  OrderedParameterQuery<T> add(Object... parameters);

  @Override
  OrderedParameterQuery<T> add(Object parameter);

}
