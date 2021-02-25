package org.nkjmlab.sorm4j.helper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class SqlWithNamedPlaceholders {

  // with ? placeholder
  private final String sql;
  // ordered parameters
  private final Object[] parameters;

  private SqlWithNamedPlaceholders(String sql, Object[] parameters) {
    this.sql = sql;
    this.parameters = parameters;
  }

  public static SqlWithNamedPlaceholders createWithNamedParameters(String sql,
      Map<String, Object> namedParameters) {
    return createWithNamedParameters(sql, namedParameters, ":", "");
  }

  public static SqlWithNamedPlaceholders createWithNamedParameters(String sql,
      Map<String, Object> namedParameters, String prefix, String suffix) {
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
    return new SqlWithNamedPlaceholders(sql, orderdParams.values().toArray());
  }

  @Override
  public String toString() {
    return " [" + sql + "] with " + Arrays.toString(parameters) + "";
  }

  public String getSql() {
    return sql;
  }

  public Object[] getParameters() {
    return parameters;
  }

  public static Builder createBuilder(String sql) {
    return new Builder(sql);
  }

  public static class Builder {
    private final String sql;
    private final Map<String, Object> namedParameters = new HashMap<>();

    Builder(String sql) {
      this.sql = sql;
    }


    public Builder putAllParameters(Map<String, Object> namedParams) {
      this.namedParameters.putAll(namedParams);
      return this;
    }

    public Builder putParameter(String key, String value) {
      this.namedParameters.put(key, value);
      return this;
    }

    SqlWithNamedPlaceholders build() {
      return createWithNamedParameters(sql, namedParameters);
    }
  }
}
