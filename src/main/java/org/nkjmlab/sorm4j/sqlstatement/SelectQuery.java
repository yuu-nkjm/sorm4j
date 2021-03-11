package org.nkjmlab.sorm4j.sqlstatement;

import java.util.Map;
import org.nkjmlab.sorm4j.TypedOrmConnection;
import org.nkjmlab.sorm4j.sqlstatement.SelectBuilderImpl.Condition;
import org.nkjmlab.sorm4j.sqlstatement.SelectBuilderImpl.OrderBy;

/**
 * This class represents a select query. This class has functions as SQL select builder.
 *
 * @author nkjm
 *
 * @param <T>
 */
public class SelectQuery<T> extends AbstQuery<T>
    implements SelectBuilder, NamedParameterSql, OrderedParameterSql, Query<T> {
  private SelectBuilder selectBuilder;

  public SelectQuery(TypedOrmConnection<T> conn) {
    super(conn);
    this.selectBuilder = SelectBuilder.create();
    this.selectBuilder.from(conn.getTableName());
  }

  private NamedParameterQuery<T> withNamedParameter() {
    return NamedParameterQuery.createFrom(conn, buildSql());
  }

  private OrderedParameterQuery<T> withOrderedParameter() {
    return OrderedParameterQuery.createFrom(conn, buildSql());
  }

  @Override
  public OrderedParameterQuery<T> add(Object... parameters) {
    OrderedParameterQuery<T> ret = withOrderedParameter();
    ret.add(parameters);
    return ret;
  }

  @Override
  public OrderedParameterQuery<T> add(Object parameter) {
    OrderedParameterQuery<T> ret = withOrderedParameter();
    ret.add(parameter);
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
    return SqlStatement.of(buildSql());
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
  public SelectQuery<T> having(Condition condition) {
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
  public SelectQuery<T> orderBy(OrderBy... orderBys) {
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
  public SelectQuery<T> where(Condition condition) {
    selectBuilder.where(condition);
    return this;
  }

  @Override
  public SelectQuery<T> where(String expr) {
    selectBuilder.where(expr);
    return this;
  }


}
