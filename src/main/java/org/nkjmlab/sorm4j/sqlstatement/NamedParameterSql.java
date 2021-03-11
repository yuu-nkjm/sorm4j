package org.nkjmlab.sorm4j.sqlstatement;

import java.util.Map;


public interface NamedParameterSql {

  NamedParameterSql bindAll(Map<String, Object> namedParams);

  NamedParameterSql bind(String key, Object value);

  SqlStatement toSqlStatement();

  static NamedParameterSql from(String sql, String prefix, String suffix) {
    return new NamedParameterSqlImpl(sql, prefix, suffix);
  }

  static NamedParameterSql from(String sql) {
    return NamedParameterSql.from(sql, ":", "");
  }

  static SqlStatement toSqlStatement(String sql, Map<String, Object> namedParameters) {
    return NamedParameterSql.from(sql).bindAll(namedParameters).toSqlStatement();
  }


}
