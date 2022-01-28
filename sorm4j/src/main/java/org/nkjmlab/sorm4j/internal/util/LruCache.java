package org.nkjmlab.sorm4j.internal.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<K, V> extends LinkedHashMap<K, V> {

  private final int maxSize;

  /**
   * Creates LRU cache which is the given max size.
   *
   * @param maxSize
   */
  public LruCache(int maxSize) {
    super(maxSize, 0.75f, true);
    this.maxSize = maxSize;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return size() > maxSize;
  }
}
