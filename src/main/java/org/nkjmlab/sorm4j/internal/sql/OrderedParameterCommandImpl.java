package org.nkjmlab.sorm4j.internal.sql;

import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.sql.OrderedParameterCommand;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

public class OrderedParameterCommandImpl extends AbstractCommand
    implements OrderedParameterCommand {

  private final OrderedParameterSql sql;

  public OrderedParameterCommandImpl(OrmConnection conn, String sql) {
    super(conn);
    this.sql = OrderedParameterSql.from(sql);
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
