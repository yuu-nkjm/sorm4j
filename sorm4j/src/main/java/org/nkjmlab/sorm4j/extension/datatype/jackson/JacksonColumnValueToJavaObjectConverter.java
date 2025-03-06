package org.nkjmlab.sorm4j.extension.datatype.jackson;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nkjmlab.sorm4j.common.annotation.Experimental;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverter;
import org.nkjmlab.sorm4j.extension.datatype.SupportTypeCache;
import org.nkjmlab.sorm4j.extension.datatype.jackson.annotation.OrmJsonColumnContainer;
import org.nkjmlab.sorm4j.internal.util.Try;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A converter that utilizes Jackson's {@link ObjectMapper} to convert column values from a {@link
 * ResultSet} into Java objects.
 *
 * <p>This implementation supports JSON serialization and deserialization for types such as lists,
 * maps, and arrays where components are annotated with {@link OrmJsonColumnContainer}.
 */
@Experimental
public class JacksonColumnValueToJavaObjectConverter implements ColumnValueToJavaObjectConverter {

  private final ObjectMapper objectMapper;
  private final SupportTypeCache cache;

  /**
   * Constructs a new converter with the specified {@link ObjectMapper} and optional JSON column
   * container classes.
   *
   * @param objectMapper the Jackson object mapper to use for conversion
   * @param ormJsonColumnContainerClasses
   * @param ormJsonColumnContainerClasses classes that should be treated as JSON containers
   */
  public JacksonColumnValueToJavaObjectConverter(
      ObjectMapper objectMapper, Class<?>... ormJsonColumnContainerClasses) {
    this.objectMapper = objectMapper;
    this.cache = new JsonSupportTypeCache(ormJsonColumnContainerClasses);
  }

  @Override
  public boolean test(Class<?> toType) {
    return cache.isSupport(toType);
  }

  /**
   * Converts a column value from the given {@link ResultSet} to the specified target type.
   *
   * @param resultSet the result set containing the column value
   * @param columnIndex the column index
   * @param columnType the SQL type of the column
   * @param toType the target Java type to convert to
   * @return the converted object
   * @throws SQLException if a database access error occurs
   */
  @Override
  public <T> T convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<T> toType)
      throws SQLException {
    try {
      return objectMapper.readValue(resultSet.getBytes(columnIndex), toType);
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }
}
