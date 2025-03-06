package org.nkjmlab.sorm4j.extension.datatype.jackson;

import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.extension.datatype.DataTypeSupport;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides support for handling JSON data types in SORM (Simple Object Relational Mapping) by
 * integrating Jackson for serialization and deserialization.
 *
 * <p>This class registers custom converters that allow storing and retrieving JSON objects in
 * database columns using {@link ObjectMapper}.
 *
 * @author yuu_nkjm
 */
public class JacksonSupport implements DataTypeSupport {

  private final ObjectMapper objectMapper;

  /**
   * Constructs a new {@code JacksonSupport} instance with the given {@link ObjectMapper}.
   *
   * @param objectMapper the Jackson {@link ObjectMapper} used for JSON serialization and
   *     deserialization
   */
  public JacksonSupport(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Registers Jackson-based converters to handle JSON serialization and deserialization in SORM.
   *
   * @param builder the {@link SormContext.Builder} to which the JSON support is added
   * @return the updated {@link SormContext.Builder} instance
   */
  @Override
  public SormContext.Builder addSupport(SormContext.Builder builder) {
    return builder
        .addColumnValueToJavaObjectConverter(
            new JacksonColumnValueToJavaObjectConverter(objectMapper))
        .addSqlParameterSetter(new JacksonSqlParameterSetter(objectMapper));
  }
}
