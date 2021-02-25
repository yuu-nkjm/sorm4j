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
import org.nkjmlab.sorm4j.config.SqlToJavaDataConverter;
import org.nkjmlab.sorm4j.util.Try;

public final class ColumnsMapping<T> extends Mapping<T> {
  private final Constructor<T> constructor;


  public ColumnsMapping(SqlToJavaDataConverter sqlToJava, Class<T> objectClass,
      ColumnFieldMapper nameGuesser) {
    super(sqlToJava, objectClass, nameGuesser);
    this.constructor =
        Try.supplyOrThrow(() -> objectClass.getDeclaredConstructor(), OrmException::new).get();
    this.constructor.setAccessible(true);
  }

  public static <T> ColumnsMapping<T> createMapping(SqlToJavaDataConverter converter,
      Class<T> objectClass, ColumnFieldMapper nameGuesser) {
    return new ColumnsMapping<>(converter, objectClass, nameGuesser);
  }

  @Override
  public String toString() {
    return "ColumnsMapping [" + super.toString() + "]";
  }

  public String getFormattedString() {
    return "Columns are mappted to class. " + System.lineSeparator()
        + super.getColumnToAccessorString();
  }

  public T createObject(ResultSet resultSet) {
    List<String> columns = createColumns(resultSet);
    List<Class<?>> setterParamTypes = getSetterParamTypes(columns);
    return createObject(columns, createValues(resultSet, setterParamTypes));
  }

  public List<T> createObjectList(ResultSet resultSet) {
    try {
      List<String> columns = createColumns(resultSet);
      List<Class<?>> setterParamTypes = getSetterParamTypes(columns);

      final List<T> ret = new ArrayList<>();
      while (resultSet.next()) {
        ret.add(createObject(columns, createValues(resultSet, setterParamTypes)));
      }
      return ret;
    } catch (IllegalArgumentException | SecurityException | SQLException e) {
      throw new OrmException(e);
    }
  }


  private final List<Object> createValues(ResultSet resultSet, List<Class<?>> setterParamTypes) {
    try {
      final List<Object> values = new ArrayList<>(setterParamTypes.size());
      for (int i = 1; i <= setterParamTypes.size(); i++) {
        final Class<?> type = setterParamTypes.get(i - 1);
        values.add(sqlToJavaConverter.getValueByClass(resultSet, i, type));
      }
      return values;
    } catch (IllegalArgumentException | SecurityException | SQLException e) {
      throw new OrmException(e);
    }
  }


  private T createObject(List<String> columns, List<Object> values) {
    try {
      final T ret = constructor.newInstance();
      for (int i = 0; i < columns.size(); i++) {
        final String columnName = columns.get(i);
        final Object value = values.get(i);
        setValue(ret, columnName, value);
      }
      return ret;
    } catch (IllegalArgumentException | SecurityException | InstantiationException
        | IllegalAccessException | InvocationTargetException e) {
      throw new OrmException(e);
    }
  }

  private static List<String> createColumns(ResultSet resultSet) {
    try {
      ResultSetMetaData metaData = resultSet.getMetaData();
      int colNum = metaData.getColumnCount();
      List<String> columns = new ArrayList<>(colNum);
      for (int i = 1; i <= colNum; i++) {
        columns.add(metaData.getColumnName(i));
      }
      return columns;
    } catch (IllegalArgumentException | SecurityException | SQLException e) {
      throw new OrmException(e);
    }
  }

  private static final Map<List<String>, List<Class<?>>> setterParamTypesMap =
      new ConcurrentHashMap<>();

  private List<Class<?>> getSetterParamTypes(List<String> columns) {
    return setterParamTypesMap.computeIfAbsent(columns,
        k -> columns.stream().map(c -> columnToAccessorMap.get(c).getSetterParameterType())
            .collect(Collectors.toList()));
  }



}
