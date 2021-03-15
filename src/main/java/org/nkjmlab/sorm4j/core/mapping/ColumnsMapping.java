package org.nkjmlab.sorm4j.core.mapping;

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
import org.nkjmlab.sorm4j.core.util.Try;
import org.nkjmlab.sorm4j.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.DefaultResultSetConverter;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;

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


  ColumnsMapping(Class<T> objectClass, ResultSetConverter defaultResultSetConverter,
      ColumnFieldMapper columnFieldMapper) {
    super(defaultResultSetConverter, objectClass, columnFieldMapper);
    this.constructor = Try.createSupplierWithThrow(() -> objectClass.getDeclaredConstructor(),
        e -> new SormException(
            "Container class for object relation mapping must have the public default constructor (with no arguments).",
            e))
        .get();
    this.constructor.setAccessible(true);
  }

  static <T> ColumnsMapping<T> createMapping(Class<T> objectClass, ResultSetConverter converter,
      ColumnFieldMapper nameGuesser) {
    return new ColumnsMapping<>(objectClass, converter, nameGuesser);
  }


  String getFormattedString() {
    return "[" + ColumnsMapping.class.getSimpleName() + "] Columns are mappted to a class"
        + System.lineSeparator() + super.getColumnToAccessorString();
  }


  T loadPojo(ResultSet resultSet) throws SQLException {
    List<String> columns = createColumns(resultSet);
    return createPojo(columns, convertToObjects(resultSet, getSetterParameterTypes(columns)));
  }

  final List<T> loadPojoList(ResultSet resultSet) throws SQLException {
    List<String> columns = createColumns(resultSet);
    List<Class<?>> setterParamTypes = getSetterParameterTypes(columns);

    final List<T> ret = new ArrayList<>();
    while (resultSet.next()) {
      ret.add(createPojo(columns, convertToObjects(resultSet, setterParamTypes)));
    }
    return ret;
  }

  private final List<Object> convertToObjects(ResultSet resultSet,
      List<Class<?>> setterParameterTypes) throws SQLException {
    final List<Object> values = new ArrayList<>(setterParameterTypes.size());
    for (int i = 1; i <= setterParameterTypes.size(); i++) {
      final Class<?> type = setterParameterTypes.get(i - 1);
      values.add(resultSetConverter.getValueBySetterParameterType(resultSet, i, type));
    }
    return values;
  }



  private T createPojo(List<String> columns, List<Object> values) {
    final T ret = createNewPojoInstance();
    for (int i = 0; i < columns.size(); i++) {
      final String columnName = columns.get(i);
      final Object value = values.get(i);
      setValue(ret, columnName, value);
    }
    return ret;
  }

  private final T createNewPojoInstance() {
    try {
      return constructor.newInstance();
    } catch (IllegalArgumentException | SecurityException | InstantiationException
        | IllegalAccessException | InvocationTargetException e) {
      throw new SormException(
          "Container class for object relation mapping must have the public default constructor (with no arguments).",
          e);
    }
  }

  private static List<String> createColumns(ResultSet resultSet) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    int colNum = metaData.getColumnCount();
    List<String> columns = new ArrayList<>(colNum);
    for (int i = 1; i <= colNum; i++) {
      columns.add(metaData.getColumnName(i));
    }
    return columns;
  }

  private final Map<List<String>, List<Class<?>>> setterParameterTypesMap =
      new ConcurrentHashMap<>();

  private List<Class<?>> getSetterParameterTypes(List<String> columns) {
    return setterParameterTypesMap.computeIfAbsent(columns,
        k -> columns.stream().map(c -> columnToAccessorMap.get(c).getSetterParameterType())
            .collect(Collectors.toList()));
  }



}
