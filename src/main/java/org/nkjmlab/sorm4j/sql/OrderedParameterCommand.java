package org.nkjmlab.sorm4j.sql;

import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.sql.OrderedParameterCommandImpl;

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
   * @param orm
   * @param sql
   * @return
   */
  static OrderedParameterCommand from(OrmConnection conn, String sql) {
    return new OrderedParameterCommandImpl(conn, sql);
  }

}
