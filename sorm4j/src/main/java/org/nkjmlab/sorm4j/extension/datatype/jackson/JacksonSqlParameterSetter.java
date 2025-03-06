package org.nkjmlab.sorm4j.extension.datatype.jackson;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.nkjmlab.sorm4j.common.annotation.Experimental;
import org.nkjmlab.sorm4j.context.SqlParameterSetter;
import org.nkjmlab.sorm4j.internal.util.Try;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Experimental
public class JacksonSqlParameterSetter implements SqlParameterSetter {
  private final ObjectMapper objectMapper;
  private final ContainerCache cache;

  public JacksonSqlParameterSetter(
      ObjectMapper objectMapper, Class<?>... ormJsonColumnContainerClasses) {
    this.objectMapper = objectMapper;
    this.cache = new ContainerCache(ormJsonColumnContainerClasses);
  }

  @Override
  public boolean test(PreparedStatement stmt, int parameterIndex, Object parameter)
      throws SQLException {
    return cache.isContainer(parameter.getClass());
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
