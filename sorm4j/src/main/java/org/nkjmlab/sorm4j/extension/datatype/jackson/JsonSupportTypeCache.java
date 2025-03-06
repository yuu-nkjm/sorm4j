package org.nkjmlab.sorm4j.extension.datatype.jackson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nkjmlab.sorm4j.extension.datatype.SupportTypeCache;
import org.nkjmlab.sorm4j.extension.datatype.jackson.annotation.OrmJsonColumnContainer;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;

class JsonSupportTypeCache implements SupportTypeCache {
  private final Map<Class<?>, Boolean> cache = new ConcurrentHashMap<>();

  public JsonSupportTypeCache(Class<?>... containerClasses) {
    Arrays.stream(containerClasses).forEach(c -> cache.put(c, true));
  }

  /**
   * Determines whether the specified type should be processed as a JSON object.
   *
   * <p>This method checks whether the given type is a JSON container type. It handles:
   *
   * <ul>
   *   <li>Lists: Any class that is assignable from {@link List} is considered a JSON object.
   *   <li>Maps: Any class that is assignable from {@link Map} is considered a JSON object. public
   *   <li>Arrays: If the component type of the array is itself a JSON container or is annotated
   *       with {@link OrmJsonColumnContainer}, it is considered a JSON object.
   * </ul>
   *
   * @param toType the target type public @return {@code true} if the type is considered a JSON
   *     object, otherwise {@code false}
   */
  @Override
  public boolean isSupport(Class<?> toType) {
    return List.class.isAssignableFrom(toType)
        || Map.class.isAssignableFrom(toType)
        || cache.getOrDefault(ArrayUtils.getInternalComponentType(toType), false)
        || ArrayUtils.getInternalComponentType(toType).getAnnotation(OrmJsonColumnContainer.class)
            != null;
  }
}
