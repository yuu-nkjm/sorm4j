package org.nkjmlab.sorm4j.internal.sql;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.sql.SelectStringBuilder;

/**
 * An builder of select SQL.
 *
 * @author nkjm
 *
 */
public class SelectStringBuilderImpl implements SelectStringBuilder {

  public SelectStringBuilderImpl() {}

  private boolean distinct;

  private String columns = "*";

  private String table;

  private String where;

  private String groupBy;

  private String having;

  private String orderBy;

  private String limit;

  @Override
  public SelectStringBuilder select(String... columns) {
    this.columns = String.join(", ", Arrays.stream(columns).collect(Collectors.toList()));
    return this;
  }

  @Override
  public SelectStringBuilder distinct() {
    this.distinct = true;
    return this;
  }

  @Override
  public SelectStringBuilder from(String table) {
    this.table = table;
    return this;
  }

  @Override
  public SelectStringBuilder groupBy(String... columns) {
    groupBy = String.join(",", Arrays.stream(columns).collect(Collectors.toList()));
    return this;
  }

  @Override
  public SelectStringBuilder having(SelectStringBuilder.Condition condition) {
    having(condition.toString());
    return this;
  }

  @Override
  public SelectStringBuilder having(String expr) {
    having = expr;
    return this;
  }

  @Override
  public SelectStringBuilder limit(int limit) {
    return limit(limit, 0);
  }

  @Override
  public SelectStringBuilder limit(int limit, int offset) {
    this.limit = limit + (offset > 0 ? " offset " + offset : "");
    return this;
  }

  @Override
  public SelectStringBuilder orderBy(String column, String ascOrDesc) {
    orderBy(new SelectStringBuilder.OrderBy(column, ascOrDesc));
    return this;
  }

  @Override
  public SelectStringBuilder orderBy(SelectStringBuilder.OrderBy... orderBys) {
    this.orderBy = String.join(", ",
        Arrays.stream(orderBys).map(ob -> ob.toString()).collect(Collectors.toList()));
    return this;
  }

  @Override
  public String build() {
    return toPrettyString(false);
  }

  @Override
  public String toPrettyString() {
    return toPrettyString(true);
  }

  @Override
  public String toPrettyString(boolean prettyPrint) {
    StringBuilder sql = new StringBuilder("select ");
    if (distinct) {
      sql.append("distinct ");
    }
    sql.append(columns);
    sql.append(prettyPrint ? System.lineSeparator() : "");
    sql.append(" from " + table);
    if (where != null) {
      sql.append(prettyPrint ? System.lineSeparator() : "");
      sql.append(" where " + where);
    }
    if (groupBy != null) {
      sql.append(prettyPrint ? System.lineSeparator() : "");
      sql.append(" group by " + groupBy);
    }
    if (having != null) {
      sql.append(prettyPrint ? System.lineSeparator() : "");
      sql.append(" having " + having);
    }
    if (orderBy != null) {
      sql.append(prettyPrint ? System.lineSeparator() : "");
      sql.append(" order by " + orderBy);
    }
    if (limit != null) {
      sql.append(prettyPrint ? System.lineSeparator() : "");
      sql.append(" limit " + limit);
    }

    return sql.toString();
  }


  @Override
  public String toString() {
    return toPrettyString(false);
  }

  @Override
  public SelectStringBuilder where(SelectStringBuilder.Condition condition) {
    where(condition.toString());
    return this;
  }

  @Override
  public SelectStringBuilder where(String expr) {
    where = expr;
    return this;
  }

}
