package org.nkjmlab.sorm4j.internal.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;
import org.nkjmlab.sorm4j.sql.SqlStatement;

/**
 * An implementation of {@link OrderedParameterSql}
 *
 * @author nkjm
 *
 */
public class OrderedParameterSqlImpl implements OrderedParameterSql {

  private final String sql;
  private final List<Object> parameters = new ArrayList<>();

  public OrderedParameterSqlImpl(String sql) {
    this.sql = sql;
  }

  @Override
  public OrderedParameterSql addParameter(Object... parameters) {
    Arrays.asList(parameters).forEach(v -> addParameter(v));
    return this;
  }

  @Override
  public OrderedParameterSql addParameter(Object parameter) {
    this.parameters.add(parameter);
    return this;
  }

  @Override
  public SqlStatement toSqlStatement() {
    return SqlStatementImpl.from(sql, parameters.toArray());
  }



}
