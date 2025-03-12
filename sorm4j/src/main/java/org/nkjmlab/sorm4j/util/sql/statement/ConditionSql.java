package org.nkjmlab.sorm4j.util.sql.statement;

import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.AND;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.BETWEEN;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.IN;
import static org.nkjmlab.sorm4j.util.sql.SqlStringUtils.join;
import static org.nkjmlab.sorm4j.util.sql.SqlStringUtils.literal;

import java.util.List;

import org.nkjmlab.sorm4j.util.sql.SqlStringUtils;

/**
   * Value object represents conditions of where clause or having clause. This object could include
   * AND and OR operators.
   */
  public class ConditionSql {
    private final Object condition;

    private ConditionSql(Object expr) {
      this.condition = expr;
    }

    /**
     * Concatenates conditions with the given operator.
     *
     * @param op
     * @param conds
     */
    private ConditionSql(String op, Object... conds) {
      this(SelectSql.wrapParentheses(join(SelectSql.wrapSpace(op), conds)));
    }

    /**
     * Condition with binary operator
     *
     * @param left
     * @param op
     * @param right
     */
    private ConditionSql(Object left, String op, Object right) {
      this.condition = left + " " + op.trim() + " " + right;
    }

    /**
     * Creates {@link ConditionSql} instance.
     *
     * <p>For example,
     *
     * <pre>
     * and(cond("id=?"), "name=?")  returns "id=? and name=?"
     * </pre>
     */
    public static ConditionSql cond(String cond) {
      return new ConditionSql(cond);
    }

  /**
     * Creates a condition with binary operator
     *
     * @param left
     * @param operator
     * @param right
     */
    public static ConditionSql cond(Object left, String operator, Object right) {
      return new ConditionSql(left, operator, right);
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
    public static ConditionSql and(Object... conds) {
      return new ConditionSql("and", conds);
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
    public static ConditionSql or(Object... conds) {
      return new ConditionSql("or", conds);
    }

  public static ConditionSql between(Object colName, Object startInclusive, Object endInclusive) {
      return new ConditionSql(
          SelectSql.wrapSpace(
              colName
                  + BETWEEN
                  + literal(startInclusive)
                  + AND
                  + SqlStringUtils.literal(endInclusive)));
    }

  public static ConditionSql in(Object colName, List<?> values) {
      return new ConditionSql(SelectSql.wrapSpace(colName + IN + SelectSql.wrapParentheses(literal(values))));
    }

  @Override
    public String toString() {
      return condition.toString();
    }
  }