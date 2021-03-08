package org.nkjmlab.sorm4j.sqlstatement;

import org.nkjmlab.sorm4j.sqlstatement.SelectBuilderImpl.Condition;
import org.nkjmlab.sorm4j.sqlstatement.SelectBuilderImpl.OrderBy;


public interface SelectBuilder {

  SelectBuilder select(String... columns);

  SelectBuilder distinct();

  SelectBuilder from(String table);

  SelectBuilder groupBy(String... columns);

  SelectBuilder having(Condition condition);

  SelectBuilder having(String expr);

  SelectBuilder limit(int limit);

  SelectBuilder limit(int limit, int offset);

  SelectBuilder orderBy(String column, String ascOrDesc);

  SelectBuilder orderBy(OrderBy... orderBys);

  String buildSqlString();

  String toPrettyString();

  String toPrettyString(boolean prettyPrint);

  SelectBuilder where(Condition condition);

  SelectBuilder where(String expr);


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


  public static OrderBy order(String column, String ascOrDesc) {
    return new OrderBy(column, ascOrDesc);
  }

  public static String q(String expr) {
    return "'" + expr + "'";
  }
}
