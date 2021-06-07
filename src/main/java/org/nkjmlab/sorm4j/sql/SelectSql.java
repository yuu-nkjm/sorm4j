package org.nkjmlab.sorm4j.sql;

import static org.nkjmlab.sorm4j.sql.SqlKeyword.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.annotation.Experimental;


/**
 * API of creates a select SQL statement.
 *
 * @author nkjm
 *
 */
@Experimental
public class SelectSql {

  public static class Builder {

    private String columns = "*";

    private boolean distinct;

    private String groupBy;

    private String having;

    private String limit;

    private String orderBy;

    private String table;

    private String where;


    /**
     * Creates a select SQL statement from the objects.
     *
     * @return
     */
    public String build() {
      return toPrettyString(false);
    }

    /**
     * Add distinct keyword to SQL.
     */

    public Builder distinct() {
      this.distinct = true;
      return this;
    }


    /**
     * Creates from clause.
     * <p>
     *
     * <pre>
     * from("player") returns "from player"
     * </pre>
     *
     * @param table
     * @return
     */
    public Builder from(String table) {
      this.table = table;
      return this;
    }


    /**
     * Creates group by clause.
     *
     * @param columns
     * @return
     */
    public Builder groupBy(String... columns) {
      groupBy = String.join(",", Arrays.stream(columns).collect(Collectors.toList()));
      return this;
    }


    /**
     * Creates having clause with the given {@link Condition}.
     *
     * @param condition
     * @return
     */
    public Builder having(Condition condition) {
      having(condition.toString());
      return this;
    }



    /**
     * Creates having clause.
     *
     * @param expr
     * @return
     */
    public Builder having(String expr) {
      having = expr;
      return this;
    }


    /**
     * Creates limit clause.
     *
     * @param limit
     * @return
     */
    public Builder limit(int limit) {
      return limit(limit, 0);
    }



    /**
     * Creates limit clause with offset.
     *
     * @param limit
     * @return
     */
    public Builder limit(int limit, int offset) {
      this.limit = limit + (offset > 0 ? " offset " + offset : "");
      return this;
    }


    /**
     * Creates order by clause.
     *
     * @param orderBys
     * @return
     */
    public Builder orderBy(OrderBy... orderBys) {
      this.orderBy = String.join(", ",
          Arrays.stream(orderBys).map(ob -> ob.toString()).collect(Collectors.toList()));
      return this;
    }


    /**
     * Creates order by clause.
     *
     * @param column
     * @param ascOrDesc
     * @return
     */
    public Builder orderBy(String column, String ascOrDesc) {
      orderBy(new OrderBy(column, ascOrDesc));
      return this;
    }


    /**
     * Creates select clause. The default value is "*".
     * <p>
     * For example,
     *
     * <pre>
     * select("id","name","age") returns "select id, name, age"
     * </pre>
     *
     * @param columns
     * @return
     */
    public Builder select(String... columns) {
      this.columns = String.join(", ", Arrays.stream(columns).collect(Collectors.toList()));
      return this;
    }


    /**
     * Creates prettified string.
     *
     * @return
     */
    public String toPrettyString() {
      return toPrettyString(true);
    }

    /**
     * Creates prettified or plain string.
     *
     * @param prettyPrint
     * @return
     */
    public String toPrettyString(boolean prettyPrint) {
      StringBuilder sql = new StringBuilder("select ");
      if (distinct) {
        sql.append("distinct ");
      }
      sql.append(columns);
      sql.append(prettyPrint ? System.lineSeparator() : "");
      sql.append(FROM + table);
      if (where != null) {
        sql.append(prettyPrint ? System.lineSeparator() : "");
        sql.append(WHERE + where);
      }
      if (groupBy != null) {
        sql.append(prettyPrint ? System.lineSeparator() : "");
        sql.append(GROUP_BY + groupBy);
      }
      if (having != null) {
        sql.append(prettyPrint ? System.lineSeparator() : "");
        sql.append(HAVING + having);
      }
      if (orderBy != null) {
        sql.append(prettyPrint ? System.lineSeparator() : "");
        sql.append(ORDER_BY + orderBy);
      }
      if (limit != null) {
        sql.append(prettyPrint ? System.lineSeparator() : "");
        sql.append(LIMIT + limit);
      }

      return sql.toString();
    }


    @Override
    public String toString() {
      return toPrettyString(false);
    }



    /**
     * Creates where clause.
     *
     * @param condition
     * @return
     */
    public Builder where(Condition condition) {
      where(condition.toString());
      return this;
    }


    /**
     * Creates where clause.
     *
     * @param expr
     * @return
     */
    public Builder where(String expr) {
      where = expr;
      return this;
    }

  }

  /**
   * Value object represents conditions of where clause or having clause. This object could include
   * AND and OR operators.
   */

  public static class Condition {
    private final Object condition;

    private Condition(Object expr) {
      this.condition = expr;
    }

    /**
     * Concatenates conditions with the given operator.
     *
     * @param op
     * @param conds
     */
    private Condition(String op, Object... conds) {
      this("(" + String.join(" " + op + " ",
          Arrays.stream(conds).map(c -> c.toString()).collect(Collectors.toList())) + ")");
    }

    /**
     * Condition with binary operator
     *
     * @param left
     * @param op
     * @param right
     */
    private Condition(Object left, String op, Object right) {
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
    private final String ascOrDesc;
    private final String column;

    private OrderBy(String column, String ascOrDesc) {
      this.column = column;
      this.ascOrDesc = ascOrDesc;
    }

    @Override
    public String toString() {
      return column + " " + ascOrDesc;
    }
  }

  /**
   * <p>
   * Creates AND condition with concatenating arguments.
   * <p>
   * For example,
   *
   * <pre>
   * and("id=?", "name=?") returns "id=? and name=?"
   * </pre>
   *
   * @param conds condition in String or Condition
   * @return
   */
  public static Condition and(Object... conds) {
    return new Condition("and", conds);
  }



  /**
   * <p>
   * Creates AS alias.
   *
   * <p>
   * For example,
   *
   * <pre>
   * as("avg(score)", "avg_score")  returns "avg(score) as avg_score"
   * </pre>
   */
  public static String as(Object src, String alias) {
    return src + AS + alias;
  }

  public static String between(String colName, Object beginExp, Object endExp) {
    return wrapSpace(colName + BETWEEN + literal(beginExp) + AND + literal(endExp));
  }

  public static String castAs(String src, String toType) {
    return wrapSpace(CAST + wrapParentheses(src + AS + toType));
  }


  public static String column(String tableName, String... colNames) {
    return Arrays.stream(colNames).map(col -> column(tableName, col))
        .collect(Collectors.joining(", "));
  }


  /**
   * <p>
   * Creates {@link Condition} instance.
   *
   * <p>
   * For example,
   *
   * <pre>
   * and(cond("id=?"), "name=?")  returns "id=? and name=?"
   * </pre>
   */
  public static Condition condition(String cond) {
    return new Condition(cond);
  }

  /**
   * Condition with binary operator
   *
   * @param left
   * @param op
   * @param right
   */

  public static Condition condition(Object left, String op, Object right) {
    return new Condition(left, op, right);
  }

  public static String cond(Object left, String op, Object right) {
    return left + op + right;
  }


  public static String count(String colName) {
    return wrapSpace(COUNT + wrapParentheses(colName));
  }


  public static String from(String tableName) {
    return wrapSpace(FROM + tableName);
  }

  public static String groupBy(String... groups) {
    return wrapSpace(GROUP_BY + joinCommaAndSpace(groups));
  }

  public static String limit(int limit) {
    return wrapSpace(LIMIT + limit);
  }

  /**
   * Converts the given arguments to SQL literal.
   *
   * @param element
   * @return
   */

  public static String literal(Object element) {
    if (element == null) {
      return "null";
    } else if (element.getClass().isArray()) {
      return "[" + String.join(", ",
          ((List<?>) element).stream().map(e -> literal(e)).toArray(String[]::new)) + "]";
    } else if (element instanceof List) {
      return String.join(", ",
          ((List<?>) element).stream().map(e -> literal(e)).toArray(String[]::new));
    }

    final String str = element.toString();
    if (element instanceof Number || element instanceof Boolean) {
      return str;
    }
    switch (str) {
      case "?":
        return str;
      default:
        return quote(str);
    }
  }


  public static String func(String functionName, String column) {
    return wrapSpace(functionName + wrapParentheses(column));
  }

  public static String func(String functionName, String... columns) {
    return wrapSpace(functionName + wrapParentheses(joinCommaAndSpace(columns)));
  }


  /**
   * Creates {@link Builder} object.
   *
   * @return
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * <p>
   * Creates OR condition with concatenating arguments.
   * <p>
   * For example,
   *
   * <pre>
   * or("id=?", "name=?") returns "id=? or name=?"
   * </pre>
   */
  public static Condition or(Object... conds) {
    return new Condition("or", conds);
  }

  /**
   * Creates {@link OrderBy} objects.
   *
   * @param column
   * @param ascOrDesc
   * @return
   */
  public static OrderBy orderBy(String column, String ascOrDesc) {
    return new OrderBy(column, ascOrDesc);
  }

  public static String orderBy(String column) {
    return wrapSpace(ORDER_BY + column);
  }

  public static String orderByAsc(String column) {
    return wrapSpace(ORDER_BY + column + ASC);
  }

  public static String orderByDesc(String column) {
    return wrapSpace(ORDER_BY + column + DESC);
  }

  /**
   * Returns single quoted expression. If it includes single quotations, they will be escaped.
   *
   * @param str
   * @return
   */
  public static String quote(String str) {
    return wrapSingleQuote(str.contains("'") ? str.replaceAll("'", "''") : str);
  }


  public static String select(String selectClause) {
    return wrapSpace(SELECT + selectClause);
  }

  public static String select(String... selectClauses) {
    return wrapSpace(SELECT + joinCommaAndSpace(selectClauses));
  }

  public static String selectDistinct(String... selectClauses) {
    return wrapSpace(SELECT + DISTINCT + joinCommaAndSpace(selectClauses));
  }

  public static String selectStar() {
    return wrapSpace(select(STAR));
  }

  public static String selectStarFrom(String tableName) {
    return wrapSpace(selectStar() + from(tableName));
  }

  public static String sum(String column) {
    return wrapSpace(SUM + wrapParentheses(column));
  }

  public static String where(String whereClause) {
    return wrapSpace(WHERE + whereClause);
  }

  private static String joinCommaAndSpace(String... elements) {
    return String.join(", ", elements);
  }

  private static String wrapParentheses(String str) {
    return "(" + str + ")";
  }

  private static String wrapSingleQuote(String str) {
    return "'" + str + "'";
  }

  private static String wrapSpace(String str) {
    return " " + str + " ";
  }
}
