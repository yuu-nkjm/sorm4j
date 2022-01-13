package org.nkjmlab.sorm4j.util.sql;

import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
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

  private SelectSql() {}

  /**
   * Creates {@link Builder} object.
   *
   * @return
   */
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String columns = "*";

    private boolean distinct;

    private String groupBy;

    private String having;

    private String limit;

    private String orderBy;

    private String table;

    private String where;

    private Builder() {}

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
     * @see <a href="http://www.h2database.com/html/grammar.html#select_order">SQL Grammar</a>
     * @param order
     * @return
     */
    public Builder orderBy(String... order) {
      orderBy = String.join(" ", order);
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
      this(wrapParentheses(joinObjects(wrapSpace(op), conds)));
    }

    /**
     * Condition with binary operator
     *
     * @param left
     * @param op
     * @param right
     */
    private Condition(Object left, String op, Object right) {
      this.condition = left + " " + op.trim() + " " + right;
    }


    @Override
    public String toString() {
      return condition.toString();
    }
  }

  public static String select(String selectClause) {
    return wrapSpace(SELECT + selectClause);
  }

  public static String select(Object... selectClauses) {
    return wrapSpace(SELECT + joinCommaAndSpace(selectClauses));
  }

  public static String selectDistinct(Object... selectClauses) {
    return wrapSpace(SELECT + DISTINCT + joinCommaAndSpace(selectClauses));
  }

  public static String selectStarFrom(String tableName) {
    return SELECT_STAR + from(tableName);
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

  /**
   * Creates cast as.
   *
   * Example
   *
   * <pre>
   * castAs("A", "DOUBLE") generates "cast (A as DOUBLE)"
   *
   * @param src
   * @param toType
   * @return
   */
  public static String castAs(String src, String toType) {
    return wrapSpace(CAST + wrapParentheses(src + AS + toType));
  }

  public static String column(String tableName, String... colNames) {
    return Arrays.stream(colNames).map(col -> column(tableName, col))
        .collect(Collectors.joining(", "));
  }

  public static String from(String tableName) {
    return wrapSpace(FROM + tableName);
  }

  public static String where() {
    return wrapSpace(WHERE);
  }

  public static String where(String searchCondition) {
    return wrapSpace(WHERE + searchCondition);
  }

  public static String where(Condition searchCondition) {
    return where(searchCondition.toString());
  }

  /** operator **/

  /**
   * Creates a string of binary operator and operands which wrapped parentheses.
   *
   * <p>
   * Examples
   *
   * <pre>
   * op(op("A", "/", "B"), "+", op("C", "/", "D"))
   *
   * generates
   *
   * "((A / B) + (C / D))"
   *
   * @param left
   * @param operator
   * @param right
   * @return
   */
  public static String op(Object left, String operator, Object right) {
    return wrapParentheses(left + wrapSpace(operator) + right);
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
  public static Condition cond(String cond) {
    return new Condition(cond);
  }

  /**
   * Creates a condition with binary operator
   *
   * @param left
   * @param operator
   * @param right
   */

  public static Condition cond(Object left, String operator, Object right) {
    return new Condition(left, operator, right);
  }

  /** Conditions **/
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

  public static Condition between(Object colName, Object startInclusive, Object endInclusive) {
    return new Condition(colName + BETWEEN + literal(startInclusive) + AND + literal(endInclusive));
  }

  public static Condition in(Object colName, Object... values) {
    return new Condition(colName + IN + wrapParentheses(
        joinComma(Arrays.stream(values).map(o -> literal(o)).collect(Collectors.toList()))));
  }


  public static String groupBy(Object... groups) {
    return wrapSpace(GROUP_BY + joinCommaAndSpace(groups));
  }

  public static String limit(Object limit) {
    return wrapSpace(LIMIT + limit);
  }

  /**
   * Creates orderBy clause.
   *
   * @param order
   * @return
   */
  public static String orderBy(Object... order) {
    return ORDER_BY + joinSpace(order);
  }

  public static String orderBy(Object column) {
    return orderBy(column, ASC);
  }

  public static String orderByAsc(Object column) {
    return orderBy(ORDER_BY, ASC);
  }

  public static String orderByDesc(Object column) {
    return orderBy(ORDER_BY, DESC);
  }

  /** functions **/

  public static String func(String functionName, Object args) {
    return wrapSpace(functionName + wrapParentheses(args));
  }

  public static String func(String functionName, Object... args) {
    return wrapSpace(functionName + wrapParentheses(joinCommaAndSpace(args)));
  }

  public static String count(String column) {
    return func(COUNT, column);
  }

  public static String sum(String column) {
    return func(SUM, column);
  }

  public static String avg(String column) {
    return func(AVG, column);
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
      final int length = Array.getLength(element);
      List<String> ret = new ArrayList<>(length);
      for (int i = 0; i < length; i++) {
        ret.add(literal(Array.get(element, i)));
      }
      return "[" + String.join(",", ret) + "]";
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

  /**
   * Returns single quoted expression. If it includes single quotations, they will be escaped.
   *
   * @param str
   * @return
   */
  public static String quote(String str) {
    return wrapSingleQuote(str.contains("'") ? str.replaceAll("'", "''") : str);
  }

  public static String joinCommaAndSpace(Object... elements) {
    return joinObjects(", ", elements);
  }

  public static String joinComma(Object... elements) {
    return joinObjects(",", elements);
  }

  public static String joinSpace(Object... elements) {
    return joinObjects(" ", elements);
  }

  public static String joinObjects(String delimiter, Object... elements) {
    return String.join(delimiter,
        Arrays.stream(elements).map(o -> o.toString()).toArray(String[]::new));
  }

  public static String wrapParentheses(Object str) {
    return "(" + str + ")";
  }

  public static String wrapSingleQuote(Object str) {
    return "'" + str + "'";
  }

  public static String wrapSpace(Object str) {
    return " " + str + " ";
  }
}
