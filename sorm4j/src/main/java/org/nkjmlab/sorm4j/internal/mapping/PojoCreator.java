package org.nkjmlab.sorm4j.internal.mapping;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.internal.util.Try;

abstract class PojoCreator<S> {
  private final Map<String, int[]> columnTypesMap = new ConcurrentHashMap<>();
  protected final Constructor<S> constructor;
  protected final ColumnToAccessorMap columnToAccessorMap;

  public PojoCreator(ColumnToAccessorMap columnToAccessorMap, Constructor<S> constructor) {
    this.columnToAccessorMap = columnToAccessorMap;
    this.constructor = constructor;
    // constructor.setAccessible(true);
  }

  abstract List<S> loadPojoList(ResultSetConverter resultSetConverter, SormOptions options,
      ResultSet resultSet, ResultSetMetaData metaData, String[] columns) throws SQLException;

  abstract S loadPojo(ResultSetConverter resultSetConverter, SormOptions options,
      ResultSet resultSet, ResultSetMetaData metaData, String[] columns) throws SQLException;


  int[] getColumnTypes(ResultSet resultSet, ResultSetMetaData metaData, String[] columns,
      String columnsStr) {
    return columnTypesMap.computeIfAbsent(columnsStr, k -> Try.getOrElseThrow(() -> {
      int n = metaData.getColumnCount();
      int[] ret = new int[n];

      for (int i = 1; i <= ret.length; i++) {
        ret[i - 1] = metaData.getColumnType(i);
      }
      return ret;
    }, Try::rethrow));
  }

  String getObjectColumnsStr(String[] columns) {
    return String.join("-", columns);
  }


}
