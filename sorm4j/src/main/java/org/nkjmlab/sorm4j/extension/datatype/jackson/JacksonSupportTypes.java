package org.nkjmlab.sorm4j.extension.datatype.jackson;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.nkjmlab.sorm4j.extension.datatype.SupportTypes;
import org.nkjmlab.sorm4j.extension.datatype.jackson.annotation.OrmJacksonColumn;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;

class JacksonSupportTypes implements SupportTypes {
  private final Set<Class<?>> cache = ConcurrentHashMap.newKeySet();

  @Override
  public boolean isSupport(Class<?> toType) {
    if (testType(toType)) {
      return true;
    }
    Class<?> componentType = ArrayUtils.getInternalComponentType(toType);
    if (componentType == null) {
      return false;
    }
    return testType(componentType);
  }

  private boolean testType(Class<?> toType) {
    if (toType == null) {
      return false;
    }
    if (cache.contains(toType)) {
      return true;
    }
    if (toType.getAnnotation(OrmJacksonColumn.class) != null) {
      cache.add(toType);
      return true;
    }
    if (List.class.isAssignableFrom(toType) || Map.class.isAssignableFrom(toType)) {
      cache.add(toType);
      return true;
    }
    return false;
  }
}
