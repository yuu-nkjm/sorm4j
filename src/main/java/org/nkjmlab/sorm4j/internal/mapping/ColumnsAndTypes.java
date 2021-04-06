package org.nkjmlab.sorm4j.internal.mapping;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class ColumnsAndTypes {

  private final List<String> columns;
  private final List<Integer> columnTypes;

  public ColumnsAndTypes(List<String> columns, List<Integer> columnTypes) {
    this.columns = columns;
    this.columnTypes = columnTypes;
  }

  public List<String> getColumns() {
    return columns;
  }

  public List<Integer> getColumnTypes() {
    return columnTypes;
  }

  static ColumnsAndTypes createColumnsAndTypes(ResultSet resultSet) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    int colNum = metaData.getColumnCount();
    List<String> columns = new ArrayList<>(colNum);
    List<Integer> columnTypes = new ArrayList<>(colNum);
    for (int i = 1; i <= colNum; i++) {
      columns.add(metaData.getColumnName(i));
      columnTypes.add(metaData.getColumnType(i));
    }
    return new ColumnsAndTypes(columns, columnTypes);
  }

}