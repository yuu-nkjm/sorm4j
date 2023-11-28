package org.nkjmlab.sorm4j.internal.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Concurrent cache. This class simply focus to limit number of entries.
 *
 * @author yuu_nkjm
 * @param <K>
 * @param <V>
 */
@SuppressWarnings("serial")
public class ConcurrentCache<K, V> extends ConcurrentHashMap<K, V> {

  private final int maxSize;

  public ConcurrentCache(int maxSize) {
    this.maxSize = maxSize;
  }

  @Override
  public V put(K key, V value) {
    if (size() >= maxSize) {
      clear();
    }
    return super.put(key, value);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    if (size() >= maxSize || m.size() + size() > maxSize) {
      clear();
    }
    super.putAll(
        m.entrySet().stream()
            .limit(maxSize - size())
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));
  }
}
