package org.nkjmlab.sorm4j.internal.mapping;

import static org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils.*;
import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.annotation.OrmConstructor;
import org.nkjmlab.sorm4j.annotation.OrmRecord;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * Holds mapping data from a given class and a table. The object reads a query result in
 * {@link ResultSet} via {@link DefaultColumnValueToJavaObjectConverters}.
 *
 * @author nkjm
 *
 * @param <T>
 */
public final class SqlResultToColumnsMapping<T> {

  private static final ConcurrentMap<Class<?>, String[]> primaryKeyColumnLabels =
      new ConcurrentHashMap<>();

  private final Class<T> objectClass;
  private final ColumnValueToJavaObjectConverters columnValueConverter;
  private final ColumnToAccessorMapping columnToAccessorMap;

  private final Map<String, int[]> columnTypesMap = new ConcurrentHashMap<>();
  private final SqlResultToContainerMapping<T> containerObjectCreator;

  public SqlResultToColumnsMapping(ColumnValueToJavaObjectConverters converter,
      Class<T> objectClass, ColumnToAccessorMapping columnToAccessorMap) {
    this.columnValueConverter = converter;
    this.objectClass = objectClass;
    this.columnToAccessorMap = columnToAccessorMap;

    Constructor<T> ormConstructor = getOrmConstructor(objectClass);
    Constructor<T> ormRecordConstructor = getOrmRecordConstructor(objectClass);
    this.containerObjectCreator = ormRecordConstructor != null
        ? createContainerRecordCreator(objectClass, ormRecordConstructor)
        : (ormConstructor != null ? createOrmConstructorPojoCreator(objectClass, ormConstructor)
            : new SqlResultToContainerMappingWithSetter<>(columnToAccessorMap,
                getDefaultConstructor(objectClass)));

  }

  private SqlResultToContainerMapping<T> createContainerRecordCreator(Class<T> objectClass,
      Constructor<T> constructor) {
    String[] parameterNames =
        Arrays.stream(objectClass.getDeclaredFields()).map(f -> f.getName()).toArray(String[]::new);
    return new SqlResultToContainerMappingWithConstructor<>(getColumnToAccessorMap(), constructor,
        parameterNames);
  }

  private Constructor<T> getOrmRecordConstructor(Class<T> objectClass) {
    OrmRecord a = objectClass.getAnnotation(OrmRecord.class);
    if (a == null) {
      return null;
    }
    return Try.getOrElseThrow(
        () -> objectClass.getConstructor(Arrays.stream(objectClass.getDeclaredFields())
            .filter(f -> !java.lang.reflect.Modifier.isStatic(f.getModifiers()))
            .map(f -> f.getType()).toArray(Class[]::new)),
        e -> new SormException(newString(
            "The given container class [{}] annotated by @{} should have the canonical constructor.",
            objectClass, OrmRecord.class.getSimpleName()), e));
  }

  private Constructor<T> getOrmConstructor(Class<T> objectClass) {
    List<Constructor<?>> ormConstructors = Arrays.stream(objectClass.getConstructors())
        .filter(c -> c.getAnnotation(OrmConstructor.class) != null).collect(Collectors.toList());
    if (ormConstructors.isEmpty()) {
      return null;
    } else if (ormConstructors.size() > 1) {
      throw new SormException(newString(
          "The given container class [{}] should have one or less constructor annotated by @{}.",
          objectClass, OrmConstructor.class.getSimpleName()));
    } else {
      @SuppressWarnings("unchecked")
      Constructor<T> constructor = (Constructor<T>) ormConstructors.get(0);
      return constructor;
    }
  }

  private SqlResultToContainerMapping<T> createOrmConstructorPojoCreator(Class<T> objectClass,
      Constructor<T> constructor) {
    String[] _parameters = constructor.getAnnotation(OrmConstructor.class).value();
    return new SqlResultToContainerMappingWithConstructor<>(getColumnToAccessorMap(), constructor,
        _parameters);
  }


  private Constructor<T> getDefaultConstructor(Class<T> objectClass) {
    return Try.getOrElseThrow(() -> objectClass.getConstructor(), e -> new SormException(newString(
        "The given container class [{}] should have the public default constructor (with no arguments) or the constructor annotated by @{}. Or the container class should be annotated by@{}.",
        objectClass, OrmConstructor.class.getSimpleName(), OrmRecord.class.getSimpleName()), e));
  }



  public List<T> traverseAndMap(ResultSet resultSet) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    String[] columns = createColumnLabels(resultSet, metaData);
    String columnsString = getObjectColumnsString(columns);
    int[] columnTypes = getColumnTypes(resultSet, metaData, columns, columnsString);
    return containerObjectCreator.loadContainerObjectList(columnValueConverter, resultSet, columns,
        columnTypes, columnsString);
  }

  public T loadResultContainerObject(ResultSet resultSet) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    String[] columns = createColumnLabels(resultSet, metaData);
    String columnsString = getObjectColumnsString(columns);
    int[] columnTypes = getColumnTypes(resultSet, metaData, columns, columnsString);
    return containerObjectCreator.loadContainerObject(columnValueConverter, resultSet, columns,
        columnTypes, columnsString);
  }

  public T loadResultContainerObjectByPrimaryKey(Class<T> objectClass, ResultSet resultSet)
      throws SQLException {
    String[] columns = primaryKeyColumnLabels.computeIfAbsent(objectClass, key -> {
      try {
        return createColumnLabels(resultSet, resultSet.getMetaData());
      } catch (SQLException e) {
        throw Try.rethrow(e);
      }
    });
    String columnsString = getObjectColumnsString(columns);
    int[] columnTypes = getColumnTypes(resultSet, null, columns, columnsString);
    return containerObjectCreator.loadContainerObject(columnValueConverter, resultSet, columns,
        columnTypes, columnsString);
  }

  private String getObjectColumnsString(String[] columns) {
    return String.join("-", columns);
  }



  private int[] getColumnTypes(ResultSet resultSet, ResultSetMetaData metaData, String[] columns,
      String columnsStr) {
    return columnTypesMap.computeIfAbsent(columnsStr, k -> {
      try {
        ResultSetMetaData _metaData = metaData == null ? resultSet.getMetaData() : metaData;
        int n = _metaData.getColumnCount();
        int[] ret = new int[n];
        for (int i = 1; i <= ret.length; i++) {
          ret[i - 1] = _metaData.getColumnType(i);
        }
        return ret;
      } catch (SQLException e) {
        throw Try.rethrow(e);
      }
    });
  }


  private String[] createColumnLabels(ResultSet resultSet, ResultSetMetaData metaData)
      throws SQLException {
    final int colNum = metaData.getColumnCount();
    final String[] columns = new String[colNum];
    for (int i = 1; i <= colNum; i++) {
      columns[i - 1] = metaData.getColumnLabel(i);
    }
    return columns;
  }


  ColumnToAccessorMapping getColumnToAccessorMap() {
    return columnToAccessorMap;
  }


  @Override
  public String toString() {
    return ParameterizedStringUtils.newString(
        "[{}] instance used as SQL result container will be created by [{}]"
            + System.lineSeparator() + "{}",
        objectClass.getName(), containerObjectCreator.getClass().getSimpleName(),
        containerObjectCreator.toString());
  }


}