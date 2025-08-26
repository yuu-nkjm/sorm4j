package org.nkjmlab.sorm4j.internal.util.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Optional;
import org.nkjmlab.sorm4j.mapping.annotation.OrmConstructor;
import org.nkjmlab.sorm4j.mapping.annotation.OrmIgnore;
import org.nkjmlab.sorm4j.mapping.annotation.OrmRecordCompatibleConstructor;
import org.nkjmlab.sorm4j.util.function.exception.Try;

public class ReflectionConstrucorsUtils {

  public static record OrmConstructorDefinition<T>(
      Constructor<T> constructor, String[] parameterNames) {}

  /**
   * The constructor selection follows the priority order below:
   *
   * <ol>
   *   <li>If a constructor is annotated with {@link OrmConstructor}, it is selected.
   *   <li>If a constructor is annotated with {@link OrmRecordCompatibleConstructor}, it is
   *       selected.
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
  @SuppressWarnings("unchecked")
  public static <T> OrmConstructorDefinition<T> createOrmConstructorDefinition(
      Class<T> objectClass) {
    Constructor<T> ormConstructor = getAnnotatedOrmConstructor(objectClass);
    if (ormConstructor != null) {
      return new OrmConstructorDefinition<T>(
          ormConstructor, toOrmConstructorParameterNames(ormConstructor));
    }
    Constructor<T> ormRocordCompatibleConstructor = getOrmRecordCompatibleConstructor(objectClass);
    if (ormRocordCompatibleConstructor != null) {
      return new OrmConstructorDefinition<T>(
          ormRocordCompatibleConstructor,
          toOrmRecordCompatibleConstructorParameterNames(objectClass));
    }
    if (objectClass.isRecord()) {
      Class<? extends Record> rc = (Class<? extends Record>) objectClass;
      Constructor<T> recordConstructor = (Constructor<T>) getRecordConstructor(rc);
      if (recordConstructor != null) {
        return new OrmConstructorDefinition<T>(
            recordConstructor, toRecordConstructorParameterNames(rc));
      }
    }
    return null;
  }

  private static <T> Constructor<T> getAnnotatedOrmConstructor(Class<T> valueType) {
    return getConstructorByAnnotation(valueType, OrmConstructor.class);
  }

  @SuppressWarnings("unchecked")
  private static <T> Constructor<T> getConstructorByAnnotation(
      Class<T> valueType, Class<? extends Annotation> annotationClass) {
    return (Constructor<T>)
        Arrays.stream(valueType.getConstructors())
            .filter(
                c ->
                    c.isAnnotationPresent(annotationClass)
                        && !c.isAnnotationPresent(OrmIgnore.class))
            .findFirst()
            .orElse(null);
  }

  public static <T> Constructor<T> getDefaultConstructor(Class<T> valueType) {
    return Try.getOrElseNull(() -> valueType.getConstructor());
  }

  private static <T> Constructor<T> getOrmRecordCompatibleConstructor(Class<T> valueType) {
    return getConstructorByAnnotation(valueType, OrmRecordCompatibleConstructor.class);
  }

  /**
   * Retrieves the canonical constructor of the given record class, if available.
   *
   * @param valueType the record class to retrieve the canonical constructor from
   * @return an {@code Optional} containing the canonical constructor if found, otherwise an empty
   *     {@code Optional}
   */
  @SuppressWarnings("unchecked")
  public static <T> Optional<Constructor<T>> getRecordCanonicalConstructor(Class<T> valueType) {
    return valueType.isRecord()
        ? Optional.of((Constructor<T>) getRecordConstructor((Class<? extends Record>) valueType))
        : Optional.ofNullable(getOrmRecordCompatibleConstructor(valueType));
  }

  /**
   * Retrieves the canonical constructor of a given record class. The canonical constructor is
   * determined by the record's components. If the constructor is annotated with {@link OrmIgnore},
   * it is ignored and {@code null} is returned.
   *
   * @param valueType the record class whose canonical constructor is to be retrieved
   * @param <T> the type of the record class
   * @return the canonical constructor of the record class, or {@code null} if not found
   */
  private static <T extends Record> Constructor<T> getRecordConstructor(Class<T> valueType) {
    try {
      Class<?>[] paramTypes =
          RefrectionOrmComponentUtils.getRecordComponents(valueType).stream()
              .map(RecordComponent::getType)
              .toArray(Class<?>[]::new);
      Constructor<T> constructor = valueType.getDeclaredConstructor(paramTypes);
      return constructor.isAnnotationPresent(OrmIgnore.class) ? null : constructor;
    } catch (NoSuchMethodException e) {
      return null;
    }
  }

  /**
   * Retrieves the parameter names of the constructor annotated with {@link OrmConstructor}.
   *
   * @param constructor the constructor annotated with {@link OrmConstructor}
   * @return an array of parameter names specified in the {@link OrmConstructor} annotation
   */
  private static String[] toOrmConstructorParameterNames(Constructor<?> constructor) {
    return constructor.getAnnotation(OrmConstructor.class).value();
  }

  /**
   * Retrieves the names of the declared fields of a given class. This is typically used for
   * ORM-compatible constructors that accept parameters corresponding to field names.
   *
   * @param valueType the class whose declared fields are to be retrieved
   * @return an array of field names in the given class
   */
  private static String[] toOrmRecordCompatibleConstructorParameterNames(Class<?> valueType) {
    return RefrectionOrmComponentUtils.getDeclaredFields(valueType).stream()
        .map(f -> f.getName())
        .toArray(String[]::new);
  }

  /**
   * Retrieves the parameter names of a record's canonical constructor. This method extracts the
   * names from the record's components.
   *
   * @param valueType the record class whose constructor parameter names are to be retrieved
   * @return an array of parameter names of the record's canonical constructor
   */
  private static <T extends Record> String[] toRecordConstructorParameterNames(Class<T> valueType) {
    return RefrectionOrmComponentUtils.getRecordComponents(valueType).stream()
        .map(RecordComponent::getName)
        .toArray(String[]::new);
  }

  private ReflectionConstrucorsUtils() {}
}
