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
import org.nkjmlab.sorm4j.extension.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.impl.DefaultColumnValueToJavaObjectConverters;
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
  private final SormOptions options;
  private final ColumnValueToJavaObjectConverters columnValueConverter;
  private final ColumnToAccessorMapping columnToAccessorMap;

  private final Map<String, int[]> columnTypesMap = new ConcurrentHashMap<>();
  private final SqlResultContainerCreator<T> containerObjectCreator;

  public SqlResultToColumnsMapping(SormOptions options, ColumnValueToJavaObjectConverters converter,
      Class<T> objectClass, ColumnToAccessorMapping columnToAccessorMap) {
    this.options = options;
    this.columnValueConverter = converter;
    this.objectClass = objectClass;
    this.columnToAccessorMap = columnToAccessorMap;

    Constructor<T> ormConstructor = getOrmConstructor(objectClass);
    Constructor<T> ormRecordConstructor = getOrmRecordConstructor(objectClass);
    this.containerObjectCreator = ormRecordConstructor != null
        ? createContainerRecordCreator(objectClass, ormRecordConstructor)
        : (ormConstructor != null ? createOrmConstructorPojoCreator(objectClass, ormConstructor)
            : new SetterBasedSqlResultContainerCreator<>(columnToAccessorMap,
                getDefaultConstructor(objectClass)));

  }

  private SqlResultContainerCreator<T> createContainerRecordCreator(Class<T> objectClass,
      Constructor<T> constructor) {
    String[] parameterNames =
        Arrays.stream(objectClass.getDeclaredFields()).map(f -> f.getName()).toArray(String[]::new);
    return new ConstructorBasedSqlResultCreator<>(getColumnToAccessorMap(), constructor,
        parameterNames);
  }

  private Constructor<T> getOrmRecordConstructor(Class<T> objectClass) {
    OrmRecord a = objectClass.getAnnotation(OrmRecord.class);
    if (a == null) {
      return null;
    }
    return Try.getOrElseThrow(
        () -> objectClass.getConstructor(Arrays.stream(objectClass.getDeclaredFields())
            .map(f -> f.getType()).toArray(Class[]::new)),
        e -> new SormException(
            newString("The given container class [{}] should have the canonical constructor.",
                objectClass),
            e));
  }

  private Constructor<T> getOrmConstructor(Class<T> objectClass) {
    List<Constructor<?>> ormConstructors = Arrays.stream(objectClass.getConstructors())
        .filter(c -> c.getAnnotation(OrmConstructor.class) != null).collect(Collectors.toList());
    if (ormConstructors.isEmpty()) {
      return null;
    } else if (ormConstructors.size() > 1) {
      throw new SormException(
          newString("Constructor with parameters annotated by {} should be one or less. ",
              OrmConstructor.class.getName()));
    } else {
      @SuppressWarnings("unchecked")
      Constructor<T> constructor = (Constructor<T>) ormConstructors.get(0);
      return constructor;
    }
  }

  private SqlResultContainerCreator<T> createOrmConstructorPojoCreator(Class<T> objectClass,
      Constructor<T> constructor) {
    String[] _parameters = constructor.getAnnotation(OrmConstructor.class).value();
    return new ConstructorBasedSqlResultCreator<>(getColumnToAccessorMap(), constructor,
        _parameters);
  }


  private Constructor<T> getDefaultConstructor(Class<T> objectClass) {
    return Try.getOrElseThrow(() -> objectClass.getConstructor(), e -> new SormException(newString(
        "The given container class [{}] should have the public default constructor (with no arguments) or the constructor annotated by [{}].",
        objectClass, OrmConstructor.class.getName()), e));
  }



  public String getFormattedString() {
    return "[" + SqlResultToColumnsMapping.class.getSimpleName()
        + "] Columns are mappted to a class" + System.lineSeparator() + getColumnToAccessorString()
        + System.lineSeparator() + "  with [" + containerObjectCreator + "]";
  }

  public List<T> loadContainerObjectList(ResultSet resultSet) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    String[] columns = createColumnLabels(resultSet, metaData);
    String columnsString = getObjectColumnsString(columns);
    int[] columnTypes = getColumnTypes(resultSet, metaData, columns, columnsString);
    return containerObjectCreator.loadContainerObjectList(columnValueConverter, options, resultSet,
        columns, columnTypes, columnsString);
  }

  public T loadContainerObject(ResultSet resultSet) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    String[] columns = createColumnLabels(resultSet, metaData);
    String columnsString = getObjectColumnsString(columns);
    int[] columnTypes = getColumnTypes(resultSet, metaData, columns, columnsString);
    return containerObjectCreator.loadContainerObject(columnValueConverter, options, resultSet,
        columns, columnTypes, columnsString);
  }

  public T loadContainerObjectByPrimaryKey(Class<T> objectClass, ResultSet resultSet)
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
    return containerObjectCreator.loadContainerObject(columnValueConverter, options, resultSet,
        columns, columnTypes, columnsString);
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

  Class<T> getObjectClass() {
    return objectClass;
  }

  String getColumnToAccessorString() {
    return "[" + objectClass.getName() + "] is mapped to " + columnToAccessorMap.toString();
  }

  ColumnToAccessorMapping getColumnToAccessorMap() {
    return columnToAccessorMap;
  }


}
