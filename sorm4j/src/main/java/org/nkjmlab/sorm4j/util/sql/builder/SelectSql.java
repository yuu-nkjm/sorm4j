package org.nkjmlab.sorm4j.util.sql.builder;

import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.AND;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.AS;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.BETWEEN;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.FROM;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.GROUP_BY;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.HAVING;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.IN;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.LIMIT;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.ORDER_BY;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.WHERE;
import static org.nkjmlab.sorm4j.util.sql.SqlStringUtils.join;
import static org.nkjmlab.sorm4j.util.sql.SqlStringUtils.literal;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.nkjmlab.sorm4j.common.Experimental;
import org.nkjmlab.sorm4j.util.sql.SqlStringUtils;

/**
 * API of creates a select SQL statement.
 *
 * @author nkjm
 */
@Experimental
public final class SelectSql {

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

    private boolean distinct = false;

    private String from;

    private String where;

    private String groupBy;
    private String orderBy;

    private String having;
    private String limit;

    private Builder() {}

    /**
     * Creates a select SQL statement from the objects.
     *
     * @return
     */
    public String build() {
      return toString();
    }

    /** Add distinct keyword to SQL. */
    public Builder distinct() {
      this.distinct = true;
      return this;
    }

    /**
     * Creates from clause.
     *
     * <p>
     *
     * <pre>
     * from("player") returns "from player"
     * </pre>
     *
     * @param tables
     * @return
     */
    public Builder from(String... tables) {
      this.from = String.join(",", tables);
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
     *
     * <p>For example,
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
      sql.append(FROM + from);
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
      this(wrapParentheses(join(wrapSpace(op), conds)));
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
    return wrapSpace("select " + selectClause);
  }

  public static String select(Object... selectClauses) {
    return wrapSpace("select " + join(", ", selectClauses));
  }

  public static String selectDistinct(Object... selectClauses) {
    return wrapSpace("select distinct " + join(", ", selectClauses));
  }

  public static String selectStarFrom(String tableName) {
    return wrapSpace("select *" + from(tableName));
  }

  public static String selectCountFrom(String tableName) {
    return wrapSpace("select count(*)" + from(tableName));
  }

  /**
   * Creates AS alias.
   *
   * <p>For example,
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
    return wrapSpace("cast" + wrapParentheses(src + AS + toType));
  }

  public static String columnWithTableName(String tableName, String... colNames) {
    return wrapSpace(
        Arrays.stream(colNames)
            .map(col -> column(tableName, col))
            .collect(Collectors.joining(",")));
  }

  public static String column(String tableName, String colName) {
    return wrapSpace(tableName + "." + colName);
  }

  public static String from(String tableName) {
    return wrapSpace("from " + tableName);
  }

  public static String where() {
    return WHERE;
  }

  public static String where(String searchCondition) {
    return wrapSpace("where " + searchCondition);
  }

  public static String where(Condition searchCondition) {
    return where(searchCondition.toString());
  }

  /** operator * */

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
   * Creates {@link Condition} instance.
   *
   * <p>For example,
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

  /** Conditions * */
  /**
   * Creates AND condition with concatenating arguments.
   *
   * <p>For example,
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
   * Creates OR condition with concatenating arguments.
   *
   * <p>For example,
   *
   * <pre>
   * or("id=?", "name=?") returns "id=? or name=?"
   * </pre>
   */
  public static Condition or(Object... conds) {
    return new Condition("or", conds);
  }

  public static Condition between(Object colName, Object startInclusive, Object endInclusive) {
    return new Condition(
        wrapSpace(
            colName
                + BETWEEN
                + literal(startInclusive)
                + AND
                + SqlStringUtils.literal(endInclusive)));
  }

  public static Condition in(Object colName, List<?> values) {
    return new Condition(wrapSpace(colName + IN + wrapParentheses(literal(values))));
  }

  public static String groupBy(Object... groups) {
    return wrapSpace("group by " + join(", ", groups));
  }

  public static String limit(Object limit) {
    return wrapSpace("limit " + limit);
  }

  /**
   * Creates orderBy clause.
   *
   * @param order
   * @return
   */
  public static String orderBy(Object... order) {
    return wrapSpace("order by " + join(" ", order));
  }

  public static String orderBy(Object column) {
    return orderBy(column, "");
  }

  public static String orderByAsc(Object column) {
    return orderBy(column, "asc");
  }

  public static String orderByDesc(Object column) {
    return orderBy(column, "desc");
  }

  /** functions * */
  public static String func(String functionName, Object args) {
    return wrapSpace(functionName + wrapParentheses(args));
  }

  public static String func(String functionName, Object... args) {
    return wrapSpace(functionName + wrapParentheses(join(", ", args)));
  }

  public static String count(String column) {
    return func("count", column);
  }

  public static String sum(String column) {
    return func("sum", column);
  }

  public static String avg(String column) {
    return func("avg", column);
  }

  /**
   * Wraps the given string in parentheses. If the argument is null, the method returns null.
   *
   * @param str
   * @return
   */
  private static String wrapParentheses(Object str) {
    return str == null ? null : "(" + str + ")";
  }

  /**
   * Wraps the given string in spaces. If the argument is null, the method returns null.
   *
   * @param str
   * @return
   */
  private static String wrapSpace(Object str) {
    return str == null ? null : " " + str.toString().trim() + " ";
  }
}
