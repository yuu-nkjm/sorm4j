package org.nkjmlab.sorm4j.sql.metadata.jdbc;

import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.Map;

import org.nkjmlab.sorm4j.internal.sql.metadata.jdbc.JdbcDatabaseMetaDataImpl;
import org.nkjmlab.sorm4j.internal.util.CanonicalStringCache;
import org.nkjmlab.sorm4j.util.function.exception.Try;

public interface JdbcDatabaseMetaData {

  String getDatabaseProductName();

  String getDatabaseProductVersion();

  int getDefaultTransactionIsolation();

  String getDriverName();

  String getDriverVersion();

  Map<TableName, List<JdbcForeignKeyMetaData>> getJdbcForeignKeysMetaData();

  Map<TableName, List<JdbcColumnMetaData>> getJdbcColumnsMetaData();

  Map<TableName, Map<String, JdbcIndexMetaData>> getJdbcIndexesMetaData();

  int getJdbcMajorVersion();

  int getJdbcMinorVersion();

  Map<TableName, JdbcTableMetaData> getJdbcTablesMetaData();

  int getMaxConnections();

  String getNumericFunctions();

  Map<TableName, List<String>> getPrimaryKeysMetaData();

  Map<String, String> getSchemasMetaData();

  String getSearchStringEscape();

  String getSqlKeywords();

  String getStringFunctions();

  String getSystemFunctions();

  List<TableName> getTableNames();

  String getTimeDateFunctions();

  String getUrl();

  String getUserName();

  boolean isSupportsBatchUpdates();

  boolean isSupportsStoredProcedures();

  boolean isSupportsTransactions();

  public static JdbcDatabaseMetaData of(DatabaseMetaData metaData) {
    return Try.getOrElseThrow(() -> JdbcDatabaseMetaDataImpl.of(metaData), e -> Try.rethrow(e));
  }

  public static final class TableName implements Comparable<TableName> {

    private final String name;

    private TableName(String name) {
      this.name = CanonicalStringCache.getDefault().toCanonicalName(name);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!(obj instanceof TableName)) return false;
      TableName other = (TableName) obj;
      return name.equals(other.name);
    }

    /**
     * Gets name of this object.
     *
     * @return
     */
    public String getName() {
      return name;
    }

    @Override
    public int hashCode() {
      return name.hashCode();
    }

    /** Uses {@link #getName()} when you want to get name. */
    @Override
    public String toString() {
      return name;
    }

    public static TableName of(String tableName) {
      return new TableName(tableName);
    }

    @Override
    public int compareTo(TableName other) {
      return this.name.compareTo(other.name);
    }
  }
}
