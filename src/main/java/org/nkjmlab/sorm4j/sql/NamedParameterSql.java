package org.nkjmlab.sorm4j.sql;

import java.util.Map;
import org.nkjmlab.sorm4j.internal.sql.NamedParameterSqlImpl;

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

  /**
   * Creates {@link NamedParameterSql} object. the named parameters should have the given prefix and
   * suffix.
   *
   * @param sql
   * @param prefix
   * @param suffix
   * @return
   */
  static NamedParameterSql from(String sql, String prefix, String suffix) {
    return new NamedParameterSqlImpl(sql, prefix, suffix);
  }

  /**
   * Creates {@link NamedParameterSql} object.
   *
   * @param sql
   * @return
   */

  static NamedParameterSql from(String sql) {
    return new NamedParameterSqlImpl(sql);
  }

  /**
   * Creates {@link NamedParameterSql} object with parameters.
   *
   * @param sql
   * @param namedParameters
   * @return
   */
  static SqlStatement toSqlStatement(String sql, Map<String, Object> namedParameters) {
    return NamedParameterSql.from(sql).bindAll(namedParameters).toSqlStatement();
  }


}
