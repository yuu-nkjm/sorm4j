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
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;

public final class JdbcDatabaseMetaData {

  private final String databaseProductName;
  private final String databaseProductVersion;
  private final String driverName;
  private final String driverVersion;
  private final String url;
  private final String userName;
  private final String jdbcDriverVersion;
  private final int defaultTransactionIsolation;
  private final int maxConnections;
  private final String searchStringEscape;
  private final Map<String, JdbcTableMetaData> jdbcTablesMetaData;
  private final Map<String, Map<String, JdbcIndexMetaData>> jdbcIndexesMetaData;
  private final List<String> tableNames;

  public JdbcDatabaseMetaData(String databaseProductName, String databaseProductVersion,
      String driverName, String driverVersion, String jdbcDriverVersion,
      int defaultTransactionIsolation, int maxConnections, String url, String userName,
      String searchStringEscape, Map<String, JdbcTableMetaData> tables,
      Map<String, Map<String, JdbcIndexMetaData>> indexes) {
    this.databaseProductName = databaseProductName;
    this.databaseProductVersion = databaseProductVersion;
    this.driverName = driverName;
    this.driverVersion = driverVersion;
    this.jdbcDriverVersion = jdbcDriverVersion;
    this.defaultTransactionIsolation = defaultTransactionIsolation;
    this.maxConnections = maxConnections;
    this.url = url;
    this.userName = userName;
    this.searchStringEscape = searchStringEscape;
    this.jdbcTablesMetaData = tables;
    this.jdbcIndexesMetaData = indexes;
    this.tableNames = tables.keySet().stream().collect(Collectors.toList());
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


  public String getSearchStringEscape() {
    return searchStringEscape;
  }


  public String getUrl() {
    return url;
  }


  public String getUserName() {
    return userName;
  }


  public String getJdbcDriverVersion() {
    return jdbcDriverVersion;
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
    return "DatabaseMetaDataImpl [databaseProductName=" + databaseProductName
        + ", databaseProductVersion=" + databaseProductVersion + ", driverName=" + driverName
        + ", driverVersion=" + driverVersion + ", url=" + url + ", userName=" + userName
        + ", jdbcDriverVersion=" + jdbcDriverVersion + ", defaultTransactionIsolation="
        + defaultTransactionIsolation + ", maxConnections=" + maxConnections
        + ", jdbcTablesMetaData=" + jdbcTablesMetaData + ", jdbcIndexesMetaData="
        + jdbcIndexesMetaData + "]";
  }

  public static JdbcDatabaseMetaData of(DatabaseMetaData metaData) throws SQLException {
    try (ResultSet resultSet =
        metaData.getTables(null, "PUBLIC", null, new String[] {"TABLE", "VIEW"})) {

      Map<String, JdbcTableMetaData> tables = mapColumnsInResultSetToMap(resultSet,
          List.of("TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "TABLE_TYPE", "REMARKS", "TYPE_CAT",
              "TYPE_SCHEM", "TYPE_NAME", "SELF_REFERENCING_COL_NAME", "REF_GENERATION")).stream()
                  .map(e -> new JdbcTableMetaData(e))
                  .collect(Collectors.toMap(m -> m.get("TABLE_NAME"), m -> m, (v1, v2) -> v1));

      Map<String, Map<String, JdbcIndexMetaData>> indexes = new HashMap<>();
      for (Entry<String, JdbcTableMetaData> jdbcTableEntry : tables.entrySet()) {
        String tableName = jdbcTableEntry.getKey();
        try (ResultSet indexInfo = metaData.getIndexInfo(null, null, tableName, false, false)) {
          Map<String, JdbcIndexMetaData> l = mapColumnsInResultSetToMap(indexInfo,
              List.of("TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "INDEX_QUALIFIER", "INDEX_NAME",
                  "TYPE", "ORDINAL_POSITION", "COLUMN_NAME", "ASC_OR_DESC", "CARDINALITY", "PAGES",
                  "FILTER_CONDITION")).stream().map(e -> new JdbcIndexMetaData(e))
                      .collect(Collectors.toMap(m -> m.get("INDEX_NAME"), m -> m, (v1, v2) -> v1));
          indexes.put(tableName, l);
        }
      }


      return new JdbcDatabaseMetaData(metaData.getDatabaseProductName(),
          metaData.getDatabaseProductVersion(), metaData.getDriverName(),
          metaData.getDriverVersion(),
          ParameterizedStringUtils.newString("{}.{}", metaData.getJDBCMajorVersion(),
              metaData.getJDBCMinorVersion()),
          metaData.getDefaultTransactionIsolation(), metaData.getMaxConnections(),
          metaData.getURL(), metaData.getUserName(), metaData.getSearchStringEscape(), tables,
          indexes);
    }
  }



  private static List<Map<String, String>> mapColumnsInResultSetToMap(ResultSet resultSet,
      List<String> columns) throws SQLException {
    List<Map<String, String>> result = new ArrayList<>();

    while (resultSet.next()) {
      Map<String, String> index = columns.stream().collect(Collectors.toMap(col -> col, col -> {
        try {
          String s = resultSet.getString(col);
          return s != null ? s : "NULL";
        } catch (SQLException e) {
          return "NA";
        }
      }, (v1, v2) -> v2, LinkedHashMap::new));
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
}
