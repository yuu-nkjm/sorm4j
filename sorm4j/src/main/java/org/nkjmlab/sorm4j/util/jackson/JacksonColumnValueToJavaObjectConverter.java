package org.nkjmlab.sorm4j.util.jackson;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverter;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.util.datatype.OrmJsonColumnContainer;
import org.nkjmlab.sorm4j.util.jackson.JacksonSormContext.OrmJsonContainers;

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
  private final OrmJsonContainers ormJsonContainers;

  /**
   * Constructs a new converter with the specified {@link ObjectMapper} and optional JSON column
   * container classes.
   *
   * @param objectMapper the Jackson object mapper to use for conversion
   * @param ormJsonContainers
   * @param ormJsonColumnContainerClasses classes that should be treated as JSON containers
   */
  public JacksonColumnValueToJavaObjectConverter(
      ObjectMapper objectMapper, OrmJsonContainers ormJsonContainers) {
    this.objectMapper = objectMapper;
    this.ormJsonContainers = ormJsonContainers;
  }

  @Override
  public boolean test(Class<?> toType) {
    return ormJsonContainers.isOrmJsonContainer(toType);
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
  public Object convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<?> toType)
      throws SQLException {
    try {
      return objectMapper.readValue(resultSet.getBytes(columnIndex), toType);
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }
}
