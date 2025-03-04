package org.nkjmlab.sorm4j.internal.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.internal.result.jdbcmetadata.JdbcColumnMetaDataImpl;

class JdbcColumnMetaDataTest {

  private static Connection connection;

  @BeforeAll
  static void setUp() throws SQLException {
    connection =
        DriverManager.getConnection("jdbc:h2:mem:testdbJdbcColumnMetaDataTest;DB_CLOSE_DELAY=-1");
    try (Statement stmt = connection.createStatement()) {
      stmt.execute(
          "CREATE TABLE test_table ("
              + "id INT PRIMARY KEY AUTO_INCREMENT,"
              + "name VARCHAR(255) NOT NULL,"
              + "age INT NULL,"
              + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
              + ")");
    }
  }

  @AfterAll
  static void tearDown() throws SQLException {
    if (connection != null) {
      connection.close();
    }
  }

  @Test
  void testJdbcColumnMetaDataImpl() throws SQLException {
    DatabaseMetaData metaData = connection.getMetaData();
    try (ResultSet columns = metaData.getColumns(null, null, "TEST_TABLE", null)) {
      while (columns.next()) {
        JdbcColumnMetaDataImpl columnMetaData =
            new JdbcColumnMetaDataImpl(
                columns.getString("TABLE_CAT"),
                columns.getString("TABLE_SCHEM"),
                columns.getString("TABLE_NAME"),
                columns.getString("COLUMN_NAME"),
                columns.getInt("DATA_TYPE"),
                columns.getString("TYPE_NAME"),
                columns.getInt("COLUMN_SIZE"),
                columns.getInt("NUM_PREC_RADIX"),
                columns.getInt("DECIMAL_DIGITS"),
                columns.getInt("ORDINAL_POSITION"),
                columns.getInt("NULLABLE"),
                columns.getInt("CHAR_OCTET_LENGTH"),
                columns.getString("IS_NULLABLE"),
                columns.getString("COLUMN_DEF"),
                columns.getString("REMARKS"),
                columns.getString("IS_AUTOINCREMENT"),
                columns.getString("IS_GENERATEDCOLUMN"));

        assertEquals(columns.getString("TABLE_CAT"), columnMetaData.getTableCatalog());
        assertEquals(columns.getString("TABLE_SCHEM"), columnMetaData.getTableSchema());
        assertEquals(columns.getString("TABLE_NAME"), columnMetaData.getTableName());
        assertEquals(columns.getString("COLUMN_NAME"), columnMetaData.getColumnName());
        assertEquals(columns.getInt("DATA_TYPE"), columnMetaData.getDataType());
        assertEquals(columns.getString("TYPE_NAME"), columnMetaData.getTypeName());
        assertEquals(columns.getInt("COLUMN_SIZE"), columnMetaData.getColumnSize());
        assertEquals(columns.getInt("NUM_PREC_RADIX"), columnMetaData.getNumPrecRadix());
        assertEquals(columns.getInt("DECIMAL_DIGITS"), columnMetaData.getDecimalDigits());
        assertEquals(columns.getInt("ORDINAL_POSITION"), columnMetaData.getOrdinalPosition());
        assertEquals(columns.getInt("NULLABLE"), columnMetaData.getNullableFlag());
        assertEquals(columns.getInt("CHAR_OCTET_LENGTH"), columnMetaData.getCharOctetLength());
        assertEquals(columns.getString("IS_NULLABLE"), columnMetaData.getIsNullable());
        assertEquals(columns.getString("COLUMN_DEF"), columnMetaData.getColumnDefault());
        assertEquals(columns.getString("REMARKS"), columnMetaData.getRemarks());
        assertEquals(columns.getString("IS_AUTOINCREMENT"), columnMetaData.getIsAutoIncremented());
        assertEquals(columns.getString("IS_GENERATEDCOLUMN"), columnMetaData.getIsGenerated());
      }
    }
  }
}
