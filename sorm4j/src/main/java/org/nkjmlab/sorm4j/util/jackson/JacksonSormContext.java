package org.nkjmlab.sorm4j.util.jackson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.context.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.util.datatype.OrmJsonColumnContainer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Experimental
public class JacksonSormContext {

  private JacksonSormContext() {}

  public static SormContext.Builder builder(
      ObjectMapper objectMapper, Class<?>... ormJsonColumnContainerClasses) {

    OrmJsonContainers ormJsonContainers = new OrmJsonContainers(ormJsonColumnContainerClasses);

    return SormContext.builder()
        .setColumnValueToJavaObjectConverters(
            new DefaultColumnValueToJavaObjectConverters(
                new JacksonColumnValueToJavaObjectConverter(objectMapper, ormJsonContainers)))
        .setSqlParametersSetter(
            new DefaultSqlParametersSetter(
                new JacksonSqlParameterSetter(objectMapper, ormJsonContainers)));
  }

  public static class OrmJsonContainers {
    private final Map<Class<?>, Boolean> ormJsonContainer = new ConcurrentHashMap<>();

    public OrmJsonContainers(Class<?>... ormJsonColumnContainerClasses) {
      Arrays.stream(ormJsonColumnContainerClasses).forEach(c -> ormJsonContainer.put(c, true));
    }

    /**
     * Determines whether the specified type should be processed as a JSON object.
     *
     * <p>This method checks whether the given type is a JSON container type. It handles:
     *
     * <ul>
     *   <li>Lists: Any class that is assignable from {@link List} is considered a JSON object.
     *   <li>Maps: Any class that is assignable from {@link Map} is considered a JSON object.
     *   <li>Arrays: If the component type of the array is itself a JSON container or is annotated
     *       with {@link OrmJsonColumnContainer}, it is considered a JSON object.
     * </ul>
     *
     * @param toType the target type
     * @return {@code true} if the type is considered a JSON object, otherwise {@code false}
     */
    public boolean isOrmJsonContainer(Class<?> toType) {
      return List.class.isAssignableFrom(toType)
          || Map.class.isAssignableFrom(toType)
          || ormJsonContainer.getOrDefault(ArrayUtils.getInternalComponentType(toType), false)
          || ArrayUtils.getInternalComponentType(toType).getAnnotation(OrmJsonColumnContainer.class)
              != null;
    }
  }
}
