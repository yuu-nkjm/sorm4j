package org.nkjmlab.sorm4j.common;

import java.util.List;

public interface JdbcTableMetaData {

  /**
   * Gets auto generated columns list.
   *
   * @return
   */
  List<String> getAutoGeneratedColumns();

  List<String> getColumns();

  List<String> getColumnsForUpdate();

  List<ColumnMetaData> getColumnsWithMetaData();

  List<String> getNotAutoGeneratedColumns();

  List<String> getNotPrimaryKeys();

  List<String> getPrimaryKeys();

  String getTableName();

  boolean hasAutoGeneratedColumns();

  boolean hasPrimaryKey();

}
