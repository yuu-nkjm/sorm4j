package org.nkjmlab.sorm4j.core.sql;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.sql.SelectBuilder;
import org.nkjmlab.sorm4j.sql.SqlStatement;

/**
 * An builder of select SQL.
 *
 * @author nkjm
 *
 */
public class SelectBuilderImpl implements SelectBuilder {

  public SelectBuilderImpl() {}

  private boolean distinct;

  private String columns = "*";

  private String table;

  private String where;

  private String groupBy;

  private String having;

  private String orderBy;

  private String limit;

  @Override
  public SelectBuilder select(String... columns) {
    this.columns = String.join(", ", Arrays.stream(columns).collect(Collectors.toList()));
    return this;
  }

  @Override
  public SelectBuilder distinct() {
    this.distinct = true;
    return this;
  }

  @Override
  public SelectBuilder from(String table) {
    this.table = table;
    return this;
  }

  @Override
  public SelectBuilder groupBy(String... columns) {
    groupBy = String.join(",", Arrays.stream(columns).collect(Collectors.toList()));
    return this;
  }

  @Override
  public SelectBuilder having(SelectBuilder.Condition condition) {
    having(condition.toString());
    return this;
  }

  @Override
  public SelectBuilder having(String expr) {
    having = expr;
    return this;
  }

  @Override
  public SelectBuilder limit(int limit) {
    return limit(limit, 0);
  }

  @Override
  public SelectBuilder limit(int limit, int offset) {
    this.limit = limit + (offset > 0 ? " offset " + offset : "");
    return this;
  }

  @Override
  public SelectBuilder orderBy(String column, String ascOrDesc) {
    orderBy(new SelectBuilder.OrderBy(column, ascOrDesc));
    return this;
  }

  @Override
  public SelectBuilder orderBy(SelectBuilder.OrderBy... orderBys) {
    this.orderBy = String.join(", ",
        Arrays.stream(orderBys).map(ob -> ob.toString()).collect(Collectors.toList()));
    return this;
  }

  @Override
  public String buildSql() {
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
  public SelectBuilder where(SelectBuilder.Condition condition) {
    where(condition.toString());
    return this;
  }

  @Override
  public SelectBuilder where(String expr) {
    where = expr;
    return this;
  }

  @Override
  public SqlStatement toSqlStatement() {
    return SqlStatement.of(buildSql());
  }

}
