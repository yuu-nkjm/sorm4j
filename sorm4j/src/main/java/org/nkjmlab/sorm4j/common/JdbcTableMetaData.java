package org.nkjmlab.sorm4j.common;

import java.sql.SQLException;
import java.util.List;

public interface JdbcTableMetaData {

  /**
   * Gets auto generated columns list.
   *
   * @return
   */
  List<String> getAutoGeneratedColumns();

  String[] getAutoGeneratedColumnsArray();

  List<String> getColumns();

  List<String> getColumnsForUpdate();

  /**
   * Retrieves metadata information for all columns in a specified table.
   *
   * <p>This method queries the database metadata to obtain details about each column in the given
   * table. The metadata includes column name, data type, type name, ordinal position, nullability,
   * auto-increment status, and generated column status.
   *
   * @return a list of {@link ColumnMetaData} objects representing the metadata of all columns in
   *     the specified table.
   * @throws SQLException if a database access error occurs while retrieving column metadata.
   */
  List<ColumnMetaData> getColumnsMetaData();

  List<String> getNotAutoGeneratedColumns();

  List<String> getNotPrimaryKeys();

  List<String> getPrimaryKeys();

  String getTableName();

  boolean hasAutoGeneratedColumns();

  boolean hasPrimaryKey();
}
