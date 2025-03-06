package org.nkjmlab.sorm4j.extension.datatype.jackson;

import org.nkjmlab.sorm4j.common.annotation.Experimental;
import org.nkjmlab.sorm4j.context.SormContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@Experimental
public class JacksonSormContext {

  private JacksonSormContext() {}

  public static SormContext.Builder builder(
      ObjectMapper objectMapper, Class<?>... ormJsonColumnContainerClasses) {

    return SormContext.builder()
        .addColumnValueToJavaObjectConverter(
            new JacksonColumnValueToJavaObjectConverter(
                objectMapper, ormJsonColumnContainerClasses))
        .addSqlParameterSetter(
            new JacksonSqlParameterSetter(objectMapper, ormJsonColumnContainerClasses));
  }
}
