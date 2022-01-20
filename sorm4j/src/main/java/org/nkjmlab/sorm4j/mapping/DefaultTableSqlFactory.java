package org.nkjmlab.sorm4j.mapping;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.result.TableMetaData;

public final class DefaultTableSqlFactory implements TableSqlFactory {

  @Override
  public TableSql create(TableMetaData tableMetaData, Class<?> objectClass, Connection connection) {
    throwExeptionIfPrimaryKeyIsNotExist(tableMetaData);

    String tableName = tableMetaData.getTableName();
    List<String> columns = tableMetaData.getColumns();
    List<String> primaryKeys = tableMetaData.getPrimaryKeys();
    List<String> notAutoGeneratedColumns = tableMetaData.getNotAutoGeneratedColumns();


    String insertSqlPrefix =
        "insert into " + tableName + " (" + toColumList(notAutoGeneratedColumns) + ") values";
    String mergeSqlPrefix =
        "merge into " + tableName + " (" + toColumList(notAutoGeneratedColumns) + ") values";

    String insertOrMergePlaceholders =
        " (" + generatePlaceholders(notAutoGeneratedColumns.size()) + ") ";

    String insertSql = insertSqlPrefix + insertOrMergePlaceholders;
    String mergeSql = mergeSqlPrefix + insertOrMergePlaceholders;


    String selectAllSql = "select " + toColumList(columns) + " from " + tableName;
    String selectByPrimaryKeySql =
        selectAllSql + " " + createWhereClauseIdentifyByPrimaryKeys(primaryKeys);

    String existsSql =
        "select 1 from " + tableName + createWhereClauseIdentifyByPrimaryKeys(primaryKeys);


    String updateSql =
        "update " + tableName + createUpdateSetClause(tableMetaData.getNotPrimaryKeys())
            + createWhereClauseIdentifyByPrimaryKeys(primaryKeys);
    String deleteSql =
        "delete from " + tableName + createWhereClauseIdentifyByPrimaryKeys(primaryKeys);

    return new TableSql(insertOrMergePlaceholders, selectByPrimaryKeySql, selectAllSql, insertSql,
        updateSql, deleteSql, mergeSql, existsSql, insertSqlPrefix, mergeSqlPrefix);
  }

  private static void throwExeptionIfPrimaryKeyIsNotExist(TableMetaData tableMetaData) {
    if (!tableMetaData.hasPrimaryKey()) {
      throw new SormException("This opperation requiers primary keys but Table ["
          + tableMetaData.getTableName() + "] doesn't have them.");
    }
  }

  private static String toColumList(List<String> columns) {
    return String.join(",", columns);
  }

  private static String createUpdateSetClause(List<String> notPrimaryKeys) {
    return " set " + String.join(",",
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
