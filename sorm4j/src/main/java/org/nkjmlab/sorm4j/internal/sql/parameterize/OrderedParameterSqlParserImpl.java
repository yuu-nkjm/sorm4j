package org.nkjmlab.sorm4j.internal.sql.parameterize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nkjmlab.sorm4j.sql.parameterize.OrderedParameterSqlBuilder;
import org.nkjmlab.sorm4j.sql.parameterize.ParameterizedSql;

/**
 * An implementation of {@link OrderedParameterSqlBuilder}
 *
 * @author nkjm
 */
public final class OrderedParameterSqlParserImpl implements OrderedParameterSqlBuilder {

  private final String sql;
  private final List<Object> parameters = new ArrayList<>();

  public OrderedParameterSqlParserImpl(String sql) {
    this.sql = sql;
  }

  @Override
  public OrderedParameterSqlBuilder addParameters(Object... parameters) {
    this.parameters.addAll(Arrays.asList(parameters));
    return this;
  }

  @Override
  public OrderedParameterSqlBuilder addParameter(Object parameter) {
    this.parameters.add(parameter);
    return this;
  }

  @Override
  public ParameterizedSql build() {
    return ParameterizedSqlImpl.of(sql, parameters.toArray());
  }
}
