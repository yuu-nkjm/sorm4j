package org.nkjmlab.sorm4j.internal.mapping;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.annotation.OrmConstructor;
import org.nkjmlab.sorm4j.annotation.OrmRecord;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl.ColumnsAndTypes;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * Holds mapping data from a given class and a table. The object reads a query result in {@link
 * ResultSet} via {@link DefaultColumnValueToJavaObjectConverters}.
 *
 * @author nkjm
 * @param <T>
 */
public final class SqlResultToColumnsMapping<T> {

  private final Class<T> objectClass;
  private final Map<Class<?>, ColumnsAndTypesAndString> metaDataForSelectByPrimaryKey =
      new ConcurrentHashMap<>();
  private final ColumnValueToJavaObjectConverters columnValueConverter;
  private final ColumnToAccessorMapping columnToAccessorMap;
  private final SqlResultToContainerMapping<T> containerObjectCreator;

  public SqlResultToColumnsMapping(
      ColumnValueToJavaObjectConverters converter,
      Class<T> objectClass,
      ColumnToAccessorMapping columnToAccessorMap) {
    this.columnValueConverter = converter;
    this.objectClass = objectClass;
    this.columnToAccessorMap = columnToAccessorMap;

    Constructor<T> ormConstructor = getOrmConstructor(objectClass);
    Constructor<T> ormRecordConstructor = getOrmRecordConstructor(objectClass);
    this.containerObjectCreator =
        ormRecordConstructor != null
            ? createContainerRecordCreator(objectClass, ormRecordConstructor)
            : (ormConstructor != null
                ? createOrmConstructorPojoCreator(objectClass, ormConstructor)
                : new SqlResultToContainerMappingWithSetter<>(
                    columnToAccessorMap, getDefaultConstructor(objectClass)));
  }

  private SqlResultToContainerMapping<T> createContainerRecordCreator(
      Class<T> objectClass, Constructor<T> constructor) {
    String[] parameterNames =
        Arrays.stream(objectClass.getDeclaredFields()).map(f -> f.getName()).toArray(String[]::new);
    return new SqlResultToContainerMappingWithConstructor<>(
        getColumnToAccessorMap(), constructor, parameterNames);
  }

  private Constructor<T> getOrmRecordConstructor(Class<T> objectClass) {
    OrmRecord a = objectClass.getAnnotation(OrmRecord.class);
    if (a == null) {
      return null;
    }
    Object[] params = {objectClass, OrmRecord.class.getSimpleName()};
    return Try.getOrElseThrow(
        () ->
            objectClass.getConstructor(
                Arrays.stream(objectClass.getDeclaredFields())
                    .filter(
                        f ->
                            !java.lang.reflect.Modifier.isStatic(f.getModifiers())
                                && !f.getName().startsWith(("this$")))
                    .map(f -> f.getType())
                    .toArray(Class[]::new)),
        e ->
            new SormException(
                ParameterizedStringFormatter.LENGTH_256.format(
                    "The given container class [{}] annotated by @{} should have the canonical constructor.",
                    params),
                e));
  }

  private Constructor<T> getOrmConstructor(Class<T> objectClass) {
    List<Constructor<?>> ormConstructors =
        Arrays.stream(objectClass.getConstructors())
            .filter(c -> c.getAnnotation(OrmConstructor.class) != null)
            .collect(Collectors.toList());
    if (ormConstructors.isEmpty()) {
      return null;
    } else if (ormConstructors.size() > 1) {
      Object[] params = {objectClass, OrmConstructor.class.getSimpleName()};
      throw new SormException(
          ParameterizedStringFormatter.LENGTH_256.format(
              "The given container class [{}] should have one or less constructor annotated by @{}.",
              params));
    } else {
      @SuppressWarnings("unchecked")
      Constructor<T> constructor = (Constructor<T>) ormConstructors.get(0);
      return constructor;
    }
  }

  private SqlResultToContainerMapping<T> createOrmConstructorPojoCreator(
      Class<T> objectClass, Constructor<T> constructor) {
    String[] _parameters = constructor.getAnnotation(OrmConstructor.class).value();
    return new SqlResultToContainerMappingWithConstructor<>(
        getColumnToAccessorMap(), constructor, _parameters);
  }

  private Constructor<T> getDefaultConstructor(Class<T> objectClass) {
    Object[] params = {
      objectClass, OrmConstructor.class.getSimpleName(), OrmRecord.class.getSimpleName()
    };
    return Try.getOrElseThrow(
        () -> objectClass.getConstructor(),
        e ->
            new SormException(
                ParameterizedStringFormatter.LENGTH_256.format(
                    "The given container class [{}] should have the public default constructor or the constructor annotated by @{}. Or the container class should be annotated by @{}.",
                    params),
                e));
  }

  public List<T> traverseAndMap(ResultSet resultSet) throws SQLException {
    ColumnsAndTypes columnsAndTypes =
        OrmConnectionImpl.ColumnsAndTypes.createColumnsAndTypes(resultSet);
    String columnsString = getObjectColumnsString(columnsAndTypes.getColumns());

    return containerObjectCreator.loadContainerObjectList(
        columnValueConverter,
        resultSet,
        columnsAndTypes.getColumns(),
        columnsAndTypes.getColumnTypes(),
        columnsString);
  }

  public T loadResultContainerObject(ResultSet resultSet) throws SQLException {

    ColumnsAndTypes columnsAndTypes =
        OrmConnectionImpl.ColumnsAndTypes.createColumnsAndTypes(resultSet);
    String columnsString = getObjectColumnsString(columnsAndTypes.getColumns());

    return containerObjectCreator.loadContainerObject(
        columnValueConverter,
        resultSet,
        columnsAndTypes.getColumns(),
        columnsAndTypes.getColumnTypes(),
        columnsString);
  }

  public T loadResultContainerObjectByPrimaryKey(Class<T> objectClass, ResultSet resultSet)
      throws SQLException {

    ColumnsAndTypesAndString columnsAndTypesAndString =
        metaDataForSelectByPrimaryKey.computeIfAbsent(
            objectClass, key -> ColumnsAndTypesAndString.create(resultSet));

    return containerObjectCreator.loadContainerObject(
        columnValueConverter,
        resultSet,
        columnsAndTypesAndString.columnsAndTypes.getColumns(),
        columnsAndTypesAndString.columnsAndTypes.getColumnTypes(),
        columnsAndTypesAndString.columnsString);
  }

  private static class ColumnsAndTypesAndString {

    final ColumnsAndTypes columnsAndTypes;
    final String columnsString;

    public ColumnsAndTypesAndString(ColumnsAndTypes columnsAndTypes, String columnsString) {
      this.columnsAndTypes = columnsAndTypes;
      this.columnsString = columnsString;
    }

    static ColumnsAndTypesAndString create(ResultSet resultSet) {
      try {
        ColumnsAndTypes columnsAndTypes =
            OrmConnectionImpl.ColumnsAndTypes.createColumnsAndTypes(resultSet);
        String columnsString = getObjectColumnsString(columnsAndTypes.getColumns());
        return new ColumnsAndTypesAndString(columnsAndTypes, columnsString);
      } catch (SQLException e) {
        throw Try.rethrow(e);
      }
    }
  }

  private static String getObjectColumnsString(String[] columns) {
    return String.join("-", columns);
  }

  ColumnToAccessorMapping getColumnToAccessorMap() {
    return columnToAccessorMap;
  }

  @Override
  public String toString() {
    Object[] params = {
      objectClass.getName(),
      containerObjectCreator.getClass().getSimpleName(),
      containerObjectCreator.toString()
    };
    return ParameterizedStringFormatter.LENGTH_256.format(
        "[{}] instance used as SQL result container will be created by [{}]"
            + System.lineSeparator()
            + "{}",
        params);
  }
}
