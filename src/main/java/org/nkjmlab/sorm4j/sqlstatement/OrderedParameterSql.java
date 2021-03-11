package org.nkjmlab.sorm4j.sqlstatement;

import java.util.List;

/**
 * SQL with ordered parameters. The instance could be convert to {@link SqlStatement}. The class
 * could treat {@link List} parameter.
 *
 * @author nkjm
 *
 */
public interface OrderedParameterSql {

  OrderedParameterSql add(Object parameter);

  OrderedParameterSql add(Object... parameters);

  SqlStatement toSqlStatement();

  static SqlStatement toSqlStatement(String sql, Object... parameters) {
    return from(sql).add(parameters).toSqlStatement();
  }

  static OrderedParameterSql from(String sql) {
    return new OrderedParameterSqlImpl(sql);
  }

}
