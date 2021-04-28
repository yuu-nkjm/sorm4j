package org.nkjmlab.sorm4j.internal.mapping;

import java.util.HashMap;
import java.util.Map;
import org.nkjmlab.sorm4j.extension.SormOptions;

public class SormOptionsImpl implements SormOptions {
  private final Map<String, Object> options;

  public SormOptionsImpl(Map<String, Object> options) {
    this.options = new HashMap<>(options);
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
