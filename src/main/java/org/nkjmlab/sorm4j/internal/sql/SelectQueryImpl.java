package org.nkjmlab.sorm4j.internal.sql;

import java.util.Map;
import org.nkjmlab.sorm4j.sql.helper.NamedParameterQuery;
import org.nkjmlab.sorm4j.sql.helper.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sql.helper.SelectStringBuilder;
import org.nkjmlab.sorm4j.sql.helper.SelectQuery;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

/**
 * This class represents a select query. This class has functions as SQL select builder.
 *
 * @author nkjm
 *
 * @param <T>
 */
public class SelectQueryImpl<T> extends AbstractQuery<T> implements SelectQuery<T> {

  private final SelectStringBuilder selectStringBuilder;


  public SelectQueryImpl(QueryExecutor<T> executor) {
    super(executor);
    this.selectStringBuilder = SelectStringBuilder.create();
  }

  private NamedParameterQuery<T> withNamedParameter() {
    return NamedParameterQueryImpl.createFrom(executor, build());
  }

  private OrderedParameterQuery<T> withOrderedParameter() {
    return OrderedParameterQueryImpl.createFrom(executor, build());
  }

  @Override
  public OrderedParameterQuery<T> addParameter(Object... parameters) {
    OrderedParameterQuery<T> ret = withOrderedParameter();
    ret.addParameter(parameters);
    return ret;
  }

  @Override
  public OrderedParameterQuery<T> addParameter(Object parameter) {
    OrderedParameterQuery<T> ret = withOrderedParameter();
    ret.addParameter(parameter);
    return ret;
  }

  @Override
  public NamedParameterQuery<T> bindAll(Map<String, Object> namedParams) {
    NamedParameterQuery<T> ret = withNamedParameter();
    ret.bindAll(namedParams);
    return ret;
  }

  @Override
  public NamedParameterQuery<T> bind(String key, Object value) {
    NamedParameterQuery<T> ret = withNamedParameter();
    ret.bind(key, value);
    return ret;
  }
  @Override
  public NamedParameterQuery<T> bindBean(Object bean) {
    NamedParameterQuery<T> ret = withNamedParameter();
    ret.bindBean(bean);
    return ret;
  }


  @Override
  public ParameterizedSql parse() {
    return ParameterizedSql.from(build());
  }

  @Override
  public SelectQuery<T> select(String... columns) {
    selectStringBuilder.select(columns);
    return this;
  }

  @Override
  public SelectQuery<T> distinct() {
    selectStringBuilder.distinct();
    return this;
  }

  @Override
  public SelectQuery<T> from(String table) {
    selectStringBuilder.from(table);
    return this;
  }

  @Override
  public SelectQuery<T> groupBy(String... columns) {
    selectStringBuilder.groupBy(columns);
    return this;
  }

  @Override
  public SelectQuery<T> having(SelectStringBuilder.Condition condition) {
    selectStringBuilder.having(condition);
    return this;
  }

  @Override
  public SelectQuery<T> having(String expr) {
    selectStringBuilder.having(expr);
    return this;
  }

  @Override
  public SelectQuery<T> limit(int limit) {
    selectStringBuilder.limit(limit);
    return this;
  }

  @Override
  public SelectQuery<T> limit(int limit, int offset) {
    selectStringBuilder.limit(limit, offset);
    return this;
  }

  @Override
  public SelectQuery<T> orderBy(String column, String ascOrDesc) {
    selectStringBuilder.orderBy(column, ascOrDesc);
    return this;
  }

  @Override
  public SelectQuery<T> orderBy(SelectStringBuilder.OrderBy... orderBys) {
    selectStringBuilder.orderBy(orderBys);
    return this;
  }

  @Override
  public String build() {
    return selectStringBuilder.build();
  }

  @Override
  public String toString() {
    return selectStringBuilder.build();
  }

  @Override
  public String toPrettyString() {
    return selectStringBuilder.toPrettyString();
  }

  @Override
  public String toPrettyString(boolean prettyPrint) {
    return selectStringBuilder.toPrettyString(prettyPrint);
  }

  @Override
  public SelectQuery<T> where(SelectStringBuilder.Condition condition) {
    selectStringBuilder.where(condition);
    return this;
  }

  @Override
  public SelectQuery<T> where(String expr) {
    selectStringBuilder.where(expr);
    return this;
  }


}

