package org.nkjmlab.sorm4j.sql;

/**
 * Query with ordered parameters.
 *
 * @author nkjm
 *
 * @param <T>
 */
public interface OrderedParameterQuery<T> extends Query<T>, OrderedParameterSql {

  @Override
  OrderedParameterQuery<T> add(Object... parameters);

  @Override
  OrderedParameterQuery<T> add(Object parameter);

}
