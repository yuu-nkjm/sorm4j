package org.nkjmlab.sorm4j.sqlstatement;

import java.util.Map;
import org.nkjmlab.sorm4j.TypedOrmReader;


public interface NamedParametersQuery<T> extends Query<T>, SqlWithNamedParameters {


  @Override
  NamedParametersQuery<T> bindAll(Map<String, Object> namedParams);

  @Override
  NamedParametersQuery<T> bind(String key, Object value);

  static <T> NamedParametersQuery<T> createFrom(TypedOrmReader<T> conn, String sql) {
    return new NamedParametersQueryImpl<>(conn, sql);
  }

}
