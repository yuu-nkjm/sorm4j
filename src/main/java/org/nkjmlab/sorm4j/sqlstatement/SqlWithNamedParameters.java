package org.nkjmlab.sorm4j.sqlstatement;

import static org.nkjmlab.sorm4j.sqlstatement.SqlStatement.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class SqlWithNamedParameters {

  private final String sql;
  private final Map<String, Object> parameters = new HashMap<>();
  private final String prefix;
  private final String suffix;

  private SqlWithNamedParameters(String sql, String prefix, String suffix) {
    this.sql = sql;
    this.prefix = prefix;
    this.suffix = suffix;
  }

  public SqlWithNamedParameters bindAll(Map<String, Object> namedParams) {
    namedParams.entrySet().stream().forEach(e -> bind(e.getKey(), e.getValue()));
    return this;
  }

  public SqlWithNamedParameters bind(String key, Object value) {
    if (value instanceof List) {
      this.parameters.put(key, literal(value));
    } else {
      this.parameters.put(key, value);
    }
    return this;
  }

  public static SqlWithNamedParameters from(String sql) {
    return from(sql, ":", "");
  }

  public static SqlWithNamedParameters from(String sql, String prefix, String suffix) {
    return new SqlWithNamedParameters(sql, prefix, suffix);
  }

  public SqlStatement toSqlStatement() {
    TreeMap<Integer, Object> orderdParams = new TreeMap<>();
    String resultSql = this.sql;
    for (String parameterName : parameters.keySet().stream()
        .sorted(Comparator.comparing(String::length).reversed()).collect(Collectors.toList())) {
      String namedPlaceholder = prefix + parameterName + suffix;
      int pos = resultSql.indexOf(namedPlaceholder);
      if (pos == -1) {
        continue;
      }
      orderdParams.put(pos, parameters.get(parameterName));
      resultSql = resultSql.replaceAll(namedPlaceholder, "?");
    }
    return SqlStatement.of(resultSql, orderdParams.values().toArray());
  }

  public static SqlStatement toSqlStatement(String sql, Map<String, Object> namedParameters) {
    return from(sql).bindAll(namedParameters).toSqlStatement();
  }


}
