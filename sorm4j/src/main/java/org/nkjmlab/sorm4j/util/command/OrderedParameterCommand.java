package org.nkjmlab.sorm4j.util.command;

import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.common.Experimental;
import org.nkjmlab.sorm4j.internal.util.command.OrderedParameterCommandImpl;
import org.nkjmlab.sorm4j.util.sql.param.OrderedParameterSqlParser;

/**
 * An executable request with ordered parameters.
 *
 * @author nkjm
 */
@Experimental
public interface OrderedParameterCommand extends OrderedParameterSqlParser, Command {

  @Override
  OrderedParameterCommand addParameter(Object parameter);

  @Override
  OrderedParameterCommand addParameter(Object... parameters);

  /**
   * Creates a request from SQL string.
   *
   * @param conn
   * @param sql
   * @return
   */
  static OrderedParameterCommand of(OrmConnection conn, String sql) {
    return new OrderedParameterCommandImpl(conn, sql);
  }
}
