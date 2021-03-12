package org.nkjmlab.sorm4j.sqlstatement;

/**
 * Query with ordered parameters.
 *
 * @author nkjm
 *
 * @param <T>
 */
public interface OrderedParameterQuery<T> extends TypedQuery<T>, OrderedParameterSql {

  @Override
  OrderedParameterQuery<T> add(Object... parameters);

  @Override
  OrderedParameterQuery<T> add(Object parameter);

}
