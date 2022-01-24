package org.nkjmlab.sorm4j.context;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.nkjmlab.sorm4j.result.TableMetaData;

public final class DefaultTableSqlFactory implements TableSqlFactory {

  @Override
  public TableSql create(TableMetaData tableMetaData, Class<?> objectClass, Connection connection) {
    String tableName = tableMetaData.getTableName();


    List<String> columns = tableMetaData.getColumns();
    String selectAllSql = "select " + joinConnma(columns) + " from " + tableName;

    List<String> notAutoGeneratedColumns = tableMetaData.getNotAutoGeneratedColumns();
    String insertSqlPrefix =
        "insert into " + tableName + " (" + joinConnma(notAutoGeneratedColumns) + ") values";
    String insertPlaceholders = " (" + generatePlaceholders(notAutoGeneratedColumns.size()) + ") ";
    String insertSql = insertSqlPrefix + insertPlaceholders;

    List<String> primaryKeys = tableMetaData.getPrimaryKeys();

    String errorMsg =
        "This opperation requiers primary key but Table [" + tableName + "] doesn't have it.";

    String existsSql = !tableMetaData.hasPrimaryKey() ? errorMsg
        : "select 1 from " + tableName + createWhereClauseIdentifyByPrimaryKeys(primaryKeys);
    String updateSql = !tableMetaData.hasPrimaryKey() ? errorMsg
        : "update " + tableName + createUpdateSetClause(tableMetaData.getNotPrimaryKeys())
            + createWhereClauseIdentifyByPrimaryKeys(primaryKeys);
    String deleteSql = !tableMetaData.hasPrimaryKey() ? errorMsg
        : "delete from " + tableName + createWhereClauseIdentifyByPrimaryKeys(primaryKeys);

    String mergePlaceholders = " (" + generatePlaceholders(columns.size()) + ") ";
    String mergeSqlPrefix = !tableMetaData.hasPrimaryKey() ? ""
        : "merge into " + tableName + " (" + joinConnma(columns) + ")" + " key ("
            + String.join(",", primaryKeys) + ") values";
    String mergeSql =
        !tableMetaData.hasPrimaryKey() ? errorMsg : mergeSqlPrefix + mergePlaceholders;

    String selectByPrimaryKeySql = !tableMetaData.hasPrimaryKey() ? ""
        : selectAllSql + " " + createWhereClauseIdentifyByPrimaryKeys(primaryKeys);

    return new TableSql(insertPlaceholders, mergePlaceholders, selectByPrimaryKeySql, selectAllSql,
        insertSql, updateSql, deleteSql, mergeSql, existsSql, insertSqlPrefix, mergeSqlPrefix);
  }

  private static String joinConnma(List<String> columns) {
    return String.join(", ", columns);
  }

  private static String createUpdateSetClause(List<String> notPrimaryKeys) {
    return " set " + String.join(", ",
        notPrimaryKeys.stream().map(npk -> npk + "=?").collect(Collectors.toList()));
  }

  private static String createWhereClauseIdentifyByPrimaryKeys(List<String> primaryKeys) {
    return " where " + String.join(" and ",
        primaryKeys.stream().map(pk -> pk + "=?").collect(Collectors.toList()));
  }

  private static String generatePlaceholders(int num) {
    return String.join(",", Stream.generate(() -> "?").limit(num).collect(Collectors.toList()));
  }



}
