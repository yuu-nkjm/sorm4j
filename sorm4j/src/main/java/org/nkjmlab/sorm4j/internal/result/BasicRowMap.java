package org.nkjmlab.sorm4j.internal.result;

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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.result.RowMap;

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
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Map)) {
      return false;
    }
    Map<?, ?> other = (Map<?, ?>) obj;
    return Objects.equals(map, other);
  }

  @Override
  public String toString() {
    return map.toString();
  }

  @Override
  public String getString(String key) {
    return getHelper(
        key,
        String.class,
        val -> byte[].class.isInstance(val) ? new String((byte[]) val) : val.toString());
  }

  @Override
  public Integer getInteger(String key) {
    return getHelper(
        key,
        Integer.class,
        val ->
            Number.class.isInstance(val)
                ? ((Number) val).intValue()
                : Integer.valueOf(val.toString()));
  }

  @Override
  public Long getLong(String key) {
    return getHelper(
        key,
        Long.class,
        val ->
            Number.class.isInstance(val)
                ? ((Number) val).longValue()
                : Long.valueOf(val.toString()));
  }

  @Override
  public Float getFloat(String key) {
    return getHelper(
        key,
        Float.class,
        val ->
            Number.class.isInstance(val)
                ? ((Number) val).floatValue()
                : Float.valueOf(val.toString()));
  }

  @Override
  public Double getDouble(String key) {
    return getHelper(
        key,
        Double.class,
        val ->
            Number.class.isInstance(val)
                ? ((Number) val).doubleValue()
                : Double.valueOf(val.toString()));
  }

  private <T> T getHelper(String key, Class<T> toType, Function<Object, T> converter) {
    Object val = get(key);
    if (val == null) {
      return null;
    }
    if (toType.isInstance(val)) {
      return toType.cast(val);
    }
    return converter.apply(val);
  }

  @Override
  public LocalDate getLocalDate(String key) {
    return getHelper(
        key,
        LocalDate.class,
        val ->
            java.sql.Date.class.isInstance(val)
                ? ((java.sql.Date) val).toLocalDate()
                : LocalDate.parse(val.toString()));
  }

  @Override
  public LocalTime getLocalTime(String key) {
    return getHelper(
        key,
        LocalTime.class,
        val ->
            java.sql.Date.class.isInstance(val)
                ? ((java.sql.Time) val).toLocalTime()
                : LocalTime.parse(val.toString()));
  }

  @Override
  public LocalDateTime getLocalDateTime(String key) {
    return getHelper(
        key,
        LocalDateTime.class,
        val ->
            java.sql.Timestamp.class.isInstance(val)
                ? ((java.sql.Timestamp) val).toLocalDateTime()
                : LocalDateTime.parse(val.toString()));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T[] getArray(String key, Class<T> componentType) {
    Object val = get(key);
    if (val == null) {
      return null;
    }
    if (val instanceof Object[]
        && componentType.isAssignableFrom(val.getClass().getComponentType())) {
      return (T[]) val;
    }
    return ArrayUtils.convertToObjectArray(componentType, val);
  }

  @Override
  public List<Object> getObjectList(String... keys) {
    return Arrays.stream(keys).map(key -> get(key)).collect(Collectors.toList());
  }

  @Override
  public List<String> getStringList(String... keys) {
    return Arrays.stream(keys).map(key -> getString(key)).collect(Collectors.toList());
  }

  @Override
  public Object getObject(String key) {
    return get(key);
  }
}
