package org.nkjmlab.sorm4j.sql;

import java.util.List;
import org.nkjmlab.sorm4j.internal.sql.OrderedParameterSqlImpl;

/**
 * SQL with ordered parameters. The instance could be convert to {@link ParameterizedSql}. The class
 * could treat {@link List} parameter.
 *
 * @author nkjm
 *
 */
public interface OrderedParameterSql extends ParameterizedSqlParser {

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

  /**
   * Creates a {@link OrderedParameterSql} object with parameters.
   *
   * @param sql
   * @param parameters
   * @return
   */
  static ParameterizedSql parse(String sql, Object... parameters) {
    return from(sql).addParameter(parameters).parse();
  }

  /**
   * Creates a {@link OrderedParameterSql} object.
   *
   * @param sql
   * @return
   */
  static OrderedParameterSql from(String sql) {
    return new OrderedParameterSqlImpl(sql);
  }

}
