package org.nkjmlab.sorm4j.sqlstatement;

import java.util.List;
import org.nkjmlab.sorm4j.core.sqlstatement.OrderedParameterSqlImpl;

/**
 * SQL with ordered parameters. The instance could be convert to {@link SqlStatement}. The class
 * could treat {@link List} parameter.
 *
 * @author nkjm
 *
 */
public interface OrderedParameterSql {

  /**
   * Add one parameter to the SQL statement sequentially.
   *
   * @param parameter
   * @return
   */
  OrderedParameterSql add(Object parameter);

  /**
   * Add parameters to the SQL statement sequentially.
   *
   * @param parameters
   * @return
   */
  OrderedParameterSql add(Object... parameters);

  /**
   * Convert to a {@link SqlStatement} objects.
   *
   * @return
   */
  SqlStatement toSqlStatement();

  static SqlStatement toSqlStatement(String sql, Object... parameters) {
    return from(sql).add(parameters).toSqlStatement();
  }

  static OrderedParameterSql from(String sql) {
    return new OrderedParameterSqlImpl(sql);
  }

}
