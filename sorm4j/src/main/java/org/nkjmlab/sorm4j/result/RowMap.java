package org.nkjmlab.sorm4j.result;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.nkjmlab.sorm4j.internal.result.RowMapImpl;

/**
 * A instance represents a row in a table. This interface extends {@link Map<String, Object>}.
 *
 *
 * @author yuu_nkjm
 *
 */
public interface RowMap extends Map<String, Object> {


  static RowMap create(Map<String, Object> map) {
    return new RowMapImpl(new LinkedHashMap<>(map));
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

  @SafeVarargs
  static RowMap of(Entry<String, Object>... entries) {
    return create(Map.ofEntries(entries));
  }


  String getString(String key);

  Integer getInteger(String key);

  Double getDouble(String key);

  Float getFloat(String key);

  Long getLong(String key);

  LocalDate getLocalDate(String key);

  LocalTime getLocalTime(String key);

  LocalDateTime getLocalDateTime(String key);

  <T> T[] getArray(String key, Class<T> valueType);


}
