package org.nkjmlab.sorm4j.sqlstatement;

import java.util.Map;


/**
 * Query with named parameters.
 *
 * @author nkjm
 *
 * @param <T>
 */
public interface NamedParameterQuery<T> extends TypedQuery<T>, NamedParameterSql {

  @Override
  NamedParameterQuery<T> bindAll(Map<String, Object> namedParams);

  @Override
  NamedParameterQuery<T> bind(String key, Object value);

}
