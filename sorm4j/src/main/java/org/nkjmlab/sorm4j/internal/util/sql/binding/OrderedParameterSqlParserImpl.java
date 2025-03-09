package org.nkjmlab.sorm4j.internal.util.sql.binding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nkjmlab.sorm4j.internal.sql.parameterize.ParameterizedSqlImpl;
import org.nkjmlab.sorm4j.sql.parameterize.OrderedParameterSqlFactory;
import org.nkjmlab.sorm4j.sql.parameterize.ParameterizedSql;

/**
 * An implementation of {@link OrderedParameterSqlFactory}
 *
 * @author nkjm
 */
public final class OrderedParameterSqlParserImpl implements OrderedParameterSqlFactory {

  private final String sql;
  private final List<Object> parameters = new ArrayList<>();

  public OrderedParameterSqlParserImpl(String sql) {
    this.sql = sql;
  }

  @Override
  public OrderedParameterSqlFactory addParameters(Object... parameters) {
    this.parameters.addAll(Arrays.asList(parameters));
    return this;
  }

  @Override
  public OrderedParameterSqlFactory addParameter(Object parameter) {
    this.parameters.add(parameter);
    return this;
  }

  @Override
  public ParameterizedSql create() {
    return ParameterizedSqlImpl.of(sql, parameters.toArray());
  }
}
