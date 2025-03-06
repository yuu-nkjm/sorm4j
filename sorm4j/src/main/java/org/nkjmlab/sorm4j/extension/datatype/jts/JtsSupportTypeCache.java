package org.nkjmlab.sorm4j.extension.datatype.jts;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nkjmlab.sorm4j.extension.datatype.SupportTypeCache;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;

class JtsSupportTypeCache implements SupportTypeCache {
  private final Map<Class<?>, Boolean> cache = new ConcurrentHashMap<>();

  public JtsSupportTypeCache(Class<?>... containerClasses) {
    Arrays.stream(containerClasses).forEach(c -> cache.put(c, true));
  }

  @Override
  public boolean isSupport(Class<?> toType) {
    return cache.computeIfAbsent(
        (Class<?>) toType,
        key ->
            GeometryJts.class.isAssignableFrom(toType)
                || GeometryJts.class.isAssignableFrom(ArrayUtils.getInternalComponentType(toType)));
  }
}
