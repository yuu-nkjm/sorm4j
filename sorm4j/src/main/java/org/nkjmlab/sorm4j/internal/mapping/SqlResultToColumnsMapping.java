package org.nkjmlab.sorm4j.internal.mapping;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl.ColumnsAndTypes;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.mapping.annotation.OrmConstructor;
import org.nkjmlab.sorm4j.mapping.annotation.OrmRecord;

/**
 * Holds mapping data from a given class and a table. The object reads a query result in {@link
 * ResultSet} via {@link DefaultColumnValueToJavaObjectConverters}.
 *
 * @author nkjm
 * @param <T>
 */
public final class SqlResultToColumnsMapping<T> {

  private final Class<T> objectClass;
  private final Map<Class<?>, ColumnsAndTypes> metaDataForSelectByPrimaryKey =
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

    Constructor<T> ormRecordConstructor = getOrmRecordConstructor(objectClass);
    if (ormRecordConstructor != null) {
      this.containerObjectCreator = createContainerRecordCreator(objectClass, ormRecordConstructor);
    } else {
      Constructor<T> ormConstructor = getOrmConstructor(objectClass);
      if (ormConstructor != null) {
        this.containerObjectCreator = createOrmConstructorPojoCreator(objectClass, ormConstructor);
      } else {
        this.containerObjectCreator =
            new SqlResultToContainerMappingWithSetter<>(
                columnToAccessorMap, getDefaultConstructor(objectClass));
      }
    }
  }

  private SqlResultToContainerMapping<T> createContainerRecordCreator(
      Class<T> objectClass, Constructor<T> constructor) {
    String[] parameterNames =
        Arrays.stream(objectClass.getDeclaredFields()).map(f -> f.getName()).toArray(String[]::new);
    return new SqlResultToContainerMappingWithConstructor<>(
        getColumnToAccessorMap(), constructor, parameterNames);
  }

  private Constructor<T> getOrmRecordConstructor(Class<T> objectClass) {
    if (!objectClass.isRecord() && objectClass.getAnnotation(OrmRecord.class) == null) {
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

    return containerObjectCreator.loadContainerObjectList(
        columnValueConverter, resultSet, columnsAndTypes);
  }

  public T loadResultContainerObject(ResultSet resultSet) throws SQLException {

    ColumnsAndTypes columnsAndTypes =
        OrmConnectionImpl.ColumnsAndTypes.createColumnsAndTypes(resultSet);

    return containerObjectCreator.loadContainerObject(
        columnValueConverter, resultSet, columnsAndTypes);
  }

  public T loadResultContainerObjectByPrimaryKey(Class<T> objectClass, ResultSet resultSet)
      throws SQLException {

    ColumnsAndTypes columnsAndTypes =
        metaDataForSelectByPrimaryKey.computeIfAbsent(
            objectClass, key -> createColumnsAndTypes(resultSet));

    return containerObjectCreator.loadContainerObject(
        columnValueConverter, resultSet, columnsAndTypes);
  }

  private static ColumnsAndTypes createColumnsAndTypes(ResultSet resultSet) {
    try {
      return OrmConnectionImpl.ColumnsAndTypes.createColumnsAndTypes(resultSet);
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
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
