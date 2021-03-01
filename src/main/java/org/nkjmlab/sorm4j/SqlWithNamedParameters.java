package org.nkjmlab.sorm4j;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class SqlWithNamedParameters {

  private final String sql;
  private final Map<String, Object> namedParameters = new HashMap<>();

  public SqlWithNamedParameters(String sql) {
    this.sql = sql;
  }

  public SqlWithNamedParameters bindAll(Map<String, Object> namedParams) {
    this.namedParameters.putAll(namedParams);
    return this;
  }

  public SqlWithNamedParameters bind(String key, String value) {
    this.namedParameters.put(key, value);
    return this;
  }

  public SqlStatement toSqlStatement() {
    return toSqlStatement(sql, namedParameters);
  }

  public static SqlWithNamedParameters of(String sql) {
    return new SqlWithNamedParameters(sql);
  }

  public static SqlStatement toSqlStatement(String sql, Map<String, Object> namedParameters) {
    return toSqlStatement(sql, namedParameters, ":", "");
  }

  public static SqlStatement toSqlStatement(String sql, Map<String, Object> namedParameters,
      String prefix, String suffix) {
    TreeMap<Integer, Object> orderdParams = new TreeMap<>();

    for (String parameterName : namedParameters.keySet().stream()
        .sorted(Comparator.comparing(String::length).reversed()).collect(Collectors.toList())) {
      String namedPlaceholder = prefix + parameterName + suffix;
      int pos = sql.indexOf(namedPlaceholder);
      if (pos == -1) {
        continue;
      }
      orderdParams.put(pos, namedParameters.get(parameterName));
      sql = sql.replaceAll(namedPlaceholder, "?");
    }
    return SqlStatement.of(sql, orderdParams.values().toArray());
  }

}
