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
import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.annotation.OrmConstructor;
import org.nkjmlab.sorm4j.extension.Accessor;
import org.nkjmlab.sorm4j.extension.DefaultResultSetConverter;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.internal.util.StringUtils;
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

  private final PojoCreator<T> pojoCreator;

  @SuppressWarnings("unchecked")
  public ColumnsMapping(Class<T> objectClass, ResultSetConverter resultSetConverter,
      ColumnToAccessorMap columnToAccessorMap) {
    super(resultSetConverter, objectClass, columnToAccessorMap);

    SetterPojoCreator<T> setterPojoCreator = new SetterPojoCreator<>(Try.getOrThrow(
        () -> objectClass.getDeclaredConstructor(),
        e -> new SormException(
            "Container class for object relation mapping must have the public default constructor (with no arguments).",
            e)));

    List<Constructor<?>> annotataedConstructors = Arrays
        .stream(objectClass.getDeclaredConstructors())
        .filter(c -> c.getAnnotation(OrmConstructor.class) != null).collect(Collectors.toList());

    if (annotataedConstructors.size() > 1) {
      throw new SormException(StringUtils.format(
          "Constructor with parameters annotated by {} should be one or less. ", OrmColumn.class));
    }

    this.pojoCreator = annotataedConstructors.isEmpty() ? setterPojoCreator
        : new ConstructorPojoCreator<>((Constructor<T>) annotataedConstructors.get(0));

  }


  private static abstract class PojoCreator<S> {
    protected final Constructor<S> constructor;

    public PojoCreator(Constructor<S> constructor) {
      this.constructor = constructor;
      constructor.setAccessible(true);
    }

    abstract List<S> loadPojoList(List<String> columns, ResultSet resultSet) throws SQLException;

    abstract S loadPojo(List<String> columns, ResultSet resultSet) throws SQLException;

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
          k -> columns.stream().map(columnName -> {
            Accessor acc = columnToAccessorMap.get(columnName);
            return acc != null ? acc.getSetterParameterType() : null;
          }).collect(Collectors.toList()));
    }


    @Override
    S loadPojo(List<String> columns, ResultSet resultSet) throws SQLException {
      final List<Class<?>> setterParameterTypes = getSetterParameterTypes(columns);
      return createPojo(columns, setterParameterTypes, resultSet);
    }

    @Override
    public List<S> loadPojoList(List<String> columns, ResultSet resultSet) throws SQLException {
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
          final Class<?> setterParameterType = setterParameterTypes.get(i - 1);
          if (setterParameterType == null) {
            continue;
          }
          final String columnName = columns.get(i - 1);
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



  }


  private final class ConstructorPojoCreator<S> extends PojoCreator<S> {

    private final Map<String, Class<?>> parameterTypes = new HashMap<>();
    private final Map<String, Integer> parameterOrders = new HashMap<>();
    private final int parametersLength;

    private final Map<List<String>, Class<?>[]> parameterTypesOrderedByColumnMap =
        new ConcurrentHashMap<>();
    private final Map<List<String>, int[]> parameterOrderedByColumnMap = new ConcurrentHashMap<>();


    public ConstructorPojoCreator(Constructor<S> constructor) {
      super(constructor);

      String colmunAliasPrefix =
          Optional.ofNullable(getObjectClass().getAnnotation(OrmColumnAliasPrefix.class))
              .map(a -> a.value()).orElse("");

      String[] parameterNames = constructor.getAnnotation(OrmConstructor.class).value();
      Parameter[] parameters = constructor.getParameters();
      this.parametersLength = parameters.length;

      for (int i = 0; i < parametersLength; i++) {
        Parameter parameter = parameters[i];
        String name = toCanonical(parameterNames[i]);
        parameterOrders.put(name, i);
        parameterTypes.put(name, parameter.getType());
        if (colmunAliasPrefix != null) {
          parameterOrders.put(toCanonical(colmunAliasPrefix + name), i);
          parameterTypes.put(toCanonical(colmunAliasPrefix + name), parameter.getType());

        }
      }
    }


    private S createPojo(int[] orders, Class<?>[] parameterTypes, ResultSet resultSet) {
      try {
        final Object[] params = new Object[parametersLength];
        for (int i = 1; i <= orders.length; i++) {
          final int order = orders[i - 1];
          if (order == -1) {
            continue;
          }
          params[order] =
              resultSetConverter.getValueBySetterParameterType(resultSet, i, parameterTypes[i - 1]);
        }
        return constructor.newInstance(params);
      } catch (SQLException e) {
        throw Try.rethrow(e);
      } catch (IllegalArgumentException | SecurityException | InstantiationException
          | IllegalAccessException | InvocationTargetException e) {
        throw new SormException(
            "Constructor with parameters of container class for object-relation mapping is not match with columns.",
            e);
      }
    }


    private Class<?>[] getParameterTypes(List<String> columns) {
      return parameterTypesOrderedByColumnMap.computeIfAbsent(columns, key -> columns.stream()
          .map(columnName -> parameterTypes.get(toCanonical(columnName))).toArray(Class<?>[]::new));
    }

    private int[] getParameterOrders(List<String> columns) {
      return parameterOrderedByColumnMap.computeIfAbsent(columns,
          key -> columns.stream().mapToInt(columnName -> {
            Integer o = parameterOrders.get(toCanonical(columnName));
            return o != null ? o.intValue() : -1;
          }).toArray());
    }


    @Override
    List<S> loadPojoList(List<String> columns, ResultSet resultSet) throws SQLException {
      final int[] orders = getParameterOrders(columns);
      final Class<?>[] parameterTypes = getParameterTypes(columns);
      final List<S> ret = new ArrayList<>();
      while (resultSet.next()) {
        ret.add(createPojo(orders, parameterTypes, resultSet));
      }
      return ret;
    }


    @Override
    S loadPojo(List<String> columns, ResultSet resultSet) throws SQLException {
      final int[] orders = getParameterOrders(columns);
      final Class<?>[] parameterTypes = getParameterTypes(columns);
      return createPojo(orders, parameterTypes, resultSet);
    }

  }

  String getFormattedString() {
    return "[" + ColumnsMapping.class.getSimpleName() + "] Columns are mappted to a class"
        + System.lineSeparator() + super.getColumnToAccessorString();
  }

  List<T> loadPojoList(ResultSet resultSet) throws SQLException {
    return pojoCreator.loadPojoList(createColumns(resultSet), resultSet);
  }

  T loadPojo(ResultSet resultSet) throws SQLException {
    return loadPojo(createColumns(resultSet), resultSet);
  }

  T loadPojo(List<String> columns, ResultSet resultSet) throws SQLException {
    return pojoCreator.loadPojo(columns, resultSet);
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

  public List<String> createColumnsForJoin(ResultSet resultSet) throws SQLException {
    final ResultSetMetaData metaData = resultSet.getMetaData();
    final int colNum = metaData.getColumnCount();
    final List<String> columns = new ArrayList<>(colNum);
    for (int i = 1; i <= colNum; i++) {
      final String colLabel = metaData.getColumnLabel(i);
      columns.add(colLabel);
    }
    return columns;
  }


}
