package org.nkjmlab.sorm4j.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.nkjmlab.sorm4j.extension.SormOptions;

public class SormOptionsImpl implements SormOptions {
  private final Map<String, Object> options;

  public SormOptionsImpl(Map<String, Object> options) {
    this.options = new ConcurrentHashMap<>(options);
  }

  @Override
  public Object get(String key) {
    return options.get(key);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getOrDefault(String key, T defaultValue) {
    return (T) options.getOrDefault(key, defaultValue);
  }

}
