package org.nkjmlab.sorm4j.internal.sql;

import java.util.Map;
import org.nkjmlab.sorm4j.sql.NamedParameterQuery;
import org.nkjmlab.sorm4j.sql.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sql.SelectBuilder;
import org.nkjmlab.sorm4j.sql.SelectQuery;
import org.nkjmlab.sorm4j.sql.SqlStatement;

/**
 * This class represents a select query. This class has functions as SQL select builder.
 *
 * @author nkjm
 *
 * @param <T>
 */
public class SelectQueryImpl<T> extends AbstractQuery<T> implements SelectQuery<T> {

  private final SelectBuilder selectBuilder;


  public SelectQueryImpl(QueryExecutor<T> executor) {
    super(executor);
    this.selectBuilder = SelectBuilder.create();
  }

  private NamedParameterQuery<T> withNamedParameter() {
    return NamedParameterQueryImpl.createFrom(executor, buildSql());
  }

  private OrderedParameterQuery<T> withOrderedParameter() {
    return OrderedParameterQueryImpl.createFrom(executor, buildSql());
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
  public SqlStatement toSqlStatement() {
    return SqlStatement.from(buildSql());
  }

  @Override
  public SelectQuery<T> select(String... columns) {
    selectBuilder.select(columns);
    return this;
  }

  @Override
  public SelectQuery<T> distinct() {
    selectBuilder.distinct();
    return this;
  }

  @Override
  public SelectQuery<T> from(String table) {
    selectBuilder.from(table);
    return this;
  }

  @Override
  public SelectQuery<T> groupBy(String... columns) {
    selectBuilder.groupBy(columns);
    return this;
  }

  @Override
  public SelectQuery<T> having(SelectBuilder.Condition condition) {
    selectBuilder.having(condition);
    return this;
  }

  @Override
  public SelectQuery<T> having(String expr) {
    selectBuilder.having(expr);
    return this;
  }

  @Override
  public SelectQuery<T> limit(int limit) {
    selectBuilder.limit(limit);
    return this;
  }

  @Override
  public SelectQuery<T> limit(int limit, int offset) {
    selectBuilder.limit(limit, offset);
    return this;
  }

  @Override
  public SelectQuery<T> orderBy(String column, String ascOrDesc) {
    selectBuilder.orderBy(column, ascOrDesc);
    return this;
  }

  @Override
  public SelectQuery<T> orderBy(SelectBuilder.OrderBy... orderBys) {
    selectBuilder.orderBy(orderBys);
    return this;
  }

  @Override
  public String buildSql() {
    return selectBuilder.buildSql();
  }

  @Override
  public String toString() {
    return selectBuilder.buildSql();
  }

  @Override
  public String toPrettyString() {
    return selectBuilder.toPrettyString();
  }

  @Override
  public String toPrettyString(boolean prettyPrint) {
    return selectBuilder.toPrettyString(prettyPrint);
  }

  @Override
  public SelectQuery<T> where(SelectBuilder.Condition condition) {
    selectBuilder.where(condition);
    return this;
  }

  @Override
  public SelectQuery<T> where(String expr) {
    selectBuilder.where(expr);
    return this;
  }

}

