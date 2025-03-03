package org.nkjmlab.sorm4j.jdbc_metadata;

import java.util.List;
import java.util.Map;

import org.nkjmlab.sorm4j.internal.mapping.TableName;

public interface JdbcDatabaseMetaData {

  String getDatabaseProductName();

  String getDatabaseProductVersion();

  int getDefaultTransactionIsolation();

  String getDriverName();

  String getDriverVersion();

  Map<TableName, List<JdbcForeignKeyMetaData>> getForeignKeysMetaData();

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
}
