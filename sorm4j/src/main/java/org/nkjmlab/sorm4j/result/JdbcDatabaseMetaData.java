package org.nkjmlab.sorm4j.result;

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

import org.nkjmlab.sorm4j.common.JdbcColumnMetaData;
import org.nkjmlab.sorm4j.common.JdbcColumnMetaDataImpl;

public final class JdbcDatabaseMetaData {

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

  private final Map<String, JdbcTableMetaData> jdbcTablesMetaData;
  private final Map<String, Map<String, JdbcIndexMetaData>> jdbcIndexesMetaData;
  private final Map<String, List<JdbcColumnMetaData>> columnsMetaData;
  private final List<String> tableNames;

  private final Map<String, String> schemasMetaData;
  private final Map<String, List<String>> primaryKeysMetaData;
  private final Map<String, List<ForeignKeyMetaData>> foreignKeysMetaData;

  public JdbcDatabaseMetaData(
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
      Map<String, List<String>> primaryKeysMetaData,
      Map<String, List<ForeignKeyMetaData>> foreignKeysMetaData,
      Map<String, JdbcTableMetaData> tablesMetaData,
      Map<String, Map<String, JdbcIndexMetaData>> indexesMetaData,
      Map<String, List<JdbcColumnMetaData>> columnsMetaData) {
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

  public Map<String, List<JdbcColumnMetaData>> getColumnsMetaData() {
    return columnsMetaData;
  }

  public String getSqlKeywords() {
    return sqlKeywords;
  }

  public String getNumericFunctions() {
    return numericFunctions;
  }

  public String getStringFunctions() {
    return stringFunctions;
  }

  public String getSystemFunctions() {
    return systemFunctions;
  }

  public String getTimeDateFunctions() {
    return timeDateFunctions;
  }

  public boolean isSupportsTransactions() {
    return supportsTransactions;
  }

  public boolean isSupportsBatchUpdates() {
    return supportsBatchUpdates;
  }

  public boolean isSupportsStoredProcedures() {
    return supportsStoredProcedures;
  }

  public Map<String, String> getSchemasMetaData() {
    return schemasMetaData;
  }

  public Map<String, List<String>> getPrimaryKeysMetaData() {
    return primaryKeysMetaData;
  }

  public Map<String, List<ForeignKeyMetaData>> getForeignKeysMetaData() {
    return foreignKeysMetaData;
  }

  public String getDatabaseProductName() {
    return databaseProductName;
  }

  public String getDatabaseProductVersion() {
    return databaseProductVersion;
  }

  public String getDriverName() {
    return driverName;
  }

  public String getDriverVersion() {
    return driverVersion;
  }

  public int getJdbcMajorVersion() {
    return jdbcMajorVersion;
  }

  public int getJdbcMinorVersion() {
    return jdbcMinorVersion;
  }

  public String getSearchStringEscape() {
    return searchStringEscape;
  }

  public String getUrl() {
    return url;
  }

  public String getUserName() {
    return userName;
  }

  public int getDefaultTransactionIsolation() {
    return defaultTransactionIsolation;
  }

  public int getMaxConnections() {
    return maxConnections;
  }

  public Map<String, JdbcTableMetaData> getJdbcTablesMetaData() {
    return jdbcTablesMetaData;
  }

  public Map<String, Map<String, JdbcIndexMetaData>> getJdbcIndexesMetaData() {
    return jdbcIndexesMetaData;
  }

  public List<String> getTableNames() {
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

      Map<String, JdbcTableMetaData> tables =
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
              .map(e -> new JdbcTableMetaData(e))
              .collect(Collectors.toMap(m -> m.get("TABLE_NAME"), m -> m, (v1, v2) -> v1));

      Map<String, List<JdbcColumnMetaData>> columns = new HashMap<>();
      Map<String, Map<String, JdbcIndexMetaData>> indexes = new HashMap<>();
      for (Entry<String, JdbcTableMetaData> jdbcTableEntry : tables.entrySet()) {
        String tableName = jdbcTableEntry.getKey();

        columns.put(tableName, getColumnsMetaData(metaData, tableName));

        try (ResultSet indexInfo = metaData.getIndexInfo(null, null, tableName, false, false)) {
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
                  .map(e -> new JdbcIndexMetaData(e))
                  .collect(Collectors.toMap(m -> m.get("INDEX_NAME"), m -> m, (v1, v2) -> v1));
          indexes.put(tableName, l);
        }
      }

      return new JdbcDatabaseMetaData(
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

  private static Map<String, List<String>> mapPrimaryKeys(DatabaseMetaData metaData)
      throws SQLException {
    Map<String, List<String>> primaryKeys = new HashMap<>();
    ResultSet pkResultSet = metaData.getTables(null, null, "%", new String[] {"TABLE"});

    while (pkResultSet.next()) {
      String tableName = pkResultSet.getString("TABLE_NAME");
      List<String> pkColumns = new ArrayList<>();

      try (ResultSet pkColumnsResultSet = metaData.getPrimaryKeys(null, null, tableName)) {
        while (pkColumnsResultSet.next()) {
          pkColumns.add(pkColumnsResultSet.getString("COLUMN_NAME"));
        }
      }
      primaryKeys.put(tableName, pkColumns);
    }
    return primaryKeys;
  }

  private static Map<String, List<ForeignKeyMetaData>> mapForeignKeys(DatabaseMetaData metaData)
      throws SQLException {
    Map<String, List<ForeignKeyMetaData>> foreignKeys = new HashMap<>();
    ResultSet tablesResultSet = metaData.getTables(null, null, "%", new String[] {"TABLE"});

    while (tablesResultSet.next()) {
      String tableName = tablesResultSet.getString("TABLE_NAME");
      List<ForeignKeyMetaData> fkList = new ArrayList<>();

      try (ResultSet fkResultSet = metaData.getImportedKeys(null, null, tableName)) {
        while (fkResultSet.next()) {
          fkList.add(
              new ForeignKeyMetaData(
                  fkResultSet.getString("FKTABLE_NAME"),
                  fkResultSet.getString("FKCOLUMN_NAME"),
                  fkResultSet.getString("PKTABLE_NAME"),
                  fkResultSet.getString("PKCOLUMN_NAME"),
                  fkResultSet.getShort("UPDATE_RULE"),
                  fkResultSet.getShort("DELETE_RULE")));
        }
      }
      foreignKeys.put(tableName, fkList);
    }
    return foreignKeys;
  }

  private static List<Map<String, String>> mapColumnsInResultSetToMap(
      ResultSet resultSet, List<String> columns) throws SQLException {
    List<Map<String, String>> result = new ArrayList<>();

    while (resultSet.next()) {
      Map<String, String> index =
          columns.stream()
              .collect(
                  Collectors.toMap(
                      col -> col,
                      col -> {
                        try {
                          String s = resultSet.getString(col);
                          return s != null ? s : "NULL";
                        } catch (SQLException e) {
                          return "NA";
                        }
                      },
                      (v1, v2) -> v2,
                      LinkedHashMap::new));
      result.add(index);
    }
    return result;
  }

  public static class JdbcTableMetaData {

    private Map<String, String> map;

    public JdbcTableMetaData(Map<String, String> map) {
      this.map = map;
    }

    public String get(String key) {
      return map.get(key);
    }

    @Override
    public String toString() {
      return "JdbcTableMetaData [map=" + map + "]";
    }
  }

  public static class ForeignKeyMetaData {
    private final String fkTable;
    private final String fkColumn;
    private final String pkTable;
    private final String pkColumn;
    private final short updateRule;
    private final short deleteRule;

    public ForeignKeyMetaData(
        String fkTable,
        String fkColumn,
        String pkTable,
        String pkColumn,
        short updateRule,
        short deleteRule) {
      this.fkTable = fkTable;
      this.fkColumn = fkColumn;
      this.pkTable = pkTable;
      this.pkColumn = pkColumn;
      this.updateRule = updateRule;
      this.deleteRule = deleteRule;
    }

    public String getFkTable() {
      return fkTable;
    }

    public String getFkColumn() {
      return fkColumn;
    }

    public String getPkTable() {
      return pkTable;
    }

    public String getPkColumn() {
      return pkColumn;
    }

    public short getUpdateRule() {
      return updateRule;
    }

    public short getDeleteRule() {
      return deleteRule;
    }
  }

  public static class JdbcIndexMetaData {
    private Map<String, String> map;

    public JdbcIndexMetaData(Map<String, String> map) {
      this.map = map;
    }

    public String get(String key) {
      return map.get(key);
    }

    @Override
    public String toString() {
      return "JdbcIndexMetaData [map=" + map + "]";
    }
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
