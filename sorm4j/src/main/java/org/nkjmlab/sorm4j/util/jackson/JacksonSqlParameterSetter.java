package org.nkjmlab.sorm4j.util.jackson;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.context.SqlParameterSetter;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.util.jackson.JacksonSormContext.OrmJsonContainers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Experimental
public class JacksonSqlParameterSetter implements SqlParameterSetter {
  private final ObjectMapper objectMapper;
  private final OrmJsonContainers ormJsonContainers;

  public JacksonSqlParameterSetter(ObjectMapper objectMapper, OrmJsonContainers ormJsonContainers) {
    this.objectMapper = objectMapper;
    this.ormJsonContainers = ormJsonContainers;
  }

  @Override
  public boolean test(PreparedStatement stmt, int parameterIndex, Object parameter)
      throws SQLException {
    return ormJsonContainers.isOrmJsonContainer(parameter.getClass());
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
