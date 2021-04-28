package org.nkjmlab.sorm4j.sql.helper;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.sql.SelectStringBuilderImpl;


/**
 * API of creates a select SQL statement.
 *
 * @author nkjm
 *
 */
@Experimental
public interface SelectStringBuilder {

  /**
   * Value object represents conditions of where clause or having clause. This object could include
   * AND and OR operators.
   */

  class Condition {
    private final Object condition;

    public Condition(Object expr) {
      this.condition = expr;
    }

    public Condition(String op, Object... conds) {
      this("(" + String.join(" " + op + " ",
          Arrays.stream(conds).map(c -> c.toString()).collect(Collectors.toList())) + ")");
    }

    public Condition(String left, String op, String right) {
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
  class OrderBy {
    private final String column;
    private final String ascOrDesc;

    public OrderBy(String column, String ascOrDesc) {
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
  public static SelectStringBuilder.Condition and(Object... conds) {
    return new SelectStringBuilder.Condition("and", conds);
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
  public static String as(String col, String alias) {
    return col + " as " + alias;
  }

  /**
   * <p>
   * Creates {@link SelectStringBuilder.Condition} instance.
   *
   * <p>
   * For example,
   *
   * <pre>
   * and(cond("id=?"), "name=?")  returns "id=? and name=?"
   * </pre>
   */
  public static SelectStringBuilder.Condition cond(String cond) {
    return new SelectStringBuilder.Condition(cond);
  }

  public static SelectStringBuilder.Condition cond(String left, String op, String right) {
    return new SelectStringBuilder.Condition(left, op, right);
  }

  /**
   * Create {@link SelectStringBuilder} object.
   *
   * @return
   */
  public static SelectStringBuilder create() {
    return new SelectStringBuilderImpl();
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
  public static SelectStringBuilder.Condition or(Object... conds) {
    return new SelectStringBuilder.Condition("or", conds);
  }

  /**
   * Create {@link SelectStringBuilder.OrderBy} objects.
   *
   * @param column
   * @param ascOrDesc
   * @return
   */
  public static SelectStringBuilder.OrderBy order(String column, String ascOrDesc) {
    return new SelectStringBuilder.OrderBy(column, ascOrDesc);
  }


  /**
   * Creates a select SQL statement from the objects.
   *
   * @return
   */
  String build();

  /**
   * Add distinct keyword to SQL.
   */
  SelectStringBuilder distinct();

  /**
   * Create from clause.
   * <p>
   *
   * <pre>
   * from("player") returns "from player"
   * </pre>
   *
   * @param table
   * @return
   */
  SelectStringBuilder from(String table);

  /**
   * Create group by clause.
   *
   * @param columns
   * @return
   */
  SelectStringBuilder groupBy(String... columns);

  /**
   * Create having clause with the given {@link SelectStringBuilder.Condition}.
   *
   * @param condition
   * @return
   */
  SelectStringBuilder having(SelectStringBuilder.Condition condition);


  /**
   * Create having clause.
   *
   * @param expr
   * @return
   */
  SelectStringBuilder having(String expr);

  /**
   * Create limit clause.
   *
   * @param limit
   * @return
   */
  SelectStringBuilder limit(int limit);


  /**
   * Create limit clause with offset.
   *
   * @param limit
   * @return
   */
  SelectStringBuilder limit(int limit, int offset);

  /**
   * Create order by clause.
   *
   * @param orderBys
   * @return
   */
  SelectStringBuilder orderBy(SelectStringBuilder.OrderBy... orderBys);

  /**
   * Create order by clause.
   *
   * @param column
   * @param ascOrDesc
   * @return
   */
  SelectStringBuilder orderBy(String column, String ascOrDesc);

  /**
   * Create select clause. The default value is "*".
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
  SelectStringBuilder select(String... columns);

  /**
   * Create prettified string.
   *
   * @return
   */
  String toPrettyString();


  /**
   * Create prettified or plain string.
   *
   * @param prettyPrint
   * @return
   */
  String toPrettyString(boolean prettyPrint);


  /**
   * Create where clause.
   *
   * @param condition
   * @return
   */
  SelectStringBuilder where(SelectStringBuilder.Condition condition);

  /**
   * Create where clause.
   *
   * @param expr
   * @return
   */
  SelectStringBuilder where(String expr);
}
