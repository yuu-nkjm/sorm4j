package org.nkjmlab.sorm4j.result;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.util.StringCache;

/**
 * A instance represents a row in a table. This interface extends {@link Map<String, Object>}. The
 * key is represented by canonical case defined by Sorm.
 *
 * @author yuu_nkjm
 *
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

  static RowMap of(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4,
      Object v4) {
    return create(Map.of(k1, v1, k2, v2, k3, v3, k4, v4));
  }

  static RowMap of(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4,
      Object v4, String k5, Object v5) {
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


}
