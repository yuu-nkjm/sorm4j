package org.nkjmlab.sorm4j.sql.helper;

import java.util.Map;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.sql.NamedParameterSql;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;
import org.nkjmlab.sorm4j.sql.ParameterizedSqlParser;


/**
 * A query with builder for reading and mapping a relation to object.
 *
 * @author nkjm
 *
 * @param <T>
 */
@Experimental
public interface SelectQuery<T> extends SelectStringBuilder, NamedParameterSql, OrderedParameterSql,
    ParameterizedSqlParser, Query<T> {
  @Override
  OrderedParameterQuery<T> addParameter(Object... parameters);

  @Override
  OrderedParameterQuery<T> addParameter(Object parameter);

  @Override
  NamedParameterQuery<T> bindAll(Map<String, Object> namedParams);

  @Override
  NamedParameterQuery<T> bind(String key, Object value);

  @Override
  @Experimental
  NamedParameterQuery<T> bindBean(Object bean);


  @Override
  SelectQuery<T> select(String... columns);

  @Override
  SelectQuery<T> distinct();

  /**
   * Sets the table name. The value automatically sets based on the given class.
   *
   */
  @Override
  SelectQuery<T> from(String table);

  @Override
  SelectQuery<T> groupBy(String... columns);

  @Override
  SelectQuery<T> having(SelectStringBuilder.Condition condition);

  @Override
  SelectQuery<T> having(String expr);

  @Override
  SelectQuery<T> limit(int limit);

  @Override
  SelectQuery<T> limit(int limit, int offset);

  @Override
  SelectQuery<T> orderBy(String column, String ascOrDesc);

  @Override
  SelectQuery<T> orderBy(SelectStringBuilder.OrderBy... orderBys);

  @Override
  String build();

  @Override
  String toPrettyString();

  @Override
  String toPrettyString(boolean prettyPrint);

  @Override
  SelectQuery<T> where(SelectStringBuilder.Condition condition);

  @Override
  SelectQuery<T> where(String expr);

}
