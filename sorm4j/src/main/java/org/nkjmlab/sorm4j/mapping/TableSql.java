package org.nkjmlab.sorm4j.mapping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.nkjmlab.sorm4j.internal.mapping.SqlParametersToTableMapping;

/**
 * Sqls generated by {@link SqlParametersToTableMapping}.
 *
 * @author nkjm
 *
 */
public final class TableSql {

  private static final Map<String, String> multiRowSqlMap = new ConcurrentHashMap<>();

  public static void clearMultiRowSqlCache() {
    multiRowSqlMap.clear();
  }

  private final String insertPlaceholders;
  private final String mergePlaceholders;
  private final String selectByPrimaryKeySql;
  private final String selectAllSql;
  private final String insertSql;
  private final String updateSql;
  private final String deleteSql;
  private final String mergeSql;
  private final String existsSql;
  private final String insertSqlPrefix;
  private final String mergeSqlPrefix;

  public TableSql(String inserPlaceholders, String mergePlaceholders, String selectByPrimaryKeySql,
      String selectAllSql, String insertSql, String updateSql, String deleteSql, String mergeSql,
      String existsSql, String insertSqlPrefix, String mergeSqlPrefix) {
    this.insertPlaceholders = inserPlaceholders;
    this.mergePlaceholders = mergePlaceholders;
    this.selectByPrimaryKeySql = selectByPrimaryKeySql;
    this.selectAllSql = selectAllSql;
    this.insertSql = insertSql;
    this.updateSql = updateSql;
    this.deleteSql = deleteSql;
    this.mergeSql = mergeSql;
    this.existsSql = existsSql;
    this.insertSqlPrefix = insertSqlPrefix;
    this.mergeSqlPrefix = mergeSqlPrefix;
  }

  public String getDeleteSql() {
    return deleteSql;
  }

  public String getInsertSql() {
    return insertSql;
  }

  public String getMergeSql() {
    return mergeSql;
  }


  public String getMultirowInsertSql(int num) {
    return getSqlWithMultirowPlaceholders(insertSqlPrefix, insertPlaceholders, num);
  }

  public String getMultirowMergeSql(int num) {
    return getSqlWithMultirowPlaceholders(mergeSqlPrefix, mergePlaceholders, num);
  }

  private String getSqlWithMultirowPlaceholders(String sqlPrefix, String placeHolders, int num) {
    return multiRowSqlMap.computeIfAbsent(sqlPrefix + num, n -> sqlPrefix + String.join(",",
        Stream.generate(() -> placeHolders).limit(num).collect(Collectors.toList())));
  }

  public String getSelectAllSql() {
    return selectAllSql;
  }

  public String getSelectByPrimaryKeySql() {
    return selectByPrimaryKeySql;
  }

  public String getUpdateSql() {
    return updateSql;
  }

  public String getExistsSql() {
    return existsSql;
  }

}

