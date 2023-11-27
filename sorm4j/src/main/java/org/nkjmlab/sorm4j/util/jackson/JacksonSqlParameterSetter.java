package org.nkjmlab.sorm4j.util.jackson;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.context.SqlParameterSetter;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.util.datatype.OrmJsonColumnContainer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Experimental
public class JacksonSqlParameterSetter implements SqlParameterSetter {
  private final ObjectMapper objectMapper;
  private final Map<Class<?>, Boolean> ormJsonContainer = new ConcurrentHashMap<>();

  public JacksonSqlParameterSetter(
      ObjectMapper objectMapper, Class<?>... ormJsonColumnContainerClasses) {
    this.objectMapper = objectMapper;
    Arrays.stream(ormJsonColumnContainerClasses).forEach(c -> ormJsonContainer.put(c, true));
  }

  private boolean isOrmJsonContainer(Class<?> type) {
    return ormJsonContainer.computeIfAbsent(
        type,
        key ->
            ArrayUtils.getInternalComponentType(type).getAnnotation(OrmJsonColumnContainer.class)
                    != null
                || List.class.isAssignableFrom(type)
                || Map.class.isAssignableFrom(type)
                || ormJsonContainer.getOrDefault(ArrayUtils.getInternalComponentType(type), false));
  }

  @Override
  public boolean test(PreparedStatement stmt, int parameterIndex, Object parameter)
      throws SQLException {
    return isOrmJsonContainer(parameter.getClass());
  }

  @Override
  public void setParameter(PreparedStatement stmt, int parameterIndex, Object parameter)
      throws SQLException {
    try {
      stmt.setBytes(parameterIndex, objectMapper.writeValueAsBytes(parameter));
    } catch (JsonProcessingException e) {
      throw Try.rethrow(e);
    }
  }
}
