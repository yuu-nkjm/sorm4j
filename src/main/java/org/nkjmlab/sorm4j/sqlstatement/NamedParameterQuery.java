package org.nkjmlab.sorm4j.sqlstatement;

import java.util.Map;
import org.nkjmlab.sorm4j.TypedOrmReader;


public interface NamedParameterQuery<T> extends Query<T>, NamedParameterSql {


  @Override
  NamedParameterQuery<T> bindAll(Map<String, Object> namedParams);

  @Override
  NamedParameterQuery<T> bind(String key, Object value);

  static <T> NamedParameterQuery<T> createFrom(TypedOrmReader<T> conn, String sql) {
    return new NamedParameterQueryImpl<>(conn, sql);
  }

}
