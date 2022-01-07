package org.nkjmlab.sorm4j.internal.mapping;

import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.extension.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.internal.util.Try;

final class ConstructorPojoCreator<S> extends PojoCreator<S> {

  private final Map<String, ConstructorParameter> constructorParametersMap = new HashMap<>();
  private final int constructorParametersLength;

  private final Map<String, ConstructorParameter[]> columnAndConstructorParameterMapping =
      new ConcurrentHashMap<>();

  public ConstructorPojoCreator(ColumnToAccessorMap columnToAccessorMap, Constructor<S> constructor,
      String[] parameterNames) {
    super(columnToAccessorMap, constructor);
    String columnAliasPrefix = columnToAccessorMap.getColumnAliasPrefix();
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
  private S createPojo(ColumnValueToJavaObjectConverters columnValueConverter, SormOptions options,
      ResultSet resultSet, int[] sqlTypes, ConstructorParameter[] constructorParameters) {
    try {
      final Object[] params = new Object[constructorParametersLength];

      for (int i = 0; i < constructorParameters.length; i++) {
        ConstructorParameter cp = constructorParameters[i];
        if (cp == null) {
          continue;
        }
        params[cp.getOrder()] = columnValueConverter.convertTo(options, resultSet, i + 1,
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


  private ConstructorParameter[] getCorrespondingParameter(String[] columns,
      String objectColumnsStr) {
    return columnAndConstructorParameterMapping.computeIfAbsent(objectColumnsStr,
        key -> Arrays.stream(columns).map(col -> constructorParametersMap.get(toCanonicalCase(col)))
            .toArray(ConstructorParameter[]::new));
  }

  @Override
  List<S> loadPojoList(ColumnValueToJavaObjectConverters columnValueConverter, SormOptions options,
      ResultSet resultSet, String[] columns, int[] columnTypes, String columnsString)
      throws SQLException {
    final ConstructorParameter[] constructorParameters =
        getCorrespondingParameter(columns, columnsString);
    final List<S> ret = new ArrayList<>();
    while (resultSet.next()) {
      ret.add(
          createPojo(columnValueConverter, options, resultSet, columnTypes, constructorParameters));
    }
    return ret;
  }


  @Override
  S loadPojo(ColumnValueToJavaObjectConverters columnValueConverter, SormOptions options,
      ResultSet resultSet, String[] columns, int[] columnTypes, String columnsString)
      throws SQLException {
    final ConstructorParameter[] constructorParameters =
        getCorrespondingParameter(columns, columnsString);
    return createPojo(columnValueConverter, options, resultSet, columnTypes, constructorParameters);
  }


  @Override
  public String toString() {
    return "ConstructorPojoCreator [constructorParametersMap=" + constructorParametersMap + "]";
  }

  final static class ConstructorParameter {
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

}
