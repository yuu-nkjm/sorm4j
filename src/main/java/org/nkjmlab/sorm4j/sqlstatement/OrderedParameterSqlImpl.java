package org.nkjmlab.sorm4j.sqlstatement;

import static org.nkjmlab.sorm4j.sqlstatement.SqlStatement.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An implementation of {@link OrderedParameterSql}
 *
 * @author nkjm
 *
 */
public final class OrderedParameterSqlImpl implements OrderedParameterSql {

  private final String sql;
  private final List<Object> parameters = new ArrayList<>();

  OrderedParameterSqlImpl(String sql) {
    this.sql = sql;
  }

  @Override
  public OrderedParameterSql add(Object... parameters) {
    Arrays.asList(parameters).forEach(v -> add(v));
    return this;
  }

  @Override
  public OrderedParameterSql add(Object parameter) {
    if (parameter instanceof List) {
      this.parameters.add(literal(parameter));
    } else {
      this.parameters.add(parameter);
    }
    return this;
  }

  @Override
  public SqlStatement toSqlStatement() {
    return SqlStatement.of(sql, parameters.toArray());
  }



}
