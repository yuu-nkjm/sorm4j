package org.nkjmlab.sorm4j.result;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;

public class BasicRowMap implements RowMap {

  private final LinkedHashMap<String, Object> map;

  public BasicRowMap() {
    this.map = new LinkedHashMap<>();
  }

  public BasicRowMap(int initialCapacity, float loadFactor) {
    this.map = new LinkedHashMap<>(initialCapacity, loadFactor);
  }

  public BasicRowMap(Map<String, Object> map) {
    this.map =
        List.copyOf(map.entrySet()).stream()
            .collect(
                LinkedHashMap::new,
                (m, v) -> m.put(toKey(v.getKey()), v.getValue()),
                LinkedHashMap::putAll);
  }

  private static String toKey(String key) {
    return RowMap.toKey(key);
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return map.containsKey(toKey(key.toString()));
  }

  @Override
  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  @Override
  public Object get(Object key) {
    return map.get(toKey(key.toString()));
  }

  @Override
  public Object put(String key, Object value) {
    return map.put(toKey(key), value);
  }

  @Override
  public Object remove(Object key) {
    return map.remove(toKey(key.toString()));
  }

  @Override
  public void putAll(Map<? extends String, ? extends Object> m) {
    m.entrySet().forEach(en -> map.put(toKey(en.getKey()), en.getValue()));
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public Set<String> keySet() {
    return map.keySet();
  }

  @Override
  public Collection<Object> values() {
    return map.values();
  }

  @Override
  public Set<Entry<String, Object>> entrySet() {
    return map.entrySet();
  }

  @Override
  public int hashCode() {
    return map.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Map)) return false;
    Map<?, ?> other = (Map<?, ?>) obj;
    return Objects.equals(map, other);
  }

  @Override
  public String toString() {
    return map.toString();
  }

  @Override
  public String getString(String key) {
    Object val = get(key);
    if (val == null) {
      return null;
    }
    if (val instanceof byte[]) {
      return new String((byte[]) val);
    }
    return val.toString();
  }

  @Override
  public Integer getInteger(String key) {
    return getNumberHelper(
        key,
        Integer.class,
        (clazz, val) ->
            Number.class.isAssignableFrom(clazz)
                ? ((Number) val).intValue()
                : Integer.valueOf(val.toString()));
  }

  @Override
  public Long getLong(String key) {
    return getNumberHelper(
        key,
        Long.class,
        (clazz, val) ->
            Number.class.isAssignableFrom(clazz)
                ? ((Number) val).longValue()
                : Long.valueOf(val.toString()));
  }

  @Override
  public Float getFloat(String key) {
    return getNumberHelper(
        key,
        Float.class,
        (clazz, val) ->
            Number.class.isAssignableFrom(clazz)
                ? ((Number) val).floatValue()
                : Float.valueOf(val.toString()));
  }

  @Override
  public Double getDouble(String key) {
    return getNumberHelper(
        key,
        Double.class,
        (clazz, val) ->
            Number.class.isAssignableFrom(clazz)
                ? ((Number) val).doubleValue()
                : Double.valueOf(val.toString()));
  }

  private <T> T getNumberHelper(
      String key, Class<T> toType, BiFunction<Class<?>, Object, T> function) {
    Object val = get(key);
    if (val == null) {
      return null;
    }
    Class<? extends Object> clazz = val.getClass();
    return function.apply(clazz, val);
  }

  @Override
  public LocalDate getLocalDate(String key) {
    Object val = get(key);
    if (val == null) {
      return null;
    }
    if (val instanceof java.sql.Date) {
      return ((java.sql.Date) val).toLocalDate();
    }
    return LocalDate.parse(val.toString());
  }

  @Override
  public LocalTime getLocalTime(String key) {
    Object val = get(key);
    if (val == null) {
      return null;
    }
    if (val instanceof java.sql.Time) {
      return ((java.sql.Time) val).toLocalTime();
    }
    return LocalTime.parse(val.toString());
  }

  @Override
  public LocalDateTime getLocalDateTime(String key) {
    Object val = get(key);
    if (val == null) {
      return null;
    }
    if (val instanceof java.sql.Timestamp) {
      return ((java.sql.Timestamp) val).toLocalDateTime();
    }
    return LocalDateTime.parse(val.toString());
  }

  @Override
  public <T> T[] getArray(String key, Class<T> componentType) {
    Object val = get(key);
    if (val == null) {
      return null;
    }
    return ArrayUtils.convertToObjectArray(componentType, val);
  }

  @Override
  public Object getObject(String key) {
    return get(key);
  }

  @Override
  public List<Object> getObjectList(String... keys) {
    return Arrays.stream(keys).map(key -> getObject(key)).collect(Collectors.toList());
  }

  @Override
  public List<String> getStringList(String... keys) {
    return Arrays.stream(keys).map(key -> getString(key)).collect(Collectors.toList());
  }
}
