package org.nkjmlab.sorm4j.internal.mapping;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.extension.DefaultResultSetConverter;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * Holds mapping data from a given class and a table. The object reads a query result in
 * {@link ResultSet} via {@link DefaultResultSetConverter}.
 *
 * @author nkjm
 *
 * @param <T>
 */
public final class ColumnsMapping<T> extends Mapping<T> {

  private final Constructor<T> constructor;

  // 2021-03-26 Effectiveness of this cache is confirmed by JMH.
  // https://github.com/yuu-nkjm/sorm4j/issues/26
  private final Map<List<String>, List<Class<?>>> setterParameterTypesMap =
      new ConcurrentHashMap<>();

  public ColumnsMapping(Class<T> objectClass, ResultSetConverter resultSetConverter,
      ColumnToAccessorMap columnToAccessorMap, Constructor<T> constructor) {
    super(resultSetConverter, objectClass, columnToAccessorMap);
    this.constructor = constructor;
  }

  private T createPojo(List<String> columns, List<Class<?>> setterParameterTypes,
      ResultSet resultSet) {
    try {
      final T ret = constructor.newInstance();
      for (int i = 1; i <= columns.size(); i++) {
        final String columnName = columns.get(i - 1);
        final Class<?> setterParameterType = setterParameterTypes.get(i - 1);
        final Object value =
            resultSetConverter.getValueBySetterParameterType(resultSet, i, setterParameterType);
        setValue(ret, columnName, value);
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

  String getFormattedString() {
    return "[" + ColumnsMapping.class.getSimpleName() + "] Columns are mappted to a class"
        + System.lineSeparator() + super.getColumnToAccessorString();
  }

  private List<Class<?>> getSetterParameterTypes(List<String> columns) {
    return setterParameterTypesMap.computeIfAbsent(columns,
        k -> columns.stream()
            .map(columnName -> columnToAccessorMap.get(columnName).getSetterParameterType())
            .collect(Collectors.toList()));
  }

  T loadPojo(ResultSet resultSet) throws SQLException {
    List<String> columns = createColumns(resultSet);
    List<Class<?>> setterParameterTypes = getSetterParameterTypes(columns);
    return createPojo(columns, setterParameterTypes, resultSet);
  }

  List<T> loadPojoList(ResultSet resultSet) throws SQLException {
    List<String> columns = createColumns(resultSet);
    List<Class<?>> setterParameterTypes = getSetterParameterTypes(columns);

    final List<T> ret = new ArrayList<>();
    while (resultSet.next()) {
      ret.add(createPojo(columns, setterParameterTypes, resultSet));
    }
    return ret;
  }

  private static List<String> createColumns(ResultSet resultSet) throws SQLException {
    final ResultSetMetaData metaData = resultSet.getMetaData();
    final int colNum = metaData.getColumnCount();
    final List<String> columns = new ArrayList<>(colNum);
    for (int i = 1; i <= colNum; i++) {
      columns.add(metaData.getColumnName(i));
    }
    return columns;
  }


}
