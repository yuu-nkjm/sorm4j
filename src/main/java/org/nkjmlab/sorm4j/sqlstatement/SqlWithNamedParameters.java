package org.nkjmlab.sorm4j.sqlstatement;

import java.util.Map;


public interface SqlWithNamedParameters {

  SqlWithNamedParameters bindAll(Map<String, Object> namedParams);

  SqlWithNamedParameters bind(String key, Object value);

  SqlStatement toSqlStatement();

  static SqlWithNamedParameters from(String sql, String prefix, String suffix) {
    return new SqlWithNamedParametersImpl(sql, prefix, suffix);
  }

  static SqlWithNamedParameters from(String sql) {
    return SqlWithNamedParameters.from(sql, ":", "");
  }

  static SqlStatement toSqlStatement(String sql, Map<String, Object> namedParameters) {
    return SqlWithNamedParameters.from(sql).bindAll(namedParameters).toSqlStatement();
  }


}
