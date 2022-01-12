package org.nkjmlab.sorm4j.internal.mapping;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.nkjmlab.sorm4j.mapping.ColumnToAccessorMapping;
import org.nkjmlab.sorm4j.mapping.ColumnValueToJavaObjectConverters;


abstract class SqlResultContainerCreator<T> {
  protected final Constructor<T> constructor;
  protected final ColumnToAccessorMapping columnToAccessorMap;

  public SqlResultContainerCreator(ColumnToAccessorMapping columnToAccessorMap,
      Constructor<T> constructor) {
    this.columnToAccessorMap = columnToAccessorMap;
    this.constructor = constructor;
  }

  abstract List<T> loadContainerObjectList(ColumnValueToJavaObjectConverters columnValueConverter,
      ResultSet resultSet, String[] columns, int[] columnTypes,
      String columnsString) throws SQLException;

  abstract T loadContainerObject(ColumnValueToJavaObjectConverters columnValueConverter,
      ResultSet resultSet, String[] columns, int[] columnTypes,
      String columnsString) throws SQLException;


}
