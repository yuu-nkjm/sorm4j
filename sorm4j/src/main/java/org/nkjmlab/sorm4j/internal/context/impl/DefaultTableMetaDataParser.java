package org.nkjmlab.sorm4j.internal.context.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.nkjmlab.sorm4j.internal.container.sql.metadata.ColumnMetaData;
import org.nkjmlab.sorm4j.internal.context.TableMetaDataParser;
import org.nkjmlab.sorm4j.internal.context.common.ColumnMetaDataImpl;

public final class DefaultTableMetaDataParser implements TableMetaDataParser {
  /**
   * @see DatabaseMetaData#getColumns(String, String, String, String)
   */
  @Override
  public List<String> getAutoGeneratedColumns(DatabaseMetaData metaData, String tableName)
      throws SQLException {
    try (ResultSet resultSet =
        metaData.getColumns(null, getSchemaPattern(metaData), tableName, "%")) {
      final List<String> columnsList = new ArrayList<>();
      while (resultSet.next()) {
        String columnName = resultSet.getString(4);
        String isAutoIncrement = resultSet.getString(23);
        String isGenerated = resultSet.getString(24);
        if (isAutoIncrement.equals("YES") || isGenerated.equals("YES")) {
          columnsList.add(columnName);
        }
      }
      return columnsList;
    }
  }

  @Override
  public List<ColumnMetaData> getColumnsMetaData(DatabaseMetaData metaData, String _tableName)
      throws SQLException {
    try (ResultSet resultSet =
        metaData.getColumns(null, getSchemaPattern(metaData), _tableName, "%")) {
      final List<ColumnMetaData> columnsList = new ArrayList<>();
      while (resultSet.next()) {
        String columnName = resultSet.getString(4);
        String typeName = resultSet.getString(6);

        columnsList.add(new ColumnMetaDataImpl(columnName, typeName));
      }
      return columnsList;
    }
  }

  @Override
  public List<String> getPrimaryKeys(DatabaseMetaData metaData, String tableName)
      throws SQLException {
    final List<String> primaryKeysList = new ArrayList<>();
    try (ResultSet resultSet =
        metaData.getPrimaryKeys(null, getSchemaPattern(metaData), tableName)) {
      while (resultSet.next()) {
        final String columnName = resultSet.getString(4);
        primaryKeysList.add(columnName);
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
