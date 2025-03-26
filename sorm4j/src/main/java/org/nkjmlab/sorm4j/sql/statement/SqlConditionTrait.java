package org.nkjmlab.sorm4j.sql.statement;

import java.util.List;

/** Trait for WHERE/HAVING clause condition composition. */
public interface SqlConditionTrait {
  default ConditionSql cond(String cond) {
    return ConditionSql.cond(cond);
  }

  default ConditionSql cond(Object left, String operator, Object right) {
    return ConditionSql.cond(left, operator, right);
  }

  default ConditionSql and(Object... conds) {
    return ConditionSql.and(conds);
  }

  default ConditionSql or(Object... conds) {
    return ConditionSql.or(conds);
  }

  default ConditionSql between(Object colName, Object startInclusive, Object endInclusive) {
    return ConditionSql.between(colName, startInclusive, endInclusive);
  }

  default ConditionSql in(Object colName, List<?> values) {
    return ConditionSql.in(colName, values);
  }
}
