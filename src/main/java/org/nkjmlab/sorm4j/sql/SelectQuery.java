package org.nkjmlab.sorm4j.sql;

import java.util.Map;


/**
 * A query with builder for reading and mapping a relation to object.
 *
 * @author nkjm
 *
 * @param <T>
 */
public interface SelectQuery<T>
    extends SelectBuilder, NamedParameterSql, OrderedParameterSql, SqlStatementSupplier, Query<T> {
  @Override
  OrderedParameterQuery<T> add(Object... parameters);

  @Override
  OrderedParameterQuery<T> add(Object parameter);

  @Override
  NamedParameterQuery<T> bindAll(Map<String, Object> namedParams);

  @Override
  NamedParameterQuery<T> bind(String key, Object value);


  @Override
  SelectQuery<T> select(String... columns);

  @Override
  SelectQuery<T> distinct();

  @Override
  SelectQuery<T> from(String table);

  @Override
  SelectQuery<T> groupBy(String... columns);

  @Override
  SelectQuery<T> having(SelectBuilder.Condition condition);

  @Override
  SelectQuery<T> having(String expr);

  @Override
  SelectQuery<T> limit(int limit);

  @Override
  SelectQuery<T> limit(int limit, int offset);

  @Override
  SelectQuery<T> orderBy(String column, String ascOrDesc);

  @Override
  SelectQuery<T> orderBy(SelectBuilder.OrderBy... orderBys);

  @Override
  String buildSql();

  @Override
  String toPrettyString();

  @Override
  String toPrettyString(boolean prettyPrint);

  @Override
  SelectQuery<T> where(SelectBuilder.Condition condition);

  @Override
  SelectQuery<T> where(String expr);

}
