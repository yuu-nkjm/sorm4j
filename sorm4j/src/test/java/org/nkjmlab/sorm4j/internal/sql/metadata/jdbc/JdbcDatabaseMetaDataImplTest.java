package org.nkjmlab.sorm4j.internal.sql.metadata.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.sql.metadata.jdbc.JdbcColumnMetaData;
import org.nkjmlab.sorm4j.sql.metadata.jdbc.JdbcDatabaseMetaData;
import org.nkjmlab.sorm4j.sql.metadata.jdbc.JdbcDatabaseMetaData.TableName;
import org.nkjmlab.sorm4j.sql.metadata.jdbc.JdbcForeignKeyMetaData;
import org.nkjmlab.sorm4j.sql.metadata.jdbc.JdbcIndexMetaData;
import org.nkjmlab.sorm4j.sql.metadata.jdbc.JdbcTableMetaData;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class JdbcDatabaseMetaDataImplTest {

  private static Connection connection;
  private static JdbcDatabaseMetaData metaData;

  @BeforeAll
  static void setupDatabase() throws SQLException {
    DataSource ds = SormTestUtils.createNewDatabaseDataSource();
    connection = ds.getConnection();
    Statement stmt = connection.createStatement();

    stmt.execute(
        "CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(255), email VARCHAR(255) UNIQUE)");
    stmt.execute(
        "CREATE TABLE orders (id INT PRIMARY KEY, user_id INT, amount DOUBLE, FOREIGN KEY (user_id) REFERENCES users(id))");
    stmt.execute("CREATE INDEX idx_orders_user_id ON orders(user_id)");
    stmt.close();
  }

  @BeforeEach
  void setUp() throws SQLException {
    DatabaseMetaData dbMetaData = connection.getMetaData();
    metaData = JdbcDatabaseMetaDataImpl.of(dbMetaData);
  }

  @AfterAll
  static void tearDown() throws SQLException {
    connection.close();
  }

  @Test
  void testDatabaseMetadataProperties() {
    assertNotNull(metaData.toString());
    assertNotNull(metaData.getSqlKeywords(), "SQL keywords should not be null");
    assertNotNull(metaData.getNumericFunctions(), "Numeric functions should not be null");
    assertNotNull(metaData.getStringFunctions(), "String functions should not be null");
    assertNotNull(metaData.getSystemFunctions(), "System functions should not be null");
    assertNotNull(metaData.getTimeDateFunctions(), "Time/date functions should not be null");
    assertNotNull(metaData.getDatabaseProductName(), "Database product name should not be null");
    assertNotNull(
        metaData.getDatabaseProductVersion(), "Database product version should not be null");
    assertNotNull(metaData.getDriverName(), "Driver name should not be null");
    assertNotNull(metaData.getDriverVersion(), "Driver version should not be null");
    assertNotNull(metaData.getSearchStringEscape(), "Search string escape should not be null");
    assertNotNull(metaData.getUrl(), "Database URL should not be null");
    assertNotNull(metaData.getUserName(), "User name should not be null");

    assertTrue(metaData.isSupportsTransactions(), "Should support transactions");
    assertTrue(metaData.isSupportsBatchUpdates(), "Should support batch updates");
    assertFalse(metaData.isSupportsStoredProcedures(), "Does not support stored procedures");

    assertTrue(metaData.getJdbcMajorVersion() > 0, "JDBC major version should be greater than 0");
    assertTrue(metaData.getJdbcMinorVersion() >= 0, "JDBC minor version should be non-negative");
    assertTrue(
        metaData.getDefaultTransactionIsolation() >= 0,
        "Default transaction isolation should be non-negative");
    assertTrue(
        metaData.getMaxConnections() >= 0, "Max connections should be equals or greater than 0");
  }

  @Test
  void testGetJdbcColumnsMetaData() {
    Map<TableName, List<JdbcColumnMetaData>> columnsMetaData = metaData.getJdbcColumnsMetaData();
    assertNotNull(columnsMetaData, "Columns metadata should not be null");

    assertTrue(
        columnsMetaData.containsKey(TableName.of("USERS")),
        "USERS table should exist in column metadata");
    assertTrue(
        columnsMetaData.containsKey(TableName.of("ORDERS")),
        "ORDERS table should exist in column metadata");

    List<JdbcColumnMetaData> usersColumns = columnsMetaData.get(TableName.of("USERS"));
    assertNotNull(usersColumns, "Columns for USERS should not be null");
    assertFalse(usersColumns.isEmpty(), "USERS table should have at least one column");

    JdbcColumnMetaData userIdColumn =
        usersColumns.stream()
            .filter(col -> "ID".equalsIgnoreCase(col.getColumnName()))
            .findFirst()
            .orElse(null);
    assertNotNull(userIdColumn, "ID column in USERS table should exist");
    assertEquals("USERS", userIdColumn.getTableName(), "Column should belong to USERS table");
    assertEquals("ID", userIdColumn.getColumnName(), "Column name should be ID");
    assertTrue(userIdColumn.getDataType() > 0, "Data type should be valid");
    assertTrue(userIdColumn.getColumnSize() > 0, "Column size should be greater than 0");
    assertNotNull(userIdColumn.getIsNullable(), "Nullable flag should not be null");
    assertNotNull(userIdColumn.getIsAutoIncremented(), "Auto-increment flag should not be null");

    List<JdbcColumnMetaData> ordersColumns = columnsMetaData.get(TableName.of("ORDERS"));
    assertNotNull(ordersColumns, "Columns for ORDERS should not be null");
    assertFalse(ordersColumns.isEmpty(), "ORDERS table should have at least one column");

    JdbcColumnMetaData orderAmountColumn =
        ordersColumns.stream()
            .filter(col -> "AMOUNT".equalsIgnoreCase(col.getColumnName()))
            .findFirst()
            .orElse(null);
    assertNotNull(orderAmountColumn, "AMOUNT column in ORDERS table should exist");
    assertEquals(
        "ORDERS", orderAmountColumn.getTableName(), "Column should belong to ORDERS table");
    assertEquals("AMOUNT", orderAmountColumn.getColumnName(), "Column name should be AMOUNT");
    assertTrue(orderAmountColumn.getDataType() > 0, "Data type should be valid");
    assertTrue(orderAmountColumn.getColumnSize() > 0, "Column size should be greater than 0");
    assertNotNull(orderAmountColumn.getIsNullable(), "Nullable flag should not be null");
  }

  @Test
  void testMetadataTablesAndKeys() {
    assertNotNull(metaData.getJdbcTablesMetaData(), "Tables metadata should not be null");
    assertNotNull(metaData.getJdbcIndexesMetaData(), "Indexes metadata should not be null");
    assertNotNull(metaData.getTableNames(), "Table names should not be null");

    assertNotNull(metaData.getPrimaryKeysMetaData(), "Primary keys metadata should not be null");
    assertNotNull(
        metaData.getJdbcForeignKeysMetaData(), "Foreign keys metadata should not be null");

    assertNotNull(metaData.getSchemasMetaData(), "Schemas metadata should not be null");
  }

  @Test
  void testGetJdbcTablesMetaData() {
    Map<TableName, JdbcTableMetaData> tables = metaData.getJdbcTablesMetaData();

    assertNotNull(tables, "JdbcTablesMetaData should not be null");

    assertEquals(2, tables.size(), "Expected exactly 2 tables in metadata");
    assertTrue(tables.containsKey(TableName.of("USERS")), "USERS table should exist in metadata");
    assertTrue(tables.containsKey(TableName.of("ORDERS")), "ORDERS table should exist in metadata");

    JdbcTableMetaData usersMetaData = tables.get(TableName.of("USERS"));
    assertNotNull(usersMetaData, "USERS metadata should not be null");

    JdbcTableMetaData ordersMetaData = tables.get(TableName.of("ORDERS"));
    assertNotNull(ordersMetaData, "ORDERS metadata should not be null");

    assertEquals("USERS", usersMetaData.getTableName(), "Table name should be USERS");
    assertNotNull(usersMetaData.getTableType(), "Table type should not be null");
    assertNull(usersMetaData.getRemarks(), "Remarks should not be null");
    assertNotNull(usersMetaData.getTableCat(), "Table catalog should not be null");
    assertNotNull(usersMetaData.getTableSchem(), "Table schema should not be null");
    assertNull(usersMetaData.getTypeCat(), "Type catalog should not be null");
    assertNull(usersMetaData.getTypeSchem(), "Type schema should not be null");
    assertNull(usersMetaData.getTypeName(), "Type name should not be null");
    assertNull(
        usersMetaData.getSelfReferencingColName(), "Self-referencing column should not be null");
    assertNull(usersMetaData.getRefGeneration(), "Ref generation should not be null");

    assertEquals("ORDERS", ordersMetaData.getTableName(), "Table name should be ORDERS");
    assertNotNull(ordersMetaData.getTableType(), "Table type should not be null");
    assertNull(ordersMetaData.getRemarks(), "Remarks should not be null");
    assertNotNull(ordersMetaData.getTableCat(), "Table catalog should not be null");
    assertNotNull(ordersMetaData.getTableSchem(), "Table schema should not be null");
    assertNull(ordersMetaData.getTypeCat(), "Type catalog should not be null");
    assertNull(ordersMetaData.getTypeSchem(), "Type schema should not be null");
    assertNull(ordersMetaData.getTypeName(), "Type name should not be null");
    assertNull(
        ordersMetaData.getSelfReferencingColName(), "Self-referencing column should not be null");
    assertNull(ordersMetaData.getRefGeneration(), "Ref generation should not be null");

    assertTrue(
        usersMetaData.toString().contains("USERS"),
        "USERS metadata toString should contain table name");
    assertTrue(
        ordersMetaData.toString().contains("ORDERS"),
        "ORDERS metadata toString should contain table name");
  }

  @Test
  void testGetJdbcIndexesMetaData() {
    Map<TableName, Map<String, JdbcIndexMetaData>> indexes = metaData.getJdbcIndexesMetaData();

    assertNotNull(indexes, "JdbcIndexesMetaData should not be null");

    assertTrue(
        indexes.containsKey(TableName.of("ORDERS")), "ORDERS table should exist in index metadata");

    Map<String, JdbcIndexMetaData> ordersIndexes = indexes.get(TableName.of("ORDERS"));
    assertNotNull(ordersIndexes, "ORDERS indexes should not be null");
    assertTrue(
        ordersIndexes.containsKey("IDX_ORDERS_USER_ID"), "Index IDX_ORDERS_USER_ID should exist");

    JdbcIndexMetaData indexMetaData = ordersIndexes.get("IDX_ORDERS_USER_ID");
    assertNotNull(indexMetaData, "Index metadata should not be null");

    assertEquals("ORDERS", indexMetaData.getTableName(), "Table name should be ORDERS");
    assertEquals(
        "IDX_ORDERS_USER_ID",
        indexMetaData.getIndexName(),
        "Index name should be IDX_ORDERS_USER_ID");
    assertTrue(
        indexMetaData.isNonUnique(), "Index should be non-unique or unique, check expected value");

    assertNotNull(indexMetaData.getTableCat(), "Table catalog should not be null");
    assertNotNull(indexMetaData.getTableSchem(), "Table schema should not be null");
    assertNotNull(indexMetaData.getIndexQualifier(), "Index qualifier should not be null");
    assertTrue(indexMetaData.getType() > 0, "Index type should be valid");
    assertTrue(indexMetaData.getOrdinalPosition() >= 0, "Ordinal position should be valid");
    assertNotNull(indexMetaData.getColumnName(), "Column name should not be null");
    assertNotNull(indexMetaData.getAscOrDesc(), "ASC/DESC should not be null");
    assertTrue(indexMetaData.getCardinality() >= 0, "Cardinality should be valid");
    assertTrue(indexMetaData.getPages() >= 0, "Pages should be valid");
    assertNull(indexMetaData.getFilterCondition(), "Filter condition is null");
  }

  @Test
  void testGetJdbcForeignKeysMetaData() {
    Map<TableName, List<JdbcForeignKeyMetaData>> foreignKeys =
        metaData.getJdbcForeignKeysMetaData();

    assertNotNull(foreignKeys, "Foreign keys metadata should not be null");

    assertTrue(
        foreignKeys.containsKey(TableName.of("ORDERS")),
        "ORDERS table should exist in foreign key metadata");

    List<JdbcForeignKeyMetaData> fkList = foreignKeys.get(TableName.of("ORDERS"));
    assertNotNull(fkList, "Foreign key list for ORDERS should not be null");
    assertFalse(fkList.isEmpty(), "Foreign key list for ORDERS should not be empty");

    JdbcForeignKeyMetaData fkMetaData = fkList.get(0);
    assertNotNull(fkMetaData, "Foreign key metadata should not be null");

    assertEquals("ORDERS", fkMetaData.getFkTable(), "Foreign key table should be ORDERS");
    assertNotNull(fkMetaData.getFkColumn(), "Foreign key column should not be null");
    assertEquals("USERS", fkMetaData.getPkTable(), "Primary key table should be USERS");
    assertNotNull(fkMetaData.getPkColumn(), "Primary key column should not be null");

    assertTrue(fkMetaData.getUpdateRule() >= 0, "Update rule should be valid");
    assertTrue(fkMetaData.getDeleteRule() >= 0, "Delete rule should be valid");
  }
}
