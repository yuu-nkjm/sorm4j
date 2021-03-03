package org.nkjmlab.sorm4j.mapping;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SqlFromTableMapping {

  private final String selectByPrimaryKeySql;
  private final String selectAllSql;
  private final String insertSql;
  private final String updateSql;
  private final String deleteSql;
  private final String deleteAllSql;
  private final String mergeSql;
  private final String insertSqlPrefix;
  private final String mergeSqlPrefix;
  private final String insertOrMergePlaceholders;

  SqlFromTableMapping(String tableName, List<String> primaryKeys, List<String> notPrimaryKeys,
      List<String> autoGeneratedColumns, List<String> notAutoGeneratedColumns,
      List<String> allColumns) {

    this.insertSqlPrefix =
        "insert into " + tableName + " (" + toColumList(notAutoGeneratedColumns) + ") values";
    this.mergeSqlPrefix =
        "merge into " + tableName + " (" + toColumList(notAutoGeneratedColumns) + ") values";

    this.insertOrMergePlaceholders =
        " (" + generatePlaceholders(notAutoGeneratedColumns.size()) + ") ";

    this.insertSql = insertSqlPrefix + insertOrMergePlaceholders;
    this.mergeSql = mergeSqlPrefix + insertOrMergePlaceholders;


    this.selectAllSql = "select " + toColumList(allColumns) + " from " + tableName;
    this.selectByPrimaryKeySql =
        selectAllSql + " " + createWhereClauseIdentifyByPrimaryKeys(primaryKeys);

    this.updateSql = "update " + tableName + createUpdateSetClause(notPrimaryKeys)
        + createWhereClauseIdentifyByPrimaryKeys(primaryKeys);
    this.deleteSql =
        "delete from " + tableName + createWhereClauseIdentifyByPrimaryKeys(primaryKeys);
    this.deleteAllSql = "delete from " + tableName;
  }

  private String createUpdateSetClause(List<String> notPrimaryKeys) {
    return " set " + String.join(",",
        notPrimaryKeys.stream().map(npk -> npk + "=?").collect(Collectors.toList()));
  }

  private String createWhereClauseIdentifyByPrimaryKeys(List<String> primaryKeys) {
    return " where " + String.join(" and ",
        primaryKeys.stream().map(pk -> pk + "=?").collect(Collectors.toList()));
  }

  private String generatePlaceholders(int num) {
    return String.join(",", Stream.generate(() -> "?").limit(num).collect(Collectors.toList()));
  }

  private String toColumList(List<String> columns) {
    return String.join(",", columns);
  }


  public String getSelectByPrimaryKeySql() {
    return selectByPrimaryKeySql;
  }

  public String getSelectAllSql() {
    return selectAllSql;
  }

  public String getInsertSql() {
    return insertSql;
  }


  public String getMultirowInsertSql(int num) {
    return getPlaceholders(insertSqlPrefix, num);
  }


  public String getMultirowMergeSql(int num) {
    return getPlaceholders(mergeSqlPrefix, num);
  }

  private static final Map<String, String> multiRowSqlMap = new ConcurrentHashMap<>();

  private String getPlaceholders(String sqlPrefix, int num) {
    return multiRowSqlMap.computeIfAbsent(sqlPrefix + num, n -> sqlPrefix + String.join(",",
        Stream.generate(() -> insertOrMergePlaceholders).limit(num).collect(Collectors.toList())));
  }


  public String getMergeSql() {
    return mergeSql;
  }

  public String getUpdateSql() {
    return updateSql;
  }

  public String getDeleteSql() {
    return deleteSql;
  }

  public String getDeleteAllSql() {
    return deleteAllSql;
  }

}

