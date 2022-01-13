package org.nkjmlab.sorm4j.mapping;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.nkjmlab.sorm4j.result.ColumnName;
import org.nkjmlab.sorm4j.result.ColumnNameWithMetaData;

public final class DefaultTableMetaDataParser implements TableMetaDataParser {
  @Override
  public List<ColumnName> getAutoGeneratedColumns(DatabaseMetaData metaData, String tableName)
      throws SQLException {
    try (ResultSet resultSet =
        metaData.getColumns(null, getSchemaPattern(metaData), tableName, "%")) {
      final List<ColumnName> columnsList = new ArrayList<>();
      while (resultSet.next()) {
        String columnName = resultSet.getString(4);
        String isAutoIncrement = resultSet.getString(23);
        if (isAutoIncrement.equals("YES")) {
          columnsList.add(new ColumnName(columnName));
        }
      }
      return columnsList;
    }
  }

  @Override
  public List<ColumnNameWithMetaData> getColumns(DatabaseMetaData metaData, String tableName)
      throws SQLException {
    try (ResultSet resultSet =
        metaData.getColumns(null, getSchemaPattern(metaData), tableName, "%")) {
      final List<ColumnNameWithMetaData> columnsList = new ArrayList<>();
      while (resultSet.next()) {
        String columnName = resultSet.getString(4);
        int dataType = resultSet.getInt(5);
        String typeName = resultSet.getString(6);
        int ordinalPosition = resultSet.getInt(17);
        String isNullable = resultSet.getString(18);
        String isAutoIncremented = resultSet.getString(23);
        String isGenerated = resultSet.getString(24);

        columnsList.add(new ColumnNameWithMetaData(columnName, dataType, typeName, ordinalPosition,
            isNullable, isAutoIncremented, isGenerated));
      }
      return columnsList;
    }
  }



  @Override
  public List<ColumnName> getPrimaryKeys(DatabaseMetaData metaData, String tableName)
      throws SQLException {
    final List<ColumnName> primaryKeysList = new ArrayList<>();
    try (ResultSet resultSet =
        metaData.getPrimaryKeys(null, getSchemaPattern(metaData), tableName)) {
      while (resultSet.next()) {
        final String columnName = resultSet.getString(4);
        primaryKeysList.add(new ColumnName(columnName));
      }
      return primaryKeysList;
    }
  }

  /**
   * Gets schema pattern for accessing {@link DatabaseMetaData}.
   *
   * @param metaData
   * @return
   * @throws SQLException
   */
  private String getSchemaPattern(DatabaseMetaData metaData) throws SQLException {
    // oracle expects a pattern such as "%" to work
    return "Oracle".equalsIgnoreCase(metaData.getDatabaseProductName()) ? "%" : null;
  }



}
