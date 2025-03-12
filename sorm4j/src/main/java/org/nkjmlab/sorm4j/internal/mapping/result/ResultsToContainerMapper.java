package org.nkjmlab.sorm4j.internal.mapping.result;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.nkjmlab.sorm4j.common.exception.SormException;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl.ColumnsAndTypes;
import org.nkjmlab.sorm4j.internal.context.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.internal.context.impl.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.internal.mapping.ColumnToAccessorMapping;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.mapping.annotation.OrmConstructor;
import org.nkjmlab.sorm4j.mapping.annotation.OrmIgnore;

/**
 * Holds mapping data from a given class and a table. The object reads a query result in {@link
 * ResultSet} via {@link DefaultColumnValueToJavaObjectConverters}.
 *
 * @author nkjm
 * @param <T>
 */
public final class ResultsToContainerMapper<T> {

  private final Class<T> objectClass;
  private final Map<Class<?>, ColumnsAndTypes> metaDataForSelectByPrimaryKey =
      new ConcurrentHashMap<>();
  private final ColumnValueToJavaObjectConverters columnValueConverter;
  private final ColumnToAccessorMapping columnToAccessorMap;
  private final ResultsContainerFactory<T> containerObjectCreator;

  public ResultsToContainerMapper(
      ColumnValueToJavaObjectConverters converter,
      Class<T> objectClass,
      ColumnToAccessorMapping columnToAccessorMap) {
    this.columnValueConverter = converter;
    this.objectClass = objectClass;
    this.columnToAccessorMap = columnToAccessorMap;

    this.containerObjectCreator = createContainerObjectCreator();
  }

  private ResultsContainerFactory<T> createContainerObjectCreator() {
    Constructor<T> ormConstructor = getAnnotatedOrmConstructor(objectClass);
    if (ormConstructor != null) {
      return createAnnotatedOrmConstructorCreator(objectClass, ormConstructor);
    }
    Constructor<T> recordConstructor = getRecordConstructor(objectClass);
    if (recordConstructor != null) {
      return createRecordConstructorCreator(objectClass, recordConstructor);
    }
    Constructor<T> ormCanonicalConstructor = getOrmCanonicalConstructor(objectClass);
    if (ormCanonicalConstructor != null) {
      return createOrmCanonicalConstructorCreator(objectClass, ormCanonicalConstructor);
    }
    return createDefaultConstructorCreator(objectClass);
  }

  private ResultsContainerFactory<T> createDefaultConstructorCreator(Class<T> objectClass) {
    return new ResultsContainerWithSetterFactory<>(
        getColumnToAccessorMap(), getDefaultConstructor(objectClass));
  }

  private ResultsContainerFactory<T> createRecordConstructorCreator(
      Class<T> objectClass, Constructor<T> constructor) {
    String[] parameterNames =
        Arrays.stream(objectClass.getRecordComponents())
            .map(RecordComponent::getName)
            .toArray(String[]::new);
    return new ResultsContainerWithConstructorFactory<>(
        getColumnToAccessorMap(), constructor, parameterNames);
  }

  private ResultsContainerFactory<T> createOrmCanonicalConstructorCreator(
      Class<T> objectClass, Constructor<T> constructor) {
    String[] parameterNames =
        Arrays.stream(objectClass.getDeclaredFields()).map(f -> f.getName()).toArray(String[]::new);
    return new ResultsContainerWithConstructorFactory<>(
        getColumnToAccessorMap(), constructor, parameterNames);
  }

  private static <T> Constructor<T> getRecordConstructor(Class<T> objectClass) {
    if (!objectClass.isRecord()) {
      return null;
    }
    try {
      Class<?>[] paramTypes =
          Arrays.stream(objectClass.getRecordComponents())
              .map(RecordComponent::getType)
              .toArray(Class<?>[]::new);
      Constructor<T> constructor = objectClass.getDeclaredConstructor(paramTypes);
      return constructor.getAnnotation(OrmIgnore.class) == null ? constructor : null;
    } catch (NoSuchMethodException e) {
      return null;
    }
  }

  private Constructor<T> getOrmCanonicalConstructor(Class<T> objectClass) {
    Constructor<T> constructor =
        Try.getOrElseNull(
            () ->
                objectClass.getConstructor(
                    Arrays.stream(objectClass.getDeclaredFields())
                        .filter(
                            f ->
                                !java.lang.reflect.Modifier.isStatic(f.getModifiers())
                                    && !f.getName().startsWith(("this$")))
                        .map(feild -> feild.getType())
                        .toArray(Class[]::new)));
    return constructor != null && constructor.getAnnotation(OrmIgnore.class) == null
        ? constructor
        : null;
  }

  @SuppressWarnings("unchecked")
  private Constructor<T> getAnnotatedOrmConstructor(Class<T> objectClass) {
    Optional<Constructor<?>> constructor =
        Arrays.stream(objectClass.getConstructors())
            .filter(c -> c.getAnnotation(OrmConstructor.class) != null)
            .findFirst();
    return constructor.isEmpty() ? null : (Constructor<T>) constructor.get();
  }

  private ResultsContainerFactory<T> createAnnotatedOrmConstructorCreator(
      Class<T> objectClass, Constructor<T> constructor) {
    String[] _parameters = constructor.getAnnotation(OrmConstructor.class).value();
    return new ResultsContainerWithConstructorFactory<>(
        getColumnToAccessorMap(), constructor, _parameters);
  }

  private Constructor<T> getDefaultConstructor(Class<T> objectClass) {
    Object[] params = {
      objectClass, OrmConstructor.class.getSimpleName(), "Record canonical constructor"
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

    return containerObjectCreator.createContainerList(
        columnValueConverter, resultSet, columnsAndTypes);
  }

  public T mapResultsToContainer(ResultSet resultSet) throws SQLException {

    ColumnsAndTypes columnsAndTypes =
        OrmConnectionImpl.ColumnsAndTypes.createColumnsAndTypes(resultSet);

    return containerObjectCreator.createContainer(columnValueConverter, resultSet, columnsAndTypes);
  }

  public T mapResultsToContainerByPrimaryKey(Class<T> objectClass, ResultSet resultSet)
      throws SQLException {

    ColumnsAndTypes columnsAndTypes =
        metaDataForSelectByPrimaryKey.computeIfAbsent(
            objectClass, key -> createColumnsAndTypes(resultSet));

    return containerObjectCreator.createContainer(columnValueConverter, resultSet, columnsAndTypes);
  }

  private static ColumnsAndTypes createColumnsAndTypes(ResultSet resultSet) {
    try {
      return OrmConnectionImpl.ColumnsAndTypes.createColumnsAndTypes(resultSet);
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  private ColumnToAccessorMapping getColumnToAccessorMap() {
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
