package org.nkjmlab.sorm4j.mapping;

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
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.config.ColumnFieldMapper;
import org.nkjmlab.sorm4j.util.Try;

public final class ColumnsMapping<T> extends Mapping<T> {
  private final Constructor<T> constructor;


  public ColumnsMapping(Class<T> objectClass, ResultSetConverter resultSetConverter,
      ColumnFieldMapper columnFieldMapper) {
    super(resultSetConverter, objectClass, columnFieldMapper);
    this.constructor = Try.createSupplierWithThrow(() -> objectClass.getDeclaredConstructor(),
        e -> new OrmException(
            "Container class for object relation mapping must have the public default constructor (with no arguments).",
            e))
        .get();
    this.constructor.setAccessible(true);
  }

  public static <T> ColumnsMapping<T> createMapping(Class<T> objectClass,
      ResultSetConverter converter, ColumnFieldMapper nameGuesser) {
    return new ColumnsMapping<>(objectClass, converter, nameGuesser);
  }


  public String getFormattedString() {
    return "[" + ColumnsMapping.class.getSimpleName() + "] Columns are mappted to a class"
        + System.lineSeparator() + super.getColumnToAccessorString();
  }

  public T loadObject(ResultSet resultSet) throws SQLException {
    List<String> columns = createColumns(resultSet);
    List<Class<?>> setterParamTypes = getSetterParamTypes(columns);
    return createObject(columns,
        resultSetConverter.toObjectsByClasses(resultSet, setterParamTypes));
  }

  public final List<T> loadObjectList(ResultSet resultSet) throws SQLException {
    List<String> columns = createColumns(resultSet);
    List<Class<?>> setterParamTypes = getSetterParamTypes(columns);

    final List<T> ret = new ArrayList<>();
    while (resultSet.next()) {
      ret.add(createObject(columns,
          resultSetConverter.toObjectsByClasses(resultSet, setterParamTypes)));
    }
    return ret;
  }



  private T createObject(List<String> columns, List<Object> values) {
    final T ret = createNewInstance();
    for (int i = 0; i < columns.size(); i++) {
      final String columnName = columns.get(i);
      final Object value = values.get(i);
      setValue(ret, columnName, value);
    }
    return ret;
  }

  private final T createNewInstance() {
    try {
      return constructor.newInstance();
    } catch (IllegalArgumentException | SecurityException | InstantiationException
        | IllegalAccessException | InvocationTargetException e) {
      throw new OrmException(
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

  private final Map<List<String>, List<Class<?>>> setterParamTypesMap = new ConcurrentHashMap<>();

  private List<Class<?>> getSetterParamTypes(List<String> columns) {
    return setterParamTypesMap.computeIfAbsent(columns,
        k -> columns.stream().map(c -> columnToAccessorMap.get(c).getSetterParameterType())
            .collect(Collectors.toList()));
  }



}
