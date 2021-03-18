package org.nkjmlab.sorm4j.sql;

import java.util.Map;
import org.nkjmlab.sorm4j.core.sqlstatement.NamedParameterSqlImpl;

/**
 * A SQL statement with named parameters.
 *
 * @author nkjm
 *
 */
public interface NamedParameterSql extends SqlStatementSupplier {

  /**
   * Binds key-value pairs to named parameters in a SQL statement.
   *
   * @param keyValuePairOfNamedParameters
   * @return
   */
  NamedParameterSql bindAll(Map<String, Object> keyValuePairOfNamedParameters);

  /**
   * Binds a key-value pair to named parameters in a SQL statement.
   *
   * @param key
   * @param value
   * @return
   */
  NamedParameterSql bind(String key, Object value);


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
