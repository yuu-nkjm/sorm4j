package org.nkjmlab.sorm4j.extension.datatype.jackson;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.nkjmlab.sorm4j.context.SqlParameterSetter;
import org.nkjmlab.sorm4j.extension.datatype.SupportTypes;
import org.nkjmlab.sorm4j.internal.util.Try;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSqlParameterSetter implements SqlParameterSetter {
  private final ObjectMapper objectMapper;
  private final SupportTypes supportTypes;

  public JacksonSqlParameterSetter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.supportTypes = new JacksonSupportTypes();
  }

  @Override
  public boolean test(PreparedStatement stmt, int parameterIndex, Object parameter)
      throws SQLException {
    return supportTypes.isSupport(parameter.getClass());
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
