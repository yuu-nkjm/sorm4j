package org.nkjmlab.sorm4j.internal.util.sql.binding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nkjmlab.sorm4j.container.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.internal.container.ParameterizedSqlImpl;
import org.nkjmlab.sorm4j.util.sql.binding.OrderedParameterSqlParser;

/**
 * An implementation of {@link OrderedParameterSqlParser}
 *
 * @author nkjm
 */
public final class OrderedParameterSqlParserImpl implements OrderedParameterSqlParser {

  private final String sql;
  private final List<Object> parameters = new ArrayList<>();

  public OrderedParameterSqlParserImpl(String sql) {
    this.sql = sql;
  }

  @Override
  public OrderedParameterSqlParser addParameter(Object... parameters) {
    this.parameters.addAll(Arrays.asList(parameters));
    return this;
  }

  @Override
  public OrderedParameterSqlParser addParameter(Object parameter) {
    this.parameters.add(parameter);
    return this;
  }

  @Override
  public ParameterizedSql parse() {
    return ParameterizedSqlImpl.parse(sql, parameters.toArray());
  }
}
