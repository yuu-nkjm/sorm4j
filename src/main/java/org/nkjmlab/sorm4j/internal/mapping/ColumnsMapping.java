package org.nkjmlab.sorm4j.internal.mapping;

import static org.nkjmlab.sorm4j.internal.util.StringUtils.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.annotation.OrmColumn;
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

  private final PojoCreator<T> defaultPojoCreator;
  private final PojoCreator<T> setterPojoCreator;

  @SuppressWarnings("unchecked")
  public ColumnsMapping(Class<T> objectClass, ResultSetConverter resultSetConverter,
      ColumnToAccessorMap columnToAccessorMap) {
    super(resultSetConverter, objectClass, columnToAccessorMap);

    Optional<Constructor<?>> annotataedConstructor =
        Arrays.stream(objectClass.getDeclaredConstructors()).filter(c -> {
          Parameter[] parameters = c.getParameters();
          return parameters.length != 0 && parameters[0].getAnnotation(OrmColumn.class) != null;
        }).findAny();
    this.setterPojoCreator = new SetterPojoCreator<>(Try.getOrThrow(
        () -> objectClass.getDeclaredConstructor(),
        e -> new SormException(
            "Container class for object relation mapping must have the public default constructor (with no arguments).",
            e)));
    this.defaultPojoCreator = annotataedConstructor.isEmpty() ? setterPojoCreator
        : new ConstructorPojoCreator<>((Constructor<T>) annotataedConstructor.get());

  }


  private static abstract class PojoCreator<T> {
    protected final Constructor<T> constructor;

    public PojoCreator(Constructor<T> constructor) {
      this.constructor = constructor;
      constructor.setAccessible(true);
    }


    abstract List<T> loadPojoList(ResultSet resultSet) throws SQLException;

    abstract T loadPojo(ResultSet resultSet) throws SQLException;

  }

  private final class SetterPojoCreator<S> extends PojoCreator<S> {
    // 2021-03-26 Effectiveness of this cache is confirmed by JMH.
    // https://github.com/yuu-nkjm/sorm4j/issues/26
    private final Map<List<String>, List<Class<?>>> setterParameterTypesMap =
        new ConcurrentHashMap<>();

    public SetterPojoCreator(Constructor<S> constructor) {
      super(constructor);
    }

    private List<Class<?>> getSetterParameterTypes(List<String> columns) {
      return setterParameterTypesMap.computeIfAbsent(columns,
          k -> columns.stream()
              .map(columnName -> columnToAccessorMap.get(columnName).getSetterParameterType())
              .collect(Collectors.toList()));
    }

    @Override
    public S loadPojo(ResultSet resultSet) throws SQLException {
      final List<String> columns = createColumns(resultSet);
      final List<Class<?>> setterParameterTypes = getSetterParameterTypes(columns);
      return createPojo(columns, setterParameterTypes, resultSet);
    }

    @Override
    public List<S> loadPojoList(ResultSet resultSet) throws SQLException {
      final List<String> columns = createColumns(resultSet);
      final List<Class<?>> setterParameterTypes = getSetterParameterTypes(columns);

      final List<S> ret = new ArrayList<>();
      while (resultSet.next()) {
        ret.add(createPojo(columns, setterParameterTypes, resultSet));
      }
      return ret;
    }

    private S createPojo(List<String> columns, List<Class<?>> setterParameterTypes,
        ResultSet resultSet) {
      try {
        final S ret = constructor.newInstance();
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

    private List<String> createColumns(ResultSet resultSet) throws SQLException {
      final ResultSetMetaData metaData = resultSet.getMetaData();
      final int colNum = metaData.getColumnCount();
      final List<String> columns = new ArrayList<>(colNum);
      for (int i = 1; i <= colNum; i++) {
        columns.add(metaData.getColumnName(i));
      }
      return columns;
    }

  }

  private final class ConstructorPojoCreator<S> extends PojoCreator<S> {

    private final Map<String, Class<?>> parameterTypes = new HashMap<>();
    private final Map<String, Integer> parameterOrders = new HashMap<>();
    private final int parametersLength;

    private volatile Class<?>[] parameterTypesOrderedByColumn;
    private volatile int[] parameterOrderedByColumn;


    public ConstructorPojoCreator(Constructor<S> constructor) {
      super(constructor);

      Parameter[] parameters = constructor.getParameters();
      this.parametersLength = parameters.length;
      for (int i = 0; i < parametersLength; i++) {
        Parameter parameter = parameters[i];
        String name = toCanonical(parameter.getAnnotation(OrmColumn.class).value());
        parameterOrders.put(name, i);
        parameterTypes.put(name, parameter.getType());
      }
    }


    private S createPojo(int[] orders, Class<?>[] parameterTypes, ResultSet resultSet) {
      final Object[] params = new Object[parametersLength];
      try {
        for (int i = 1; i <= orders.length; i++) {
          final int order = orders[i - 1];

          params[order] =
              resultSetConverter.getValueBySetterParameterType(resultSet, i, parameterTypes[i - 1]);
        }
        final S ret = constructor.newInstance(params);
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

    private List<String> createColumns(ResultSet resultSet) {
      try {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        final int colNum = metaData.getColumnCount();
        final List<String> columns = new ArrayList<>(colNum);
        for (int i = 1; i <= colNum; i++) {
          columns.add(metaData.getColumnName(i));
        }
        return columns;
      } catch (SQLException e) {
        throw Try.rethrow(e);
      }
    }

    private Class<?>[] getParameterTypes(ResultSet resultSet) {
      if (parameterTypesOrderedByColumn != null) {
        return parameterTypesOrderedByColumn;
      }
      final List<String> columns = createColumns(resultSet);
      this.parameterTypesOrderedByColumn = columns.stream()
          .map(columnName -> parameterTypes.get(toCanonical(columnName))).toArray(Class<?>[]::new);
      return parameterTypesOrderedByColumn;
    }

    private int[] getParameterOrders(ResultSet resultSet) {
      if (parameterOrderedByColumn != null) {
        return parameterOrderedByColumn;
      }
      final List<String> columns = createColumns(resultSet);
      this.parameterOrderedByColumn = columns.stream()
          .mapToInt(columnName -> parameterOrders.get(toCanonical(columnName))).toArray();
      return parameterOrderedByColumn;
    }


    @Override
    List<S> loadPojoList(ResultSet resultSet) throws SQLException {
      final int[] orders = getParameterOrders(resultSet);
      final Class<?>[] parameterTypes = getParameterTypes(resultSet);

      final List<S> ret = new ArrayList<>();
      while (resultSet.next()) {
        ret.add(createPojo(orders, parameterTypes, resultSet));
      }
      return ret;
    }


    @Override
    S loadPojo(ResultSet resultSet) throws SQLException {
      final int[] orders = getParameterOrders(resultSet);
      final Class<?>[] parameterTypes = getParameterTypes(resultSet);
      return createPojo(orders, parameterTypes, resultSet);
    }



  }



  String getFormattedString() {
    return "[" + ColumnsMapping.class.getSimpleName() + "] Columns are mappted to a class"
        + System.lineSeparator() + super.getColumnToAccessorString();
  }



  List<T> loadPojoList(ResultSet resultSet) throws SQLException {
    try {
      return defaultPojoCreator.loadPojoList(resultSet);
    } catch (Exception e) {
      if (defaultPojoCreator == setterPojoCreator) {
        throw Try.rethrow(e);
      }
      return setterPojoCreator.loadPojoList(resultSet);
    }
  }


  T loadPojo(ResultSet resultSet) throws SQLException {
    try {
      return defaultPojoCreator.loadPojo(resultSet);
    } catch (Exception e) {
      if (defaultPojoCreator == setterPojoCreator) {
        throw Try.rethrow(e);
      }
      return setterPojoCreator.loadPojo(resultSet);
    }
  }


}
