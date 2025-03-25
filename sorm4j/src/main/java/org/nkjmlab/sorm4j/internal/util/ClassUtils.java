package org.nkjmlab.sorm4j.internal.util;

import static java.util.Map.entry;

import java.util.Map;

public final class ClassUtils {
  private ClassUtils() {}

  private static final Map<Class<?>, Class<?>> primitiveToWrapperMap =
      Map.ofEntries(
          entry(Boolean.TYPE, Boolean.class),
          entry(Byte.TYPE, Byte.class),
          entry(Character.TYPE, Character.class),
          entry(Short.TYPE, Short.class),
          entry(Integer.TYPE, Integer.class),
          entry(Long.TYPE, Long.class),
          entry(Double.TYPE, Double.class),
          entry(Float.TYPE, Float.class));

  public static Class<?> primitiveToWrapper(Class<?> clazz) {
    return primitiveToWrapperMap.get(clazz);
  }
}
