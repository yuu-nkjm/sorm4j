package org.nkjmlab.sorm4j.internal.mapping;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.extension.Accessor;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.internal.util.Try;

final class SetterPojoCreator<T> extends PojoCreator<T> {
  // 2021-03-26 Effectiveness of this cache is confirmed by JMH.
  // https://github.com/yuu-nkjm/sorm4j/issues/26
  private final Map<String, Class<?>[]> setterTypesMap = new ConcurrentHashMap<>();

  public SetterPojoCreator(ColumnToAccessorMap columnToAccessorMap, Constructor<T> constructor) {
    super(columnToAccessorMap, constructor);
  }

  private Class<?>[] getSetterTypes(String[] columns, String objectColumnsStr) {
    return setterTypesMap.computeIfAbsent(objectColumnsStr,
        k -> Arrays.stream(columns).map(columnName -> {
          Accessor acc = columnToAccessorMap.get(columnName);
          return acc != null ? acc.getSetterParameterType() : null;
        }).toArray(Class[]::new));
  }


  @Override
  T loadPojo(ResultSetConverter resultSetConverter, SormOptions options, ResultSet resultSet,
      ResultSetMetaData metaData, String[] columns) throws SQLException {
    String objectColumnsStr = getObjectColumnsStr(columns);
    final Class<?>[] setterTypes = getSetterTypes(columns, objectColumnsStr);
    final int[] columnTypes = getColumnTypes(resultSet, metaData, columns, objectColumnsStr);
    return createPojo(resultSetConverter, options, resultSet, columns, columnTypes, setterTypes);
  }



  @Override
  public List<T> loadPojoList(ResultSetConverter resultSetConverter, SormOptions options,
      ResultSet resultSet, ResultSetMetaData metaData, String[] columns) throws SQLException {
    String objectColumnsStr = getObjectColumnsStr(columns);
    final Class<?>[] setterTypes = getSetterTypes(columns, objectColumnsStr);
    final int[] columnTypes = getColumnTypes(resultSet, metaData, columns, objectColumnsStr);

    final List<T> ret = new ArrayList<>();
    while (resultSet.next()) {
      ret.add(
          createPojo(resultSetConverter, options, resultSet, columns, columnTypes, setterTypes));
    }
    return ret;
  }

  private T createPojo(ResultSetConverter resultSetConverter, SormOptions options,
      ResultSet resultSet, String[] columns, int[] sqlTypes, Class<?>[] setterTypes) {
    try {
      final T ret = constructor.newInstance();
      for (int i = 1; i <= columns.length; i++) {
        final String columnName = columns[i - 1];
        if (columnToAccessorMap.get(columnName) == null) {
          continue;
        }
        final Class<?> setterType = setterTypes[i - 1];
        if (setterType == null) {
          continue;
        }
        final int sqlType = sqlTypes[i - 1];
        final Object value =
            resultSetConverter.convertColumnValueTo(options, resultSet, i, sqlType, setterType);
        columnToAccessorMap.setValue(ret, columnName, value);
      }
      return ret;
    } catch (SQLException e) {
      throw Try.rethrow(e);
    } catch (IllegalArgumentException | SecurityException | InstantiationException
        | IllegalAccessException | InvocationTargetException e) {
      throw new SormException(
          "Container class for object relation mapping must have the public default constructor (with no arguments).",
          e);
    }
  }



}