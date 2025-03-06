package org.nkjmlab.sorm4j.extension.datatype.jts;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nkjmlab.sorm4j.internal.util.ArrayUtils;

class ContainerCache {
  private final Map<Class<?>, Boolean> cache = new ConcurrentHashMap<>();

  public ContainerCache(Class<?>... containerClasses) {
    Arrays.stream(containerClasses).forEach(c -> cache.put(c, true));
  }

  public boolean isContainer(Class<?> toType) {
    return cache.computeIfAbsent(
        (Class<?>) toType,
        key ->
            GeometryJts.class.isAssignableFrom(toType)
                || GeometryJts.class.isAssignableFrom(ArrayUtils.getInternalComponentType(toType)));
  }
}
