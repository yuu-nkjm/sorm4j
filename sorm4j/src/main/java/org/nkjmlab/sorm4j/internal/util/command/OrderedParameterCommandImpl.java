package org.nkjmlab.sorm4j.internal.util.command;

import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.sql.OrderedParameterSqlParser;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.util.command.OrderedParameterCommand;

public final class OrderedParameterCommandImpl extends AbstractCommand
    implements OrderedParameterCommand {

  private final OrderedParameterSqlParser sql;

  public OrderedParameterCommandImpl(OrmConnection conn, String sql) {
    super(conn);
    this.sql = OrderedParameterSqlParser.of(sql);
  }

  @Override
  public ParameterizedSql parse() {
    return sql.parse();
  }

  @Override
  public OrderedParameterCommand addParameter(Object parameter) {
    sql.addParameter(parameter);
    return this;
  }

  @Override
  public OrderedParameterCommand addParameter(Object... parameters) {
    sql.addParameter(parameters);
    return this;
  }

}
