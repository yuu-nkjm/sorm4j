package org.nkjmlab.sorm4j.helper;

import java.util.HashMap;
import java.util.Map;

public class SqlWithNamedParametersBuilder {
  private final String sql;
  private final Map<String, Object> namedParameters = new HashMap<>();

  public SqlWithNamedParametersBuilder(String sql) {
    this.sql = sql;
  }

  public SqlWithNamedParametersBuilder putAllParameters(Map<String, Object> namedParams) {
    this.namedParameters.putAll(namedParams);
    return this;
  }

  public SqlWithNamedParametersBuilder putParameter(String key, String value) {
    this.namedParameters.put(key, value);
    return this;
  }

  Sql build() {
    return Sql.ofNamedParameters(sql, namedParameters);
  }
}