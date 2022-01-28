package org.nkjmlab.sorm4j.internal.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<K, V> extends LinkedHashMap<K, V> {

  private final int maxSize;

  public LruCache(int size) {
    super(size, 0.75f, true);
    this.maxSize = size;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return size() > maxSize;
  }
}
