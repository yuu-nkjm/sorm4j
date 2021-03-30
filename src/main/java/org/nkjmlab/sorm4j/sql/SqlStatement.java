package org.nkjmlab.sorm4j.sql;

import org.nkjmlab.sorm4j.internal.sql.SqlStatementImpl;
import org.nkjmlab.sorm4j.internal.util.SqlUtils;


/**
 * This class represents a SQL statement with ordered parameters.
 *
 * @author nkjm
 *
 */

public interface SqlStatement {

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
   * Create SqlStatement objects from the given arguments.
   *
   * @param sql
   * @param parameters
   * @return
   */
  static SqlStatement of(String sql, Object... parameters) {
    return SqlStatementImpl.of(sql, parameters);
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
