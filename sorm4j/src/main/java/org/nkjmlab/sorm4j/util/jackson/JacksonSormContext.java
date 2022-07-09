package org.nkjmlab.sorm4j.util.jackson;

import org.nkjmlab.sorm4j.context.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.context.SormContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSormContext {

  private JacksonSormContext() {}

  public static SormContext.Builder builder(ObjectMapper objectMapper) {
    return SormContext.builder()
        .setColumnValueToJavaObjectConverters(new DefaultColumnValueToJavaObjectConverters(
            new JacksonColumnValueToJavaObjectConverter(objectMapper)))
        .setSqlParametersSetter(
            new DefaultSqlParametersSetter(new JacksonSqlParameterSetter(objectMapper)));
  }

}
