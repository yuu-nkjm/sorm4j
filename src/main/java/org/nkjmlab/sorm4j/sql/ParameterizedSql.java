package org.nkjmlab.sorm4j.sql;

import java.util.Map;
import org.nkjmlab.sorm4j.internal.sql.ParameterizedSqlImpl;
import org.nkjmlab.sorm4j.internal.util.SqlUtils;


/**
 * This class represents a SQL statement with ordered parameters.
 *
 * @author nkjm
 *
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

  static ParameterizedSql parseAsOrdered(String sql, Object... orderedParameters) {
    return OrderedParameterSql.parse(sql, orderedParameters);
  }

  static ParameterizedSql parseAsNamed(String sql, Map<String, Object> namedParameters) {
    return NamedParameterSql.parse(sql, namedParameters);
  }

  /**
   * Creates {@link ParameterizedSql} object from the given SQL string. When you use a SQL statement
   * with parameter, use {@link NamedParameterSql}, {@link OrderedParameterSql}.
   *
   * @param sql without parameter.
   * @return
   */
  static ParameterizedSql from(String sql) {
    return ParameterizedSqlImpl.parse(sql);
  }

  /**
   * Convert the given arguments to SQL literal.
   *
   * @param element
   * @return
   */
  static String literal(Object element) {
    return SqlUtils.literal(element);
  }

  /**
   * Returns single quoted expression. If it includes single quotations, they will be escaped.
   *
   * @param expr
   * @return
   */
  static String quote(String expr) {
    return SqlUtils.quote(expr);
  }

}
