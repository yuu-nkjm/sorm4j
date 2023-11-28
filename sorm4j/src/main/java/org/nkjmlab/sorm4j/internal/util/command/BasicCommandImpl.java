package org.nkjmlab.sorm4j.internal.util.command;

import java.util.Map;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.util.command.BasicCommand;
import org.nkjmlab.sorm4j.util.command.NamedParameterCommand;
import org.nkjmlab.sorm4j.util.command.OrderedParameterCommand;

public final class BasicCommandImpl extends AbstractCommand implements BasicCommand {

  private final String sql;

  public BasicCommandImpl(OrmConnection conn, String sql) {
    super(conn);
    this.sql = sql;
  }

  @Override
  public OrderedParameterCommand addParameter(Object parameter) {
    return OrderedParameterCommand.of(conn, sql).addParameter(parameter);
  }

  @Override
  public OrderedParameterCommand addParameter(Object... parameters) {
    return OrderedParameterCommand.of(conn, sql).addParameter(parameters);
  }

  @Override
  public NamedParameterCommand bindAll(Map<String, Object> keyValuePairOfNamedParameters) {
    return NamedParameterCommand.of(conn, sql).bindAll(keyValuePairOfNamedParameters);
  }

  @Override
  public NamedParameterCommand bind(String key, Object value) {
    return NamedParameterCommand.of(conn, sql).bind(key, value);
  }

  @Override
  public NamedParameterCommand bindBean(Object bean) {
    return NamedParameterCommand.of(conn, sql).bindBean(bean);
  }

  @Override
  protected ParameterizedSql parse() {
    return ParameterizedSql.of(sql);
  }
}
