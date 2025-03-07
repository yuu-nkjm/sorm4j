package org.nkjmlab.sorm4j.internal.context.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.internal.container.TableSql;
import org.nkjmlab.sorm4j.internal.container.sql.metadata.DbMetaData;
import org.nkjmlab.sorm4j.internal.container.sql.metadata.TableMetaData;
import org.nkjmlab.sorm4j.internal.context.TableSqlFactory;

// "select * " is faster than "select col1, col2, ..., coln" in H2 2.1.210. the former is also
// faster than "select tablname.col1, tablname.col2, ..., tablname.coln".

public final class DefaultTableSqlFactory implements TableSqlFactory {

  @Override
  public TableSql create(TableMetaData tableMetaData, DbMetaData databaseMetaData) {
    String tableName = tableMetaData.getTableName();

    List<String> columns = tableMetaData.getColumns();
    String selectAllSql = "select * from " + tableName;

    List<String> notAutoGeneratedColumns = tableMetaData.getNotAutoGeneratedColumns();
    String insertSqlPrefix =
        "insert into " + tableName + " (" + String.join(", ", notAutoGeneratedColumns) + ") values";
    String insertPlaceholders =
        " ("
            + generatePlaceholders(databaseMetaData, tableMetaData, notAutoGeneratedColumns)
            + ") ";
    String insertSql = insertSqlPrefix + insertPlaceholders;

    List<String> primaryKeys = tableMetaData.getPrimaryKeys();

    String errorMsg =
        "This opperation requiers primary key but Table [" + tableName + "] doesn't have it.";

    String whereClauseIdentifyByPrimaryKeys = createWhereClauseIdentifyByPrimaryKeys(primaryKeys);

    String existsSql =
        !tableMetaData.hasPrimaryKey()
            ? errorMsg
            : "select 1 from " + tableName + whereClauseIdentifyByPrimaryKeys;

    UpdateSqlFactory updateSqlFactory =
        new UpdateSqlFactory(
            tableMetaData.hasPrimaryKey(),
            errorMsg,
            tableName,
            tableMetaData.getNotPrimaryKeys(),
            whereClauseIdentifyByPrimaryKeys);
    String updateSql = updateSqlFactory.createUpdateSql(tableMetaData.getNotPrimaryKeys());
    String deleteSql =
        !tableMetaData.hasPrimaryKey()
            ? errorMsg
            : "delete from " + tableName + whereClauseIdentifyByPrimaryKeys;

    String mergePlaceholders =
        " (" + generatePlaceholders(databaseMetaData, tableMetaData, columns) + ") ";
    String mergeSqlPrefix =
        !tableMetaData.hasPrimaryKey()
            ? ""
            : "merge into "
                + tableName
                + " ("
                + String.join(", ", columns)
                + ")"
                + " key ("
                + String.join(",", primaryKeys)
                + ") values";
    String mergeSql =
        !tableMetaData.hasPrimaryKey() ? errorMsg : mergeSqlPrefix + mergePlaceholders;

    String selectByPrimaryKeySql =
        !tableMetaData.hasPrimaryKey() ? "" : selectAllSql + " " + whereClauseIdentifyByPrimaryKeys;

    return new DefaultTableSql(
        insertPlaceholders,
        mergePlaceholders,
        selectByPrimaryKeySql,
        selectAllSql,
        insertSql,
        updateSql,
        deleteSql,
        mergeSql,
        existsSql,
        insertSqlPrefix,
        mergeSqlPrefix,
        updateSqlFactory,
        primaryKeys);
  }

  public static class UpdateSqlFactory {

    private final boolean hasPrimaryKey;
    private final String errorMsg;
    private final String tableName;
    private final String whereClauseIdentifyByPrimaryKeys;
    private final Map<String, String> canonicalNameToDbColumnMap;

    public UpdateSqlFactory(
        boolean hasPrimaryKey,
        String errorMsg,
        String tableName,
        List<String> columns,
        String whereClauseIdentifyByPrimaryKeys) {
      this.hasPrimaryKey = hasPrimaryKey;
      this.errorMsg = errorMsg;
      this.tableName = tableName;
      this.whereClauseIdentifyByPrimaryKeys = whereClauseIdentifyByPrimaryKeys;
      this.canonicalNameToDbColumnMap =
          columns.stream()
              .collect(
                  Collectors.toMap(
                      c -> SormContext.getDefaultCanonicalStringCache().toCanonicalName(c),
                      c -> c));
    }

    public String createUpdateSql(Collection<String> columns) {
      return !hasPrimaryKey
          ? errorMsg
          : "update "
              + tableName
              + createUpdateSetClause(columns)
              + whereClauseIdentifyByPrimaryKeys;
    }

    private String createUpdateSetClause(Collection<String> columns) {
      return " set "
          + String.join(
              ", ",
              columns.stream()
                  .map(
                      col ->
                          canonicalNameToDbColumnMap.get(
                                  SormContext.getDefaultCanonicalStringCache().toCanonicalName(col))
                              + "=?")
                  .collect(Collectors.toList()));
    }
  }

  private static String createWhereClauseIdentifyByPrimaryKeys(List<String> primaryKeys) {
    return " where "
        + String.join(
            " and ", primaryKeys.stream().map(pk -> pk + "=?").collect(Collectors.toList()));
  }

  protected String generatePlaceholders(
      DbMetaData databaseMetaData, TableMetaData tableMetaData, List<String> targetColumns) {
    if (databaseMetaData.getDatabaseProductName().toLowerCase().contains("h2")) {
      return String.join(
          ",",
          tableMetaData.getColumnsMetaData().stream()
              .filter(c -> targetColumns.contains(c.getColumnName()))
              .map(c -> c.getTypeName().equalsIgnoreCase("json") ? "? format json" : "?")
              .toArray(String[]::new));
    } else {
      return String.join(
          ",", Stream.generate(() -> "?").limit(targetColumns.size()).collect(Collectors.toList()));
    }
  }
}
