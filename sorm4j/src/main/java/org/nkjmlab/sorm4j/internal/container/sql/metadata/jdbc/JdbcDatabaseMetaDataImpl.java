package org.nkjmlab.sorm4j.internal.container.sql.metadata.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.nkjmlab.sorm4j.container.sql.TableName;
import org.nkjmlab.sorm4j.container.sql.metadata.jdbc.JdbcColumnMetaData;
import org.nkjmlab.sorm4j.container.sql.metadata.jdbc.JdbcDatabaseMetaData;
import org.nkjmlab.sorm4j.container.sql.metadata.jdbc.JdbcForeignKeyMetaData;
import org.nkjmlab.sorm4j.container.sql.metadata.jdbc.JdbcIndexMetaData;
import org.nkjmlab.sorm4j.container.sql.metadata.jdbc.JdbcTableMetaData;

public final class JdbcDatabaseMetaDataImpl implements JdbcDatabaseMetaData {

  private final String databaseProductName;
  private final String databaseProductVersion;
  private final String driverName;
  private final String driverVersion;
  private final String url;
  private final String userName;

  private final int jdbcMajorVersion;
  private final int jdbcMinorVersion;

  private final int defaultTransactionIsolation;
  private final int maxConnections;
  private final String searchStringEscape;

  private final String sqlKeywords;
  private final String numericFunctions;
  private final String stringFunctions;
  private final String systemFunctions;
  private final String timeDateFunctions;

  private final boolean supportsTransactions;
  private final boolean supportsBatchUpdates;
  private final boolean supportsStoredProcedures;

  private final Map<TableName, JdbcTableMetaData> jdbcTablesMetaData;
  private final Map<TableName, Map<String, JdbcIndexMetaData>> jdbcIndexesMetaData;
  private final Map<TableName, List<JdbcColumnMetaData>> columnsMetaData;
  private final List<TableName> tableNames;

  private final Map<String, String> schemasMetaData;
  private final Map<TableName, List<String>> primaryKeysMetaData;
  private final Map<TableName, List<JdbcForeignKeyMetaData>> foreignKeysMetaData;

  public JdbcDatabaseMetaDataImpl(
      String databaseProductName,
      String databaseProductVersion,
      String driverName,
      String driverVersion,
      int jdbcMajorVersion,
      int jdbcMinorVersion,
      int defaultTransactionIsolation,
      int maxConnections,
      String url,
      String userName,
      String searchStringEscape,
      String sqlKeywords,
      String numericFunctions,
      String stringFunctions,
      String systemFunctions,
      String timeDateFunctions,
      boolean supportsTransactions,
      boolean supportsBatchUpdates,
      boolean supportsStoredProcedures,
      Map<String, String> schemasMetaData,
      Map<TableName, List<String>> primaryKeysMetaData,
      Map<TableName, List<JdbcForeignKeyMetaData>> foreignKeysMetaData,
      Map<TableName, JdbcTableMetaData> tablesMetaData,
      Map<TableName, Map<String, JdbcIndexMetaData>> indexesMetaData,
      Map<TableName, List<JdbcColumnMetaData>> columnsMetaData) {
    this.databaseProductName = databaseProductName;
    this.databaseProductVersion = databaseProductVersion;
    this.driverName = driverName;
    this.driverVersion = driverVersion;
    this.jdbcMajorVersion = jdbcMajorVersion;
    this.jdbcMinorVersion = jdbcMinorVersion;
    this.defaultTransactionIsolation = defaultTransactionIsolation;
    this.maxConnections = maxConnections;
    this.url = url;
    this.userName = userName;
    this.searchStringEscape = searchStringEscape;

    this.sqlKeywords = sqlKeywords;
    this.numericFunctions = numericFunctions;
    this.stringFunctions = stringFunctions;
    this.systemFunctions = systemFunctions;
    this.timeDateFunctions = timeDateFunctions;

    this.supportsTransactions = supportsTransactions;
    this.supportsBatchUpdates = supportsBatchUpdates;
    this.supportsStoredProcedures = supportsStoredProcedures;

    this.jdbcTablesMetaData = tablesMetaData;
    this.jdbcIndexesMetaData = indexesMetaData;
    this.columnsMetaData = columnsMetaData;
    this.schemasMetaData = schemasMetaData;
    this.primaryKeysMetaData = primaryKeysMetaData;
    this.foreignKeysMetaData = foreignKeysMetaData;

    this.tableNames = tablesMetaData.keySet().stream().collect(Collectors.toList());
  }

  @Override
  public Map<TableName, List<JdbcColumnMetaData>> getJdbcColumnsMetaData() {
    return columnsMetaData;
  }

  @Override
  public String getSqlKeywords() {
    return sqlKeywords;
  }

  @Override
  public String getNumericFunctions() {
    return numericFunctions;
  }

  @Override
  public String getStringFunctions() {
    return stringFunctions;
  }

  @Override
  public String getSystemFunctions() {
    return systemFunctions;
  }

  @Override
  public String getTimeDateFunctions() {
    return timeDateFunctions;
  }

  @Override
  public boolean isSupportsTransactions() {
    return supportsTransactions;
  }

  @Override
  public boolean isSupportsBatchUpdates() {
    return supportsBatchUpdates;
  }

  @Override
  public boolean isSupportsStoredProcedures() {
    return supportsStoredProcedures;
  }

  @Override
  public Map<String, String> getSchemasMetaData() {
    return schemasMetaData;
  }

  @Override
  public Map<TableName, List<String>> getPrimaryKeysMetaData() {
    return primaryKeysMetaData;
  }

  @Override
  public Map<TableName, List<JdbcForeignKeyMetaData>> getJdbcForeignKeysMetaData() {
    return foreignKeysMetaData;
  }

  @Override
  public String getDatabaseProductName() {
    return databaseProductName;
  }

  @Override
  public String getDatabaseProductVersion() {
    return databaseProductVersion;
  }

  @Override
  public String getDriverName() {
    return driverName;
  }

  @Override
  public String getDriverVersion() {
    return driverVersion;
  }

  @Override
  public int getJdbcMajorVersion() {
    return jdbcMajorVersion;
  }

  @Override
  public int getJdbcMinorVersion() {
    return jdbcMinorVersion;
  }

  @Override
  public String getSearchStringEscape() {
    return searchStringEscape;
  }

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public String getUserName() {
    return userName;
  }

  @Override
  public int getDefaultTransactionIsolation() {
    return defaultTransactionIsolation;
  }

  @Override
  public int getMaxConnections() {
    return maxConnections;
  }

  @Override
  public Map<TableName, JdbcTableMetaData> getJdbcTablesMetaData() {
    return jdbcTablesMetaData;
  }

  @Override
  public Map<TableName, Map<String, JdbcIndexMetaData>> getJdbcIndexesMetaData() {
    return jdbcIndexesMetaData;
  }

  @Override
  public List<TableName> getTableNames() {
    return tableNames;
  }

  @Override
  public String toString() {
    return "JdbcDatabaseMetaData [databaseProductName="
        + databaseProductName
        + ", databaseProductVersion="
        + databaseProductVersion
        + ", driverName="
        + driverName
        + ", driverVersion="
        + driverVersion
        + ", url="
        + url
        + ", userName="
        + userName
        + ", driverVersion="
        + driverVersion
        + ", jdbcMajorVersion="
        + jdbcMajorVersion
        + ", jdbcMinorVersion="
        + jdbcMinorVersion
        + ", defaultTransactionIsolation="
        + defaultTransactionIsolation
        + ", maxConnections="
        + maxConnections
        + ", searchStringEscape="
        + searchStringEscape
        + ", sqlKeywords="
        + sqlKeywords
        + ", numericFunctions="
        + numericFunctions
        + ", stringFunctions="
        + stringFunctions
        + ", systemFunctions="
        + systemFunctions
        + ", timeDateFunctions="
        + timeDateFunctions
        + ", supportsTransactions="
        + supportsTransactions
        + ", supportsBatchUpdates="
        + supportsBatchUpdates
        + ", supportsStoredProcedures="
        + supportsStoredProcedures
        + ", jdbcTablesMetaData="
        + jdbcTablesMetaData
        + ", jdbcIndexesMetaData="
        + jdbcIndexesMetaData
        + ", columnsMetaData="
        + columnsMetaData
        + ", tableNames="
        + tableNames
        + ", schemasMetaData="
        + schemasMetaData
        + ", primaryKeysMetaData="
        + primaryKeysMetaData
        + ", foreignKeysMetaData="
        + foreignKeysMetaData
        + "]";
  }

  public static JdbcDatabaseMetaData of(DatabaseMetaData metaData) throws SQLException {
    try (ResultSet resultSet =
        metaData.getTables(null, "PUBLIC", null, new String[] {"TABLE", "VIEW"})) {

      Map<TableName, JdbcTableMetaData> tables =
          mapColumnsInResultSetToMap(
                  resultSet,
                  List.of(
                      "TABLE_CAT",
                      "TABLE_SCHEM",
                      "TABLE_NAME",
                      "TABLE_TYPE",
                      "REMARKS",
                      "TYPE_CAT",
                      "TYPE_SCHEM",
                      "TYPE_NAME",
                      "SELF_REFERENCING_COL_NAME",
                      "REF_GENERATION"))
              .stream()
              .map(e -> new JdbcTableMetaDataImpl(e))
              .collect(
                  Collectors.toMap(m -> TableName.of(m.getTableName()), m -> m, (v1, v2) -> v1));

      Map<TableName, List<JdbcColumnMetaData>> columns = new HashMap<>();
      Map<TableName, Map<String, JdbcIndexMetaData>> indexes = new HashMap<>();
      for (Entry<TableName, JdbcTableMetaData> jdbcTableEntry : tables.entrySet()) {
        TableName tableName = jdbcTableEntry.getKey();

        columns.put(tableName, getColumnsMetaData(metaData, tableName.getName()));

        try (ResultSet indexInfo =
            metaData.getIndexInfo(null, null, tableName.getName(), false, false)) {
          Map<String, JdbcIndexMetaData> l =
              mapColumnsInResultSetToMap(
                      indexInfo,
                      List.of(
                          "TABLE_CAT",
                          "TABLE_SCHEM",
                          "TABLE_NAME",
                          "NON_UNIQUE",
                          "INDEX_QUALIFIER",
                          "INDEX_NAME",
                          "TYPE",
                          "ORDINAL_POSITION",
                          "COLUMN_NAME",
                          "ASC_OR_DESC",
                          "CARDINALITY",
                          "PAGES",
                          "FILTER_CONDITION"))
                  .stream()
                  .map(e -> new JdbcIndexMetaDataImpl(e))
                  .collect(Collectors.toMap(m -> m.getIndexName(), m -> m, (v1, v2) -> v1));
          indexes.put(tableName, l);
        }
      }

      return new JdbcDatabaseMetaDataImpl(
          metaData.getDatabaseProductName(),
          metaData.getDatabaseProductVersion(),
          metaData.getDriverName(),
          metaData.getDriverVersion(),
          metaData.getJDBCMajorVersion(),
          metaData.getJDBCMinorVersion(),
          metaData.getDefaultTransactionIsolation(),
          metaData.getMaxConnections(),
          metaData.getURL(),
          metaData.getUserName(),
          metaData.getSearchStringEscape(),
          metaData.getSQLKeywords(),
          metaData.getNumericFunctions(),
          metaData.getStringFunctions(),
          metaData.getSystemFunctions(),
          metaData.getTimeDateFunctions(),
          metaData.supportsTransactions(),
          metaData.supportsBatchUpdates(),
          metaData.supportsStoredProcedures(),
          mapSchemas(metaData.getSchemas()),
          mapPrimaryKeys(metaData),
          mapForeignKeys(metaData),
          tables,
          indexes,
          columns);
    }
  }

  private static Map<String, String> mapSchemas(ResultSet schemas) throws SQLException {
    Map<String, String> schemaMap = new HashMap<>();
    while (schemas.next()) {
      String schemaName = schemas.getString("TABLE_SCHEM");
      String catalogName = schemas.getString("TABLE_CATALOG");
      schemaMap.put(schemaName, catalogName);
    }
    return schemaMap;
  }

  private static Map<TableName, List<String>> mapPrimaryKeys(DatabaseMetaData metaData)
      throws SQLException {
    Map<TableName, List<String>> primaryKeys = new HashMap<>();
    ResultSet pkResultSet = metaData.getTables(null, null, "%", new String[] {"TABLE"});

    while (pkResultSet.next()) {
      String tableName = pkResultSet.getString("TABLE_NAME");
      List<String> pkColumns = new ArrayList<>();

      try (ResultSet pkColumnsResultSet = metaData.getPrimaryKeys(null, null, tableName)) {
        while (pkColumnsResultSet.next()) {
          pkColumns.add(pkColumnsResultSet.getString("COLUMN_NAME"));
        }
      }
      primaryKeys.put(TableName.of(tableName), pkColumns);
    }
    return primaryKeys;
  }

  private static Map<TableName, List<JdbcForeignKeyMetaData>> mapForeignKeys(
      DatabaseMetaData metaData) throws SQLException {
    Map<TableName, List<JdbcForeignKeyMetaData>> foreignKeys = new HashMap<>();
    ResultSet tablesResultSet = metaData.getTables(null, null, "%", new String[] {"TABLE"});

    while (tablesResultSet.next()) {
      String tableName = tablesResultSet.getString("TABLE_NAME");
      List<JdbcForeignKeyMetaData> fkList = new ArrayList<>();

      try (ResultSet fkResultSet = metaData.getImportedKeys(null, null, tableName)) {
        while (fkResultSet.next()) {
          fkList.add(
              new JdbcForeignKeyMetaDataImpl(
                  fkResultSet.getString("FKTABLE_NAME"),
                  fkResultSet.getString("FKCOLUMN_NAME"),
                  fkResultSet.getString("PKTABLE_NAME"),
                  fkResultSet.getString("PKCOLUMN_NAME"),
                  fkResultSet.getShort("UPDATE_RULE"),
                  fkResultSet.getShort("DELETE_RULE")));
        }
      }
      foreignKeys.put(TableName.of(tableName), fkList);
    }
    return foreignKeys;
  }

  private static List<Map<String, Object>> mapColumnsInResultSetToMap(
      ResultSet resultSet, List<String> columns) throws SQLException {
    List<Map<String, Object>> result = new ArrayList<>();

    while (resultSet.next()) {
      Map<String, Object> tmp = new LinkedHashMap<>();
      columns.forEach(
          col -> {
            try {
              tmp.put(col, resultSet.getObject(col));
            } catch (SQLException e) {
              tmp.put(col, null);
            }
          });

      result.add(tmp);
    }
    return result;
  }

  private static String getSchemaPattern(DatabaseMetaData metaData) throws SQLException {
    // oracle expects a pattern such as "%" to work
    return "Oracle".equalsIgnoreCase(metaData.getDatabaseProductName()) ? "%" : null;
  }

  public static List<JdbcColumnMetaData> getColumnsMetaData(
      DatabaseMetaData metaData, String _tableName) throws SQLException {
    try (ResultSet resultSet =
        metaData.getColumns(null, getSchemaPattern(metaData), _tableName, "%")) {
      final List<JdbcColumnMetaData> columnsList = new ArrayList<>();
      while (resultSet.next()) {
        String tableCatalog = resultSet.getString(1);
        String tableSchema = resultSet.getString(2);
        String tableName = resultSet.getString(3);
        String columnName = resultSet.getString(4);
        int dataType = resultSet.getInt(5);
        String typeName = resultSet.getString(6);
        int columnSize = resultSet.getInt(7);
        int decimalDigits = resultSet.getInt(9);
        int numPrecRadix = resultSet.getInt(10);
        int nullableFlag = resultSet.getInt(11);
        String remarks = resultSet.getString(12);
        String columnDefault = resultSet.getString(13);
        int charOctetLength = resultSet.getInt(16);
        int ordinalPosition = resultSet.getInt(17);
        String isNullable = resultSet.getString(18);
        String isAutoIncremented = resultSet.getString(23);
        String isGenerated = resultSet.getString(24);

        columnsList.add(
            new JdbcColumnMetaDataImpl(
                tableCatalog,
                tableSchema,
                tableName,
                columnName,
                dataType,
                typeName,
                columnSize,
                numPrecRadix,
                decimalDigits,
                ordinalPosition,
                nullableFlag,
                charOctetLength,
                isNullable,
                columnDefault,
                remarks,
                isAutoIncremented,
                isGenerated));
      }
      return columnsList;
    }
  }
}
