package org.nkjmlab.sorm4j.internal.mapping;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.nkjmlab.sorm4j.extension.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.extension.SormOptions;

abstract class PojoCreator<T> {
  protected final Constructor<T> constructor;
  protected final ColumnToAccessorMap columnToAccessorMap;

  public PojoCreator(ColumnToAccessorMap columnToAccessorMap, Constructor<T> constructor) {
    this.columnToAccessorMap = columnToAccessorMap;
    this.constructor = constructor;
  }

  abstract List<T> loadPojoList(ColumnValueToJavaObjectConverters columnValueConverter,
      SormOptions options, ResultSet resultSet, String[] columns, int[] columnTypes,
      String columnsString) throws SQLException;

  abstract T loadPojo(ColumnValueToJavaObjectConverters columnValueConverter, SormOptions options,
      ResultSet resultSet, String[] columns, int[] columnTypes, String columnsString)
      throws SQLException;


}
