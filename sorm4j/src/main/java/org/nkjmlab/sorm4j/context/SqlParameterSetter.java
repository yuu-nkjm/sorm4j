package org.nkjmlab.sorm4j.context;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A functional interface for setting parameters in a {@link PreparedStatement}.
 *
 * <p>This interface defines a contract for setting Java objects as SQL parameters in a {@link
 * PreparedStatement}. Implementations of this interface provide custom logic for handling type
 * conversion and parameter binding before executing SQL statements.
 *
 * <p>Implementations can be registered in {@link org.nkjmlab.sorm4j.SormContext} using:
 *
 * <pre><code>
 * SormContext.builder()
 *     .setSqlParametersSetters(
 *             new JacksonSqlParameterSetter(objectMapper, ormJsonContainers))
 *     .build();
 * </code></pre>
 *
 * <p>Example implementation using Jackson for JSON parameter conversion:
 *
 * <pre><code>
 * public class JacksonSqlParameterSetter implements SqlParameterSetter {
 *
 *     private final ObjectMapper objectMapper;
 *     private final OrmJsonContainers ormJsonContainers;
 *
 *     public JacksonSqlParameterSetter(ObjectMapper objectMapper, OrmJsonContainers ormJsonContainers) {
 *         this.objectMapper = objectMapper;
 *         this.ormJsonContainers = ormJsonContainers;
 *     }
 *
 *     {@literal @}Override
 *     public boolean test(PreparedStatement stmt, int parameterIndex, Object parameter) {
 *         return parameter != null && ormJsonContainers.isOrmJsonContainer(parameter.getClass());
 *     }
 *
 *     {@literal @}Override
 *     public void setParameter(PreparedStatement stmt, int parameterIndex, Object parameter) throws SQLException {
 *         try {
 *             stmt.setBytes(parameterIndex, objectMapper.writeValueAsBytes(parameter));
 *         } catch (IOException e) {
 *             throw new SQLException("Failed to serialize JSON parameter", e);
 *         }
 *     }
 * }
 * </code></pre>
 */
public interface SqlParameterSetter {

  /**
   * Determines whether this setter can handle the given parameter.
   *
   * @param stmt the {@link PreparedStatement} where the parameter will be set
   * @param parameterIndex the index of the parameter in the SQL statement (starting from 1)
   * @param parameter the Java object representing the parameter value
   * @return {@code true} if this setter can handle the parameter, otherwise {@code false}
   * @throws SQLException if a database access error occurs
   */
  boolean test(PreparedStatement stmt, int parameterIndex, Object parameter) throws SQLException;

  /**
   * Sets the specified parameter in the given {@link PreparedStatement}.
   *
   * <p>This method is responsible for converting the provided Java object to an appropriate SQL
   * type and binding it to the given parameter index in the prepared statement.
   *
   * @param stmt the {@link PreparedStatement} where the parameter will be set
   * @param parameterIndex the index of the parameter in the SQL statement (starting from 1)
   * @param parameter the Java object representing the parameter value
   * @throws SQLException if a database access error occurs
   */
  void setParameter(PreparedStatement stmt, int parameterIndex, Object parameter)
      throws SQLException;
}
