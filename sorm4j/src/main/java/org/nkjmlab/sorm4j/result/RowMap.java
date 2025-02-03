package org.nkjmlab.sorm4j.result;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.util.StringCache;
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
   * Key to canonical key
   *
   * @param key
   * @return
   */
  @Experimental
  static String toKey(String key) {
    return StringCache.toCanonicalCase(key);
  }

  <T> T[] getArray(String key, Class<T> componentType);

  Double getDouble(String key);

  Float getFloat(String key);

  Integer getInteger(String key);

  LocalDate getLocalDate(String key);

  LocalDateTime getLocalDateTime(String key);

  LocalTime getLocalTime(String key);

  Long getLong(String key);

  Object getObject(String key);

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
  @Experimental
  static <T extends Record> RowMap fromRecord(T src) {
    try {
      RecordComponent[] recordComponents =
          recordComponentsCache.computeIfAbsent(
              src.getClass(), key -> src.getClass().getRecordComponents());
      BasicRowMap destMap = new BasicRowMap((int) (recordComponents.length / 0.75f), 0.75f);
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
   * A key of the map is not included in the record components. => the entry of the map is ignore.
   * A record component does not exists in the key set of the map => the record component set as null. if the component is primitive type, an exception is thrown.
   * </pre>
   *
   * @param <T>
   * @param src
   * @param toType
   * @return
   */
  @Experimental
  static <T extends Record> T toRecord(RowMap src, Class<T> toType) {
    try {
      RecordComponent[] recordComponents =
          recordComponentsCache.computeIfAbsent(toType, key -> toType.getRecordComponents());

      Object[] args = new Object[recordComponents.length];
      for (int i = 0; i < recordComponents.length; i++) {
        args[i] = src.get(StringCache.toCanonicalCase(recordComponents[i].getName()));
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
  @Experimental
  default <T extends Record> T toRecord(Class<T> toType) {
    return toRecord(this, toType);
  }
}
