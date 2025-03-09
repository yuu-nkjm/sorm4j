package org.nkjmlab.sorm4j.sql.parameterize;

import java.util.List;

import org.nkjmlab.sorm4j.internal.sql.parameterize.ParameterizedSqlImpl;
import org.nkjmlab.sorm4j.internal.util.sql.binding.OrderedParameterSqlParserImpl;

/**
 * SQL parser for ordered parameters. The instance could be convert to {@link ParameterizedSql}. The
 * class could treat {@link List} parameter.
 *
 * @author nkjm
 */
public interface OrderedParameterSqlFactory extends ParameterizedSqlFactory {

  /**
   * Add one parameter to the SQL statement sequentially.
   *
   * @param parameter
   * @return
   */
  OrderedParameterSqlFactory addParameter(Object parameter);

  /**
   * Add parameters to the SQL statement sequentially.
   *
   * @param parameters
   * @return
   */
  OrderedParameterSqlFactory addParameters(Object... parameters);

  /**
   * Creates a {@link OrderedParameterSqlFactory} object with parameters.
   *
   * @param sql
   * @param parameters
   * @return
   */
  static ParameterizedSql create(String sql, Object... parameters) {
    return ParameterizedSqlImpl.of(sql, parameters);
  }

  /**
   * Creates a {@link OrderedParameterSqlFactory} object.
   *
   * @param sql
   * @return
   */
  static OrderedParameterSqlFactory of(String sql) {
    return new OrderedParameterSqlParserImpl(sql);
  }
}
