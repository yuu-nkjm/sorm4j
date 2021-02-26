package org.nkjmlab.sorm4j.helper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class Sql {

  // with ? placeholder
  private final String sql;
  // ordered parameters
  private final Object[] parameters;

  private Sql(String sql, Object[] parameters) {
    this.sql = sql;
    this.parameters = parameters;
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


  public static Sql ofNamedParameters(String sql, Map<String, Object> namedParameters) {
    return ofNamedParameters(sql, namedParameters, ":", "");
  }

  public static Sql ofNamedParameters(String sql, Map<String, Object> namedParameters,
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
    return new Sql(sql, orderdParams.values().toArray());
  }
}
