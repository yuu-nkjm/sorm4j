package org.nkjmlab.sorm4j.internal.mapping;

import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.annotation.OrmConstructor;
import org.nkjmlab.sorm4j.extension.Accessor;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.impl.DefaultResultSetConverter;
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
  public ColumnsMapping(SormOptions options, ResultSetConverter resultSetConverter,
      Class<T> objectClass, ColumnToAccessorMap columnToAccessorMap) {
    super(options, resultSetConverter, objectClass, columnToAccessorMap);


    List<Constructor<?>> annotataedConstructors = Arrays.stream(objectClass.getConstructors())
        .filter(c -> c.getAnnotation(OrmConstructor.class) != null).collect(Collectors.toList());

    if (annotataedConstructors.size() > 1) {
      throw new SormException(
          format("Constructor with parameters annotated by {} should be one or less. ",
              OrmConstructor.class.getName()));
    }
    this.pojoCreator = !annotataedConstructors.isEmpty()
        ? new ConstructorPojoCreator<>((Constructor<T>) annotataedConstructors.get(0),
            columnToAccessorMap.getColumnAliasPrefix())
        : new SetterPojoCreator<>(Try.getOrThrow(() -> objectClass.getConstructor(),
            e -> new SormException(format(
                "The given container class [{}] should have the public default constructor (with no arguments) or the constructor annotated by [{}].",
                objectClass, OrmConstructor.class.getName()), e)));

  }


  private static abstract class PojoCreator<S> {
    protected final Constructor<S> constructor;

    public PojoCreator(Constructor<S> constructor) {
      this.constructor = constructor;
      // constructor.setAccessible(true);
    }

    abstract List<S> loadPojoList(ResultSet resultSet, List<String> columns) throws SQLException;

    abstract S loadPojo(ResultSet resultSet, List<String> columns) throws SQLException;

  }

  private final Map<List<String>, int[]> columnTypesMap = new ConcurrentHashMap<>();

  private int[] getColumnTypes(ResultSet resultSet, List<String> columns) {
    return columnTypesMap.computeIfAbsent(columns, k -> Try.getOrThrow(() -> {
      ResultSetMetaData metaData = resultSet.getMetaData();
      int n = metaData.getColumnCount();
      int[] ret = new int[n];

      for (int i = 1; i <= ret.length; i++) {
        ret[i - 1] = metaData.getColumnType(i);
      }
      return ret;
    }, Try::rethrow));
  }

  private final class SetterPojoCreator<S> extends PojoCreator<S> {
    // 2021-03-26 Effectiveness of this cache is confirmed by JMH.
    // https://github.com/yuu-nkjm/sorm4j/issues/26
    private final Map<List<String>, Class<?>[]> setterTypesMap = new ConcurrentHashMap<>();

    public SetterPojoCreator(Constructor<S> constructor) {
      super(constructor);
    }

    private Class<?>[] getSetterTypes(List<String> columns) {
      return setterTypesMap.computeIfAbsent(columns, k -> columns.stream().map(columnName -> {
        Accessor acc = columnToAccessorMap.get(columnName);
        return acc != null ? acc.getSetterParameterType() : null;
      }).toArray(Class[]::new));
    }


    @Override
    S loadPojo(ResultSet resultSet, List<String> columns) throws SQLException {
      final Class<?>[] setterTypes = getSetterTypes(columns);
      final int[] columnTypes = getColumnTypes(resultSet, columns);
      return createPojo(resultSet, columns, columnTypes, setterTypes);
    }



    @Override
    public List<S> loadPojoList(ResultSet resultSet, List<String> columns) throws SQLException {
      final Class<?>[] setterTypes = getSetterTypes(columns);
      final int[] columnTypes = getColumnTypes(resultSet, columns);

      final List<S> ret = new ArrayList<>();
      while (resultSet.next()) {
        ret.add(createPojo(resultSet, columns, columnTypes, setterTypes));
      }
      return ret;
    }

    private S createPojo(ResultSet resultSet, List<String> columns, int[] sqlTypes,
        Class<?>[] setterTypes) {
      try {
        final S ret = constructor.newInstance();
        for (int i = 1; i <= columns.size(); i++) {
          final Class<?> setterType = setterTypes[i - 1];
          final int sqlType = sqlTypes[i - 1];
          if (setterType == null) {
            continue;
          }
          final String columnName = columns.get(i - 1);
          final Object value =
              resultSetConverter.convertColumnValueTo(options, resultSet, i, sqlType, setterType);
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

  private static class ConstructorParameter {
    private final String name;
    /**
     * Order in the constructor parameter
     */
    private final int order;
    private final Class<?> type;

    public ConstructorParameter(String name, int order, Class<?> type) {
      this.name = name;
      this.order = order;
      this.type = type;
    }

    public int getOrder() {
      return order;
    }

    public Class<?> getType() {
      return type;
    }

    @Override
    public String toString() {
      return "ConstructorParameter [name=" + name + ", order=" + order + ", type=" + type + "]";
    }

  }

  private final class ConstructorPojoCreator<S> extends PojoCreator<S> {


    private final Map<String, ConstructorParameter> constructorParametersMap = new HashMap<>();
    private final int constructorParametersLength;

    private final Map<List<String>, ConstructorParameter[]> columnAndConstructorParameterMapping =
        new ConcurrentHashMap<>();


    public ConstructorPojoCreator(Constructor<S> constructor, String columnAliasPrefix) {
      super(constructor);


      String[] parameterNames = constructor.getAnnotation(OrmConstructor.class).value();
      Parameter[] parameters = constructor.getParameters();
      this.constructorParametersLength = parameters.length;

      for (int i = 0; i < constructorParametersLength; i++) {
        Parameter parameter = parameters[i];
        String canonicalName = toCanonicalCase(parameterNames[i]);
        ConstructorParameter cp = new ConstructorParameter(canonicalName, i, parameter.getType());
        constructorParametersMap.put(canonicalName, cp);
        if (columnAliasPrefix != null && columnAliasPrefix.length() != 0) {
          constructorParametersMap.put(toCanonicalCase(columnAliasPrefix + canonicalName), cp);

        }
      }
    }


    /**
     * <pre>
     * <code>
     * Pojo(int a, String b, int c);
     * select c, d, b, a from pojo;
     * =>
     * constructorParameters =
     *     {c,3,int}     <= c: SQL(1st), CONST(3rd),
     *     null          <= d: SQL(2nd), CONST(NON),
     *     {b,2,String}  <= b: SQL(3rd), CONST(2nd),
     *     {a,1,int}     <= a: SQL(4th), CONST(1st),
     *
     * </code>
     * </pre>
     *
     * @param resultSet
     * @param sqlTypes
     * @param constructorParameters ordered by column. if the column is not mapped to constructor
     *        parameter, the value is null.
     * @return
     */
    private S createPojo(ResultSet resultSet, int[] sqlTypes,
        ConstructorParameter[] constructorParameters) {
      try {
        final Object[] params = new Object[constructorParametersLength];

        for (int i = 0; i < constructorParameters.length; i++) {
          ConstructorParameter cp = constructorParameters[i];
          if (cp == null) {
            continue;
          }
          params[cp.getOrder()] = resultSetConverter.convertColumnValueTo(options, resultSet, i + 1,
              sqlTypes[i], constructorParameters[i].getType());
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


    private ConstructorParameter[] getCorrespondingParameter(List<String> columns) {
      return columnAndConstructorParameterMapping.computeIfAbsent(columns,
          key -> columns.stream().map(col -> constructorParametersMap.get(toCanonicalCase(col)))
              .toArray(ConstructorParameter[]::new));
    }

    @Override
    List<S> loadPojoList(ResultSet resultSet, List<String> columns) throws SQLException {
      final int[] columnTypes = getColumnTypes(resultSet, columns);
      final ConstructorParameter[] constructorParameters = getCorrespondingParameter(columns);
      final List<S> ret = new ArrayList<>();
      while (resultSet.next()) {
        ret.add(createPojo(resultSet, columnTypes, constructorParameters));
      }
      return ret;
    }


    @Override
    S loadPojo(ResultSet resultSet, List<String> columns) throws SQLException {
      final int[] columnTypes = getColumnTypes(resultSet, columns);
      final ConstructorParameter[] constructorParameters = getCorrespondingParameter(columns);
      return createPojo(resultSet, columnTypes, constructorParameters);
    }


    @Override
    public String toString() {
      return "ConstructorPojoCreator [constructorParametersMap=" + constructorParametersMap + "]";
    }

  }

  public String getFormattedString() {
    return "[" + ColumnsMapping.class.getSimpleName() + "] Columns are mappted to a class"
        + System.lineSeparator() + super.getColumnToAccessorString() + System.lineSeparator()
        + "  with [" + pojoCreator + "]";
  }

  public List<T> loadPojoList(ResultSet resultSet) throws SQLException {
    return pojoCreator.loadPojoList(resultSet, createColumnLabels(resultSet));
  }

  public T loadPojo(ResultSet resultSet) throws SQLException {
    return pojoCreator.loadPojo(resultSet, createColumnLabels(resultSet));
  }

  public List<String> createColumnLabels(ResultSet resultSet) throws SQLException {
    final ResultSetMetaData metaData = resultSet.getMetaData();
    final int colNum = metaData.getColumnCount();
    final List<String> columns = new ArrayList<>(colNum);
    for (int i = 1; i <= colNum; i++) {
      columns.add(metaData.getColumnLabel(i));
    }
    return columns;
  }



}
