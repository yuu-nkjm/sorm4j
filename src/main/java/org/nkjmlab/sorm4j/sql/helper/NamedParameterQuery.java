package org.nkjmlab.sorm4j.sql.helper;

import java.util.Map;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.sql.NamedParameterSql;


/**
 * Query with named parameters.
 *
 * @author nkjm
 *
 * @param <T>
 */
public interface NamedParameterQuery<T> extends Query<T>, NamedParameterSql {

  @Override
  NamedParameterQuery<T> bindAll(Map<String, Object> namedParams);

  @Override
  NamedParameterQuery<T> bind(String key, Object value);

  @Override
  @Experimental
  NamedParameterQuery<T> bindBean(Object bean);

}
