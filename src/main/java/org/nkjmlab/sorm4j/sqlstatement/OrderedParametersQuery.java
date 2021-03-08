package org.nkjmlab.sorm4j.sqlstatement;

import org.nkjmlab.sorm4j.TypedOrmReader;

public interface OrderedParametersQuery<T> extends Query<T>, SqlWithOrderedParameters {

  public static <T> OrderedParametersQuery<T> createFrom(TypedOrmReader<T> conn, String sql) {
    return new OrderedParametersQueryImpl<>(conn, sql);
  }

  @Override
  OrderedParametersQuery<T> add(Object... parameters);

}
