package org.nkjmlab.sorm4j.internal.util.command;

import java.util.Map;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.common.ParameterizedSql;
import org.nkjmlab.sorm4j.util.command.NamedParameterCommand;
import org.nkjmlab.sorm4j.util.sql.param.NamedParameterSqlParser;

public final class NamedParameterCommandImpl extends AbstractCommand
    implements NamedParameterCommand {

  private final NamedParameterSqlParser sql;

  public NamedParameterCommandImpl(OrmConnection conn, String sql) {
    super(conn);
    this.sql = NamedParameterSqlParser.of(sql);
  }

  @Override
  public ParameterizedSql parse() {
    return sql.parse();
  }

  @Override
  public NamedParameterCommand bindAll(Map<String, Object> keyValuePairOfNamedParameters) {
    sql.bindAll(keyValuePairOfNamedParameters);
    return this;
  }

  @Override
  public NamedParameterCommand bind(String key, Object value) {
    sql.bind(key, value);
    return this;
  }

  @Override
  public NamedParameterCommand bindBean(Object bean) {
    sql.bindBean(bean);
    return this;
  }
}
