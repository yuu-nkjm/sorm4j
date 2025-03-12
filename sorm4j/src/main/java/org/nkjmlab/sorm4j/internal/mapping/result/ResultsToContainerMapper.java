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
import org.nkjmlab.sorm4j.internal.mapping.ColumnToAccessorMapping;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.mapping.annotation.OrmConstructor;
import org.nkjmlab.sorm4j.mapping.annotation.OrmIgnore;
import org.nkjmlab.sorm4j.mapping.annotation.OrmRecordCompatibleConstructor;

/**
 * Maps query results from a {@link ResultSet} to container objects of type {@code T}. This class
 * determines the appropriate constructor for creating instances of {@code T} and applies the
 * corresponding mapping strategy.
 *
 * <p>The constructor selection follows the priority order below:
 *
 * <ol>
 *   <li>If a constructor is annotated with {@link OrmConstructor}, it is selected.
 *   <li>If a constructor is annotated with {@link OrmRecordCompatibleConstructor}, it is selected.
 *   <li>If the class is a {@code record}, its canonical constructor is used.
 *   <li>Otherwise, the default constructor is used, and field values are set via setters.
 * </ol>
 *
 * <p>This design ensures compatibility with different class structures, including records,
 * annotated constructors, and traditional JavaBeans-style objects.
 *
 * @param <T> the type of the container class
 * @author nkjm
 */
public final class ResultsToContainerMapper<T> {

  private final Class<T> objectClass;
  private final Map<Class<?>, ColumnsAndTypes> metaDataForSelectByPrimaryKey =
      new ConcurrentHashMap<>();
  private final ColumnValueToJavaObjectConverters columnValueConverter;
  private final ColumnToAccessorMapping columnToAccessorMap;
  private final ResultsContainerFactory<T> resultsContainerFactory;

  public ResultsToContainerMapper(
      ColumnValueToJavaObjectConverters converter,
      Class<T> objectClass,
      ColumnToAccessorMapping columnToAccessorMap) {
    this.columnValueConverter = converter;
    this.objectClass = objectClass;
    this.columnToAccessorMap = columnToAccessorMap;

    this.resultsContainerFactory = createResultsContainerFactory();
  }

  private ResultsContainerFactory<T> createResultsContainerFactory() {
    Constructor<T> ormConstructor = getAnnotatedOrmConstructor(objectClass);
    if (ormConstructor != null) {
      return createAnnotatedOrmConstructorFactory(objectClass, ormConstructor);
    }
    Constructor<T> ormCanonicalConstructor = getOrmRecordCompatibleConstructor(objectClass);
    if (ormCanonicalConstructor != null) {
      return createOrmRecordCompatibleConstructorFactory(objectClass, ormCanonicalConstructor);
    }
    Constructor<T> recordConstructor = getRecordConstructor(objectClass);
    if (recordConstructor != null) {
      return createRecordConstructorFactory(objectClass, recordConstructor);
    }
    return createDefaultConstructorFactory(objectClass);
  }

  private ResultsContainerFactory<T> createDefaultConstructorFactory(Class<T> objectClass) {
    return new ResultsContainerWithSetterFactory<>(
        getColumnToAccessorMap(), getDefaultConstructor(objectClass));
  }

  private ResultsContainerFactory<T> createRecordConstructorFactory(
      Class<T> objectClass, Constructor<T> constructor) {
    String[] parameterNames =
        Arrays.stream(objectClass.getRecordComponents())
            .map(RecordComponent::getName)
            .toArray(String[]::new);
    return new ResultsContainerWithConstructorFactory<>(
        getColumnToAccessorMap(), constructor, parameterNames);
  }

  private ResultsContainerFactory<T> createOrmRecordCompatibleConstructorFactory(
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

  @SuppressWarnings("unchecked")
  private Constructor<T> getOrmRecordCompatibleConstructor(Class<T> objectClass) {
    Optional<Constructor<?>> constructor =
        Arrays.stream(objectClass.getConstructors())
            .filter(c -> c.getAnnotation(OrmRecordCompatibleConstructor.class) != null)
            .findFirst();
    return constructor.isEmpty() ? null : (Constructor<T>) constructor.get();
  }

  @SuppressWarnings("unchecked")
  private Constructor<T> getAnnotatedOrmConstructor(Class<T> objectClass) {
    Optional<Constructor<?>> constructor =
        Arrays.stream(objectClass.getConstructors())
            .filter(c -> c.getAnnotation(OrmConstructor.class) != null)
            .findFirst();
    return constructor.isEmpty() ? null : (Constructor<T>) constructor.get();
  }

  private ResultsContainerFactory<T> createAnnotatedOrmConstructorFactory(
      Class<T> objectClass, Constructor<T> constructor) {
    String[] _parameters = constructor.getAnnotation(OrmConstructor.class).value();
    return new ResultsContainerWithConstructorFactory<>(
        getColumnToAccessorMap(), constructor, _parameters);
  }

  private Constructor<T> getDefaultConstructor(Class<T> objectClass) {
    Object[] params = {
      objectClass,
      OrmConstructor.class.getSimpleName(),
      OrmRecordCompatibleConstructor.class.getSimpleName()
    };

    return Try.getOrElseThrow(
        () -> objectClass.getConstructor(),
        e ->
            new SormException(
                ParameterizedStringFormatter.LENGTH_256.format(
                    "The given container class [{}] should be record class, "
                        + "must be a record class, have a constructor annotated with @{}, have a constructor annotated with @{}, or have a default constructor.",
                    params),
                e));
  }

  public List<T> traverseAndMap(ResultSet resultSet) throws SQLException {
    ColumnsAndTypes columnsAndTypes =
        OrmConnectionImpl.ColumnsAndTypes.createColumnsAndTypes(resultSet);

    return resultsContainerFactory.createContainerList(
        columnValueConverter, resultSet, columnsAndTypes);
  }

  public T mapResultsToContainer(ResultSet resultSet) throws SQLException {

    ColumnsAndTypes columnsAndTypes =
        OrmConnectionImpl.ColumnsAndTypes.createColumnsAndTypes(resultSet);

    return resultsContainerFactory.createContainer(
        columnValueConverter, resultSet, columnsAndTypes);
  }

  public T mapResultsToContainerByPrimaryKey(Class<T> objectClass, ResultSet resultSet)
      throws SQLException {

    ColumnsAndTypes columnsAndTypes =
        metaDataForSelectByPrimaryKey.computeIfAbsent(
            objectClass, key -> createColumnsAndTypes(resultSet));

    return resultsContainerFactory.createContainer(
        columnValueConverter, resultSet, columnsAndTypes);
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
      resultsContainerFactory.getClass().getSimpleName(),
      resultsContainerFactory.toString()
    };
    return ParameterizedStringFormatter.LENGTH_256.format(
        "[{}] instance used as SQL result container will be created by [{}]"
            + System.lineSeparator()
            + "{}",
        params);
  }
}
