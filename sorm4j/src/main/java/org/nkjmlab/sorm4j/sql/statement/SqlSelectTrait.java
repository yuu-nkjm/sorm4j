package org.nkjmlab.sorm4j.sql.statement;

/** Trait for SELECT clause and query structure. */
public interface SqlSelectTrait {
  default String select(String selectClause) {
    return SelectSql.select(selectClause);
  }

  default String select(Object... selectClauses) {
    return SelectSql.select(selectClauses);
  }

  default String selectDistinct(Object... selectClauses) {
    return SelectSql.selectDistinct(selectClauses);
  }

  default String selectStarFrom(String tableName) {
    return SelectSql.selectStarFrom(tableName);
  }

  default String selectCountFrom(String tableName) {
    return SelectSql.selectCountFrom(tableName);
  }

  default String as(Object src, String alias) {
    return SelectSql.as(src, alias);
  }

  default String castAs(String src, String toType) {
    return SelectSql.castAs(src, toType);
  }

  default String columnWithTableName(String tableName, String... colNames) {
    return SelectSql.columnWithTableName(tableName, colNames);
  }

  default String column(String tableName, String colName) {
    return SelectSql.column(tableName, colName);
  }

  default String from(String tableName) {
    return SelectSql.from(tableName);
  }

  default String where() {
    return SelectSql.where();
  }

  default String where(String searchCondition) {
    return SelectSql.where(searchCondition);
  }

  default String where(ConditionSql searchCondition) {
    return SelectSql.where(searchCondition);
  }

  default String op(Object left, String operator, Object right) {
    return SelectSql.op(left, operator, right);
  }

  default String groupBy(Object... groups) {
    return SelectSql.groupBy(groups);
  }

  default String limit(Object limit) {
    return SelectSql.limit(limit);
  }

  default String orderBy(Object... order) {
    return SelectSql.orderBy(order);
  }

  default String orderBy(Object column) {
    return SelectSql.orderBy(column);
  }

  default String orderByAsc(Object column) {
    return SelectSql.orderByAsc(column);
  }

  default String orderByDesc(Object column) {
    return SelectSql.orderByDesc(column);
  }

  default String func(String functionName, Object args) {
    return SelectSql.func(functionName, args);
  }

  default String func(String functionName, Object... args) {
    return SelectSql.func(functionName, args);
  }

  default String count(String column) {
    return SelectSql.count(column);
  }

  default String sum(String column) {
    return SelectSql.sum(column);
  }

  default String avg(String column) {
    return SelectSql.avg(column);
  }
}
