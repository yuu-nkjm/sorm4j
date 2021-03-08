package org.nkjmlab.sorm4j.sqlstatement;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * An builder of select SQL.
 *
 * @author nkjm
 *
 */
public class SelectBuilderImpl implements SelectBuilder {

  SelectBuilderImpl() {}

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
  public SelectBuilder having(Condition condition) {
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
    orderBy(new OrderBy(column, ascOrDesc));
    return this;
  }

  @Override
  public SelectBuilder orderBy(OrderBy... orderBys) {
    this.orderBy = String.join(", ",
        Arrays.stream(orderBys).map(ob -> ob.toString()).collect(Collectors.toList()));
    return this;
  }

  @Override
  public String buildSqlString() {
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
  public SelectBuilder where(Condition condition) {
    where(condition.toString());
    return this;
  }

  @Override
  public SelectBuilder where(String expr) {
    where = expr;
    return this;
  }



  /**
   * Value object represents conditions of where clause or having clause.
   */

  public static class Condition {
    private final Object condition;

    Condition(Object expr) {
      this.condition = expr;
    }

    Condition(String op, Object... conds) {
      this("(" + String.join(" " + op + " ",
          Arrays.stream(conds).map(c -> c.toString()).collect(Collectors.toList())) + ")");
    }

    Condition(String left, String op, String right) {
      this.condition = left + op + right;
    }


    @Override
    public String toString() {
      return condition.toString();
    }
  }

  /**
   * Value object for order by clause.
   */
  public static class OrderBy {
    private final String column;
    private final String ascOrDesc;

    OrderBy(String column, String ascOrDesc) {
      this.column = column;
      this.ascOrDesc = ascOrDesc;
    }

    @Override
    public String toString() {
      return column + " " + ascOrDesc;
    }
  }

}
