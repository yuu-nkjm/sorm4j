package org.nkjmlab.sorm4j.sql;

import java.util.Map;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.sql.NamedParameterCommandImpl;


/**
 * An executable request with named parameters.
 *
 * @author nkjm
 *
 */
@Experimental
public interface NamedParameterCommand extends NamedParameterSql, Command {


  @Override
  NamedParameterCommand bindAll(Map<String, Object> keyValuePairOfNamedParameters);

  @Override
  NamedParameterCommand bind(String key, Object value);

  @Override
  @Experimental
  NamedParameterCommand bindBean(Object bean);

  /**
   * Creates a request from SQL string.
   *
   * @param conn
   * @param sql
   * @return
   */
  static NamedParameterCommand from(OrmConnection conn, String sql) {
    return new NamedParameterCommandImpl(conn, sql);
  }

}
