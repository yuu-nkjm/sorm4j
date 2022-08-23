package org.nkjmlab.sorm4j.util.jackson;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.context.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.context.SormContext;
import com.fasterxml.jackson.databind.ObjectMapper;

@Experimental
public class JacksonSormContext {

  private JacksonSormContext() {}

  public static SormContext.Builder builder(ObjectMapper objectMapper,
      Class<?>... ormJsonColumnContainerClasses) {
    return SormContext.builder()
        .setColumnValueToJavaObjectConverters(new DefaultColumnValueToJavaObjectConverters(
            new JacksonColumnValueToJavaObjectConverter(objectMapper,
                ormJsonColumnContainerClasses)))
        .setSqlParametersSetter(new DefaultSqlParametersSetter(
            new JacksonSqlParameterSetter(objectMapper, ormJsonColumnContainerClasses)));
  }

}
