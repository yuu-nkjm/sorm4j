package org.nkjmlab.sorm4j.internal.sql;

import java.util.Map;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.sql.BasicCommand;
import org.nkjmlab.sorm4j.sql.NamedParameterCommand;
import org.nkjmlab.sorm4j.sql.OrderedParameterCommand;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

public class BasicCommandImpl extends AbstractCommand implements BasicCommand {

  private final String sql;

  public BasicCommandImpl(OrmConnection conn, String sql) {
    super(conn);
    this.sql = sql;
  }

  @Override
  public OrderedParameterCommand addParameter(Object parameter) {
    return OrderedParameterCommand.from(conn, sql).addParameter(parameter);
  }

  @Override
  public OrderedParameterCommand addParameter(Object... parameters) {
    return OrderedParameterCommand.from(conn, sql).addParameter(parameters);
  }

  @Override
  public NamedParameterCommand bindAll(Map<String, Object> keyValuePairOfNamedParameters) {
    return NamedParameterCommand.from(conn, sql).bindAll(keyValuePairOfNamedParameters);
  }

  @Override
  public NamedParameterCommand bind(String key, Object value) {
    return NamedParameterCommand.from(conn, sql).bind(key, value);
  }

  @Override
  public NamedParameterCommand bindBean(Object bean) {
    return NamedParameterCommand.from(conn, sql).bindBean(bean);
  }

  @Override
  protected ParameterizedSql parse() {
    return ParameterizedSql.from(sql);
  }


}
