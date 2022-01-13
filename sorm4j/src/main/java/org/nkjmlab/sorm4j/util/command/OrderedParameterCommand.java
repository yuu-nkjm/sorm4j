package org.nkjmlab.sorm4j.util.command;

import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.sql.OrderedParameterCommandImpl;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;

/**
 * An executable request with ordered parameters.
 *
 * @author nkjm
 *
 */
@Experimental
public interface OrderedParameterCommand extends OrderedParameterSql, Command {

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
  static OrderedParameterCommand from(OrmConnection conn, String sql) {
    return new OrderedParameterCommandImpl(conn, sql);
  }

}
