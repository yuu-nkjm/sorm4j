package org.nkjmlab.sorm4j.sql.parameterize;

import java.util.List;

import org.nkjmlab.sorm4j.internal.util.sql.binding.OrderedParameterSqlParserImpl;

/**
 * SQL parser for ordered parameters. The instance could be convert to {@link ParameterizedSql}. The
 * class could treat {@link List} parameter.
 *
 * @author nkjm
 */
public interface OrderedParameterSqlBuilder extends ParameterizedSqlBuilder {

  /**
   * Add one parameter to the SQL statement sequentially.
   *
   * @param parameter
   * @return
   */
  OrderedParameterSqlBuilder addParameter(Object parameter);

  /**
   * Add parameters to the SQL statement sequentially.
   *
   * @param parameters
   * @return
   */
  OrderedParameterSqlBuilder addParameters(Object... parameters);

  /**
   * Creates a {@link OrderedParameterSqlBuilder} object.
   *
   * @param sql
   * @return
   */
  static OrderedParameterSqlBuilder builder(String sql) {
    return new OrderedParameterSqlParserImpl(sql);
  }
}
