package org.nkjmlab.sorm4j.result;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.internal.result.BasicRowMap;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * A instance represents a row in a table. This interface extends {@link Map<String, Object>}. The
 * key is represented by canonical case defined by Sorm.
 *
 * @author yuu_nkjm
 */
public interface RowMap extends Map<String, Object> {

  static RowMap create() {
    return new BasicRowMap();
  }

  static RowMap create(int initialCapacity, float loadFactor) {
    return new BasicRowMap(initialCapacity, loadFactor);
  }

  static RowMap create(Map<String, Object> map) {
    return new BasicRowMap(map);
  }

  @SafeVarargs
  static RowMap of(Entry<String, Object>... entries) {
    return create(Map.ofEntries(entries));
  }

  static RowMap of(String k1, Object v1) {
    return create(Map.of(k1, v1));
  }

  static RowMap of(String k1, Object v1, String k2, Object v2) {
    return create(Map.of(k1, v1, k2, v2));
  }

  static RowMap of(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
    return create(Map.of(k1, v1, k2, v2, k3, v3));
  }

  static RowMap of(
      String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
    return create(Map.of(k1, v1, k2, v2, k3, v3, k4, v4));
  }

  static RowMap of(
      String k1,
      Object v1,
      String k2,
      Object v2,
      String k3,
      Object v3,
      String k4,
      Object v4,
      String k5,
      Object v5) {
    return create(Map.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
  }

  /**
   * The string is converted to canonical key
   *
   * @param str
   * @return
   */
  public static String toKey(String str) {
    return SormContext.getDefaultCanonicalStringCache().toCanonicalName(str);
  }

  /** key is converted as canonical case */
  @Override
  Object get(Object key);

  /**
   * Retrieves the value associated with the specified key as an array of the specified component
   * type.
   *
   * <p>If the retrieved value is {@code null}, this method returns {@code null}. The conversion is
   * performed using {@link ArrayUtils#convertToObjectArray(Class, Object)}.
   *
   * @param <T> the component type of the array
   * @param key the key whose associated value is to be retrieved
   * @param componentType the class of the array component type
   * @return the value converted to an array of {@code componentType}, or {@code null} if the value
   *     is absent
   */
  <T> T[] getArray(String key, Class<T> componentType);

  /**
   * Retrieves the value associated with the specified key as a {@link Double}.
   *
   * <p>If the retrieved value is {@code null}, this method returns {@code null}. If the value is a
   * subclass of {@link Number}, it is converted using {@code doubleValue()}. Otherwise, the value
   * is converted to a string and parsed as a double.
   *
   * @param key the key whose associated value is to be retrieved
   * @return the value converted to {@link Double}, or {@code null} if the value is absent
   * @throws NumberFormatException if the value cannot be parsed as a double
   */
  Double getDouble(String key);

  /**
   * Retrieves the value associated with the specified key as a {@link Float}.
   *
   * <p>If the retrieved value is {@code null}, this method returns {@code null}. If the value is a
   * subclass of {@link Number}, it is converted using {@code floatValue()}. Otherwise, the value is
   * converted to a string and parsed as a float.
   *
   * @param key the key whose associated value is to be retrieved
   * @return the value converted to {@link Float}, or {@code null} if the value is absent
   * @throws NumberFormatException if the value cannot be parsed as a float
   */
  Float getFloat(String key);

  /**
   * Retrieves the value associated with the specified key as an {@link Integer}.
   *
   * <p>If the retrieved value is {@code null}, this method returns {@code null}. If the value is a
   * subclass of {@link Number}, it is converted using {@code intValue()}. Otherwise, the value is
   * converted to a string and parsed as an integer.
   *
   * @param key the key whose associated value is to be retrieved
   * @return the value converted to {@link Integer}, or {@code null} if the value is absent
   * @throws NumberFormatException if the value cannot be parsed as an integer
   */
  Integer getInteger(String key);

  /**
   * Retrieves the value associated with the specified key as a {@link LocalDate}.
   *
   * <p>If the retrieved value is {@code null}, this method returns {@code null}. If the value is an
   * instance of {@link java.sql.Date}, it is converted using {@link java.sql.Date#toLocalDate()}.
   * Otherwise, the value is converted to a string and parsed using {@link LocalDate#parse(String)}.
   *
   * @param key the key whose associated value is to be retrieved
   * @return the value converted to {@link LocalDate}, or {@code null} if the value is absent
   * @throws DateTimeParseException if the value cannot be parsed as a valid date
   */
  LocalDate getLocalDate(String key);

  /**
   * Retrieves the value associated with the specified key as a {@link LocalDateTime}.
   *
   * <p>If the retrieved value is {@code null}, this method returns {@code null}. If the value is an
   * instance of {@link java.sql.Timestamp}, it is converted using {@link
   * java.sql.Timestamp#toLocalDateTime()}. Otherwise, the value is converted to a string and parsed
   * using {@link LocalDateTime#parse(String)}.
   *
   * @param key the key whose associated value is to be retrieved
   * @return the value converted to {@link LocalDateTime}, or {@code null} if the value is absent
   * @throws DateTimeParseException if the value cannot be parsed as a valid date-time
   */
  LocalDateTime getLocalDateTime(String key);

  /**
   * Retrieves the value associated with the specified key as a {@link LocalTime}.
   *
   * <p>If the retrieved value is {@code null}, this method returns {@code null}. If the value is an
   * instance of {@link java.sql.Time}, it is converted using {@link java.sql.Time#toLocalTime()}.
   * Otherwise, the value is converted to a string and parsed using {@link LocalTime#parse(String)}.
   *
   * @param key the key whose associated value is to be retrieved
   * @return the value converted to {@link LocalTime}, or {@code null} if the value is absent
   * @throws DateTimeParseException if the value cannot be parsed as a valid time
   */
  LocalTime getLocalTime(String key);

  /**
   * Retrieves the value associated with the specified key as a {@link Long}.
   *
   * <p>If the retrieved value is {@code null}, this method returns {@code null}. If the value is a
   * subclass of {@link Number}, it is converted using {@code longValue()}. Otherwise, the value is
   * converted to a string and parsed as a long.
   *
   * @param key the key whose associated value is to be retrieved
   * @return the value converted to {@link Long}, or {@code null} if the value is absent
   * @throws NumberFormatException if the value cannot be parsed as a long
   */
  Long getLong(String key);

  Object getObject(String key);

  /**
   * Retrieves the value associated with the specified key as a {@link String}.
   *
   * <p>If the retrieved value is {@code null}, this method returns {@code null}. If the value is an
   * instance of {@code byte[]}, it is converted to a string using the default character encoding.
   * Otherwise, {@code toString()} is used to obtain the string representation.
   *
   * @param key the key whose associated value is to be retrieved
   * @return the value converted to {@link String}, or {@code null} if the value is absent
   */
  String getString(String key);

  List<Object> getObjectList(String... key);

  List<String> getStringList(String... keys);

  /**
   * A {@link RowMap} object is created from a record object.
   *
   * @param <T>
   * @param src
   * @return
   */
  static <T extends Record> RowMap fromRecord(T src) {
    try {
      RecordComponent[] recordComponents =
          recordComponentsCache.computeIfAbsent(
              src.getClass(), key -> src.getClass().getRecordComponents());
      RowMap destMap = RowMap.create((int) (recordComponents.length / 0.75f), 0.75f);
      for (int i = 0; i < recordComponents.length; i++) {
        destMap.put(recordComponents[i].getName(), recordComponents[i].getAccessor().invoke(src));
      }
      return destMap;
    } catch (IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | SecurityException e) {
      throw Try.rethrow(e);
    }
  }

  /**
   * The object is converted to a record object.
   *
   * <pre>
   * A key of the map is included in the record components. =>  the record component set the value.
   * A key of the map is not included in the record components. => the entry of the map is ignore and skipped.
   * A record component does not exists in the key set of the map => the record component set as null. if the component is primitive type, an exception is thrown.
   * </pre>
   *
   * @param <T>
   * @param src
   * @param toType
   * @return
   */
  static <T extends Record> T toRecord(RowMap src, Class<T> toType) {
    try {
      RecordComponent[] recordComponents =
          recordComponentsCache.computeIfAbsent(toType, key -> toType.getRecordComponents());

      Object[] args = new Object[recordComponents.length];
      for (int i = 0; i < recordComponents.length; i++) {
        args[i] = src.get(recordComponents[i].getName());
      }

      return getConstructor(recordComponents, toType).newInstance(args);
    } catch (InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | SecurityException e) {
      throw Try.rethrow(e);
    }
  }

  @SuppressWarnings("unchecked")
  static <T> Constructor<T> getConstructor(RecordComponent[] recordComponents, Class<T> toType) {
    return (Constructor<T>)
        constructorCache.computeIfAbsent(
            toType,
            key -> {
              Class<?>[] types = new Class<?>[recordComponents.length];
              for (int i = 0; i < recordComponents.length; i++) {
                types[i] = recordComponents[i].getType();
              }
              try {
                return toType.getDeclaredConstructor(types);
              } catch (NoSuchMethodException | SecurityException e) {
                throw Try.rethrow(e);
              }
            });
  }

  static final ConcurrentMap<Class<?>, RecordComponent[]> recordComponentsCache =
      new ConcurrentHashMap<>();
  static final ConcurrentMap<Class<?>, Constructor<?>> constructorCache = new ConcurrentHashMap<>();

  /**
   * The object is converted to a record object.
   *
   * <pre>
   * A key of the map is included in the record components. =>  the record component set the value.
   * A key of the map is not included in the record components. => the entry of the map is ignore.
   * A record component does not exists in the key set of the map => the record component set as null. if the component is primitive type, an exception is thrown.
   * </pre>
   *
   * @param <T>
   * @param toType
   * @return
   */
  default <T extends Record> T toRecord(Class<T> toType) {
    return toRecord(this, toType);
  }
}
