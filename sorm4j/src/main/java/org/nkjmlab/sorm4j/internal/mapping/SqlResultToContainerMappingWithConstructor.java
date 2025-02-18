package org.nkjmlab.sorm4j.internal.mapping;

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
import java.util.stream.Collectors;

import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl.ColumnsAndTypes;
import org.nkjmlab.sorm4j.internal.util.JdbcTypeUtils;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;

final class SqlResultToContainerMappingWithConstructor<S> extends SqlResultToContainerMapping<S> {

  private final Map<String, ConstructorParameter> constructorParametersMap = new HashMap<>();
  private final int constructorParametersLength;

  private final Map<List<String>, ConstructorParameter[]> columnAndConstructorParameterMapping =
      new ConcurrentHashMap<>();

  public SqlResultToContainerMappingWithConstructor(
      ColumnToAccessorMapping columnToAccessorMap,
      Constructor<S> constructor,
      String[] parameterNames) {
    super(columnToAccessorMap, constructor);
    String columnAliasPrefix = columnToAccessorMap.getColumnAliasPrefix();
    Parameter[] parameters = constructor.getParameters();
    this.constructorParametersLength = parameters.length;

    for (int i = 0; i < constructorParametersLength; i++) {
      Parameter parameter = parameters[i];
      String canonicalName =
          SormContext.getDefaultCanonicalStringCache().toCanonicalName(parameterNames[i]);
      ConstructorParameter cp = new ConstructorParameter(canonicalName, i, parameter.getType());
      constructorParametersMap.put(canonicalName, cp);
      if (columnAliasPrefix != null && columnAliasPrefix.length() != 0) {
        constructorParametersMap.put(
            SormContext.getDefaultCanonicalStringCache()
                .toCanonicalNameWithTableName(columnAliasPrefix, parameterNames[i]),
            cp);
      }
    }
  }

  /**
   *
   *
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
   *     parameter, the value is null.
   * @return
   */
  private S createContainerObject(
      ColumnValueToJavaObjectConverters columnValueConverter,
      ResultSet resultSet,
      int[] sqlTypes,
      ConstructorParameter[] constructorParameters) {
    try {
      final Object[] params = new Object[constructorParametersLength];

      for (int i = 0; i < constructorParameters.length; i++) {
        ConstructorParameter cp = constructorParameters[i];
        if (cp == null) {
          continue;
        }
        params[cp.getOrder()] =
            columnValueConverter.convertTo(
                resultSet, i + 1, sqlTypes[i], constructorParameters[i].getType());
      }
      return constructor.newInstance(params);
    } catch (IllegalArgumentException
        | SecurityException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      Object[] params = {JdbcTypeUtils.convert(sqlTypes), constructorParameters};
      throw new SormException(
          ParameterizedStringFormatter.NO_LENGTH_LIMIT.format(
              "Constructor with parameters of container class for object-relation mapping is not match with columns. param={}, sqltypes={}",
              params),
          e);
    }
  }

  private ConstructorParameter[] getCorrespondingParameter(String[] columns) {
    return columnAndConstructorParameterMapping.computeIfAbsent(
        Arrays.asList(columns),
        key ->
            Arrays.stream(columns)
                .map(
                    col ->
                        constructorParametersMap.get(
                            SormContext.getDefaultCanonicalStringCache().toCanonicalName(col)))
                .toArray(ConstructorParameter[]::new));
  }

  @Override
  List<S> loadContainerObjectList(
      ColumnValueToJavaObjectConverters columnValueConverter,
      ResultSet resultSet,
      ColumnsAndTypes columnsAndTypes)
      throws SQLException {
    final String[] columns = columnsAndTypes.getColumns();
    final int[] sqlTypes = columnsAndTypes.getColumnTypes();
    final ConstructorParameter[] constructorParameters = getCorrespondingParameter(columns);
    final List<S> ret = new ArrayList<>();
    while (resultSet.next()) {
      ret.add(
          createContainerObject(columnValueConverter, resultSet, sqlTypes, constructorParameters));
    }
    return ret;
  }

  @Override
  S loadContainerObject(
      ColumnValueToJavaObjectConverters columnValueConverter,
      ResultSet resultSet,
      ColumnsAndTypes columnsAndTypes)
      throws SQLException {
    final String[] columns = columnsAndTypes.getColumns();
    final int[] sqlTypes = columnsAndTypes.getColumnTypes();
    final ConstructorParameter[] constructorParameters = getCorrespondingParameter(columns);
    return createContainerObject(columnValueConverter, resultSet, sqlTypes, constructorParameters);
  }

  @Override
  public String toString() {
    List<String> keySet =
        constructorParametersMap.keySet().stream().sorted().collect(Collectors.toList());
    Object[] params = {
      constructor,
      keySet,
      String.join(
          System.lineSeparator(),
          keySet.stream()
              .map(key -> "  " + key + "=>" + constructorParametersMap.get(key))
              .collect(Collectors.toList()))
    };
    return ParameterizedStringFormatter.LENGTH_256.format(
        "constructor=[{}], arguments={}" + System.lineSeparator() + "{}", params);
  }

  static final class ConstructorParameter {
    private final String name;

    /** Order in the constructor parameter */
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
      return "[name=" + name + ", order=" + order + ", type=" + type + "]";
    }
  }
}
