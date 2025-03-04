package org.nkjmlab.sorm4j.common;

import org.nkjmlab.sorm4j.internal.common.ParameterizedSqlImpl;

/**
 * This class represents a SQL statement with ordered parameters.
 *
 * @author nkjm
 */
public interface ParameterizedSql {

  /**
   * Gets this SQL statement.
   *
   * @return
   */
  String getSql();

  /**
   * Gets this ordered parameters.
   *
   * @return
   */
  Object[] getParameters();

  /**
   * Gets the SQL string after binding parameter.
   *
   * @return
   */
  String getParameterBindedSql();

  /**
   * Creates {@link ParameterizedSql} object of the given SQL string.
   *
   * @param sql without parameter.
   * @return
   */
  static ParameterizedSql of(String sql) {
    return ParameterizedSqlImpl.of(sql);
  }

  /**
   * Creates {@link ParameterizedSql} object of the given SQL string and parameters. The given
   * parameters should be simple ordered parameters.
   *
   * @param sql
   * @param parameters ordered parameters without special parameters (e.g. named parameter, list
   *     parameter and embedded parameter)
   * @return
   */
  static ParameterizedSql of(String sql, Object... parameters) {
    return ParameterizedSqlImpl.of(sql, parameters);
  }
}
