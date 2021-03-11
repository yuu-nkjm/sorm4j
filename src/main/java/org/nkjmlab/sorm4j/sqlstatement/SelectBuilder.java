package org.nkjmlab.sorm4j.sqlstatement;

import org.nkjmlab.sorm4j.sqlstatement.SelectBuilderImpl.Condition;
import org.nkjmlab.sorm4j.sqlstatement.SelectBuilderImpl.OrderBy;


/**
 * API of cleate a select SQL statement.
 *
 * @author nkjm
 *
 */
public interface SelectBuilder {

  /**
   * Create select clause.
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
  SelectBuilder select(String... columns);

  /**
   * Add distinct keyword to SQL.
   */
  SelectBuilder distinct();

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
  SelectBuilder from(String table);

  /**
   * Create group by clause.
   *
   * @param columns
   * @return
   */
  SelectBuilder groupBy(String... columns);

  /**
   * Create having clause with the given {@link Condition}.
   *
   * @param condition
   * @return
   */
  SelectBuilder having(Condition condition);

  /**
   * Create having clause.
   *
   * @param expr
   * @return
   */
  SelectBuilder having(String expr);

  /**
   * Create limit clause.
   *
   * @param limit
   * @return
   */
  SelectBuilder limit(int limit);

  /**
   * Create limit clause with offset.
   *
   * @param limit
   * @return
   */
  SelectBuilder limit(int limit, int offset);

  SelectBuilder orderBy(String column, String ascOrDesc);

  SelectBuilder orderBy(OrderBy... orderBys);

  /**
   * Creates a select SQL statement from the objects.
   *
   * @return
   */
  String buildSql();

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
  SelectBuilder where(Condition condition);

  /**
   * Create where clause.
   *
   * @param expr
   * @return
   */
  SelectBuilder where(String expr);


  /**
   * Create {@link SelectBuilder} object.
   *
   * @return
   */
  public static SelectBuilder create() {
    return new SelectBuilderImpl();
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
  public static String as(String col, String alias) {
    return col + " as " + alias;
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

  public static Condition cond(String left, String op, String right) {
    return new Condition(left, op, right);
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
   * Create {@link OrderBy} objects.
   *
   * @param column
   * @param ascOrDesc
   * @return
   */
  public static OrderBy order(String column, String ascOrDesc) {
    return new OrderBy(column, ascOrDesc);
  }

  /**
   * Returns single quoted string.
   *
   * @param expr
   * @return
   */
  public static String q(String expr) {
    return "'" + expr + "'";
  }
}
