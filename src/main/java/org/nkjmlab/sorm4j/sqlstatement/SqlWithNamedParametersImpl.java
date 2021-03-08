package org.nkjmlab.sorm4j.sqlstatement;

import static org.nkjmlab.sorm4j.sqlstatement.SqlStatement.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * SQL with named parameters. The instance could be convert to {@link SqlStatement}.The class could
 * treat {@link List} parameter.
 *
 * @author nkjm
 *
 */
public final class SqlWithNamedParametersImpl implements SqlWithNamedParameters {

  private final String sql;
  private final Map<String, Object> parameters = new HashMap<>();
  private final String prefix;
  private final String suffix;

  SqlWithNamedParametersImpl(String sql, String prefix, String suffix) {
    this.sql = sql;
    this.prefix = prefix;
    this.suffix = suffix;
  }

  @Override
  public SqlWithNamedParameters bindAll(Map<String, Object> namedParams) {
    namedParams.entrySet().stream().forEach(e -> bind(e.getKey(), e.getValue()));
    return this;
  }

  @Override
  public SqlWithNamedParameters bind(String key, Object value) {
    if (value instanceof List) {
      this.parameters.put(key, literal(value));
    } else {
      this.parameters.put(key, value);
    }
    return this;
  }

  @Override
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


}
