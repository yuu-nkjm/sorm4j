package org.nkjmlab.sorm4j.util.sql.binding;

import java.util.List;

import org.nkjmlab.sorm4j.container.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.internal.container.ParameterizedSqlImpl;
import org.nkjmlab.sorm4j.internal.util.sql.binding.OrderedParameterSqlParserImpl;

/**
 * SQL parser for ordered parameters. The instance could be convert to {@link ParameterizedSql}. The
 * class could treat {@link List} parameter.
 *
 * @author nkjm
 */
public interface OrderedParameterSqlParser extends ParameterizedSqlParser {

  /**
   * Add one parameter to the SQL statement sequentially.
   *
   * @param parameter
   * @return
   */
  OrderedParameterSqlParser addParameter(Object parameter);

  /**
   * Add parameters to the SQL statement sequentially.
   *
   * @param parameters
   * @return
   */
  OrderedParameterSqlParser addParameter(Object... parameters);

  /**
   * Creates a {@link OrderedParameterSqlParser} object with parameters.
   *
   * @param sql
   * @param parameters
   * @return
   */
  static ParameterizedSql parse(String sql, Object... parameters) {
    return ParameterizedSqlImpl.parse(sql, parameters);
  }

  /**
   * Creates a {@link OrderedParameterSqlParser} object.
   *
   * @param sql
   * @return
   */
  static OrderedParameterSqlParser of(String sql) {
    return new OrderedParameterSqlParserImpl(sql);
  }
}
