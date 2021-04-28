package org.nkjmlab.sorm4j;

import java.util.Map;
import org.nkjmlab.sorm4j.sql.helper.Command;
import org.nkjmlab.sorm4j.sql.helper.NamedParameterCommand;
import org.nkjmlab.sorm4j.sql.helper.OrderedParameterCommand;

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
