package org.nkjmlab.sorm4j.extension.datatype.jackson;

import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.extension.datatype.DataTypeSupport;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSupport implements DataTypeSupport {

  private final ObjectMapper objectMapper;
  private final Class<?>[] jsonColumns;

  public JacksonSupport(ObjectMapper objectMapper, Class<?>... jsonColumns) {
    this.objectMapper = objectMapper;
    this.jsonColumns = jsonColumns;
  }

  @Override
  public SormContext.Builder addSupport(SormContext.Builder builder) {
    return builder
        .addColumnValueToJavaObjectConverter(
            new JacksonColumnValueToJavaObjectConverter(objectMapper, jsonColumns))
        .addSqlParameterSetter(new JacksonSqlParameterSetter(objectMapper, jsonColumns));
  }
}
