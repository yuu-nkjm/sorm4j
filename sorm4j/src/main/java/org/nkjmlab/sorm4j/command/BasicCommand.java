package org.nkjmlab.sorm4j.command;

import java.util.Map;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.internal.sql.BasicCommandImpl;

public interface BasicCommand extends Command {

  OrderedParameterCommand addParameter(Object parameter);

  OrderedParameterCommand addParameter(Object... parameters);

  NamedParameterCommand bindAll(Map<String, Object> keyValuePairOfNamedParameters);

  NamedParameterCommand bind(String key, Object value);

  NamedParameterCommand bindBean(Object bean);

  static BasicCommand from(OrmConnection conn, String sql) {
    return new BasicCommandImpl(conn, sql);
  }


}
