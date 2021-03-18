package org.nkjmlab.sorm4j.sql;

import java.util.List;
import org.nkjmlab.sorm4j.core.sqlstatement.OrderedParameterSqlImpl;

/**
 * SQL with ordered parameters. The instance could be convert to {@link SqlStatement}. The class
 * could treat {@link List} parameter.
 *
 * @author nkjm
 *
 */
public interface OrderedParameterSql extends SqlStatementSupplier {

  /**
   * Add one parameter to the SQL statement sequentially.
   *
   * @param parameter
   * @return
   */
  OrderedParameterSql addParameter(Object parameter);

  /**
   * Add parameters to the SQL statement sequentially.
   *
   * @param parameters
   * @return
   */
  OrderedParameterSql addParameter(Object... parameters);


  static SqlStatement toSqlStatement(String sql, Object... parameters) {
    return from(sql).addParameter(parameters).toSqlStatement();
  }

  static OrderedParameterSql from(String sql) {
    return new OrderedParameterSqlImpl(sql);
  }

}
