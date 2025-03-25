package org.nkjmlab.sorm4j.context;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A functional interface for converting column values from a {@link ResultSet} into Java objects.
 *
 * <p>This interface is used to define custom conversion logic for mapping SQL column values to Java
 * objects during query execution. Implementations of this interface are responsible for handling
 * type conversion based on the target Java class.
 *
 * <p>Implementations can be registered in {@link org.nkjmlab.sorm4j.SormContext} using:
 *
 * <pre><code>
 * SormContext.builder()
 *     .setColumnValueToJavaObjectConverters(
 *             new JacksonColumnValueToJavaObjectConverter(objectMapper, ormJsonContainers))
 *     .build();
 * </code></pre>
 *
 * <p>Example implementation using Jackson for JSON conversion:
 *
 * <pre><code>
 * public class JacksonColumnValueToJavaObjectConverter implements ColumnValueToJavaObjectConverter {
 *
 *     private final ObjectMapper objectMapper;
 *     private final OrmJsonContainers ormJsonContainers;
 *
 *     public JacksonColumnValueToJavaObjectConverter(ObjectMapper objectMapper, OrmJsonContainers ormJsonContainers) {
 *         this.objectMapper = objectMapper;
 *         this.ormJsonContainers = ormJsonContainers;
 *     }
 *
 *     {@literal @}Override
 *     public boolean test(Class<?> toType) {
 *         return ormJsonContainers.isOrmJsonContainer(toType);
 *     }
 *
 *     {@literal @}Override
 *     public <T> T convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<T> toType) throws SQLException {
 *         try {
 *             return objectMapper.readValue(resultSet.getBytes(columnIndex), toType);
 *         } catch (IOException e) {
 *             throw Try.rethrow(e);
 *         }
 *     }
 * }
 * </code></pre>
 */
public interface ColumnValueToJavaObjectConverter {

  /**
   * Determines whether this converter supports conversion to the specified Java type.
   *
   * @param toType the target Java class
   * @return {@code true} if the converter supports the given type, otherwise {@code false}
   */
  boolean test(Class<?> toType);

  /**
   * Reads a column from the current row in the provided {@link java.sql.ResultSet} and returns an
   * instance of the specified Java {@link java.lang.Class} containing the converted value.
   *
   * <p>This method is mainly used for converting the result of a query, i.e., converting from SQL
   * types to Java objects of the specified class.
   *
   * <p>It is invoked during the transformation of {@link java.sql.ResultSet} rows into Java
   * objects. The target class corresponds to the field type in the mapped Java entity.
   *
   * <p>For non-primitive types, null values from the {@link java.sql.ResultSet} will be preserved.
   * However, for primitive types, a null value will be converted to the default value (e.g., 0 for
   * int).
   *
   * @param resultSet the {@link java.sql.ResultSet}, positioned at the row to be processed
   * @param columnIndex the index of the column in the result set (starting from 1)
   * @param columnType the SQL type of the column
   * @param toType the target Java {@link java.lang.Class} to convert the column value to
   * @return the converted Java object
   * @throws SQLException if a database access error occurs
   */
  <T> T convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<T> toType)
      throws SQLException;
}
