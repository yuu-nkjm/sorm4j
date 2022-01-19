package org.nkjmlab.sorm4j.internal.sql.result;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.nkjmlab.sorm4j.result.RowMap;

public class RowMapImpl implements RowMap {

  private final LinkedHashMap<String, Object> map;

  public RowMapImpl(LinkedHashMap<String, Object> map) {
    this.map = map;
  }

  public RowMapImpl(int initialCapacity, float loadFactor) {
    this(new LinkedHashMap<>(initialCapacity, loadFactor));
  }

  public RowMapImpl() {
    this(new LinkedHashMap<>());
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
    return map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  @Override
  public Object get(Object key) {
    return map.get(key);
  }

  @Override
  public Object put(String key, Object value) {
    return map.put(key, value);
  }

  @Override
  public Object remove(Object key) {
    return map.remove(key);
  }

  @Override
  public void putAll(Map<? extends String, ? extends Object> m) {
    map.putAll(m);
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
    return Objects.hash(map);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof RowMapImpl))
      return false;
    RowMapImpl other = (RowMapImpl) obj;
    return Objects.equals(map, other.map);
  }

  @Override
  public String toString() {
    return map.toString();
  }

}
