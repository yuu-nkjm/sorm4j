package org.nkjmlab.sorm4j.extension.datatype.jackson;

import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.extension.datatype.DataTypeSupport;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSupport implements DataTypeSupport {

  private final ObjectMapper objectMapper;

  public JacksonSupport(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public SormContext.Builder addSupport(SormContext.Builder builder) {
    return builder
        .addColumnValueToJavaObjectConverter(
            new JacksonColumnValueToJavaObjectConverter(objectMapper))
        .addSqlParameterSetter(new JacksonSqlParameterSetter(objectMapper));
  }
}
