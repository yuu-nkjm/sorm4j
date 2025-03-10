package org.nkjmlab.sorm4j.sql.parameterize;

import java.util.Map;

import org.nkjmlab.sorm4j.internal.context.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.internal.util.sql.binding.NamedParameterSqlParserImpl;

/**
 * A builder interface for constructing SQL statements with named parameters.
 *
 * <p>This interface allows dynamic binding of named parameters in an SQL statement. It supports
 * multiple ways to bind parameters, including individual key-value pairs, a map of key-value pairs,
 * and a Java object whose field names are mapped to parameter names.
 *
 * <p><strong>Example usage:</strong>
 *
 * <pre><code>
 * String sql = "SELECT * FROM customer WHERE id = :id AND address = :address";
 *
 * // Using a map to bind parameters
 * ParameterizedSql statement = NamedParameterSqlBuilder.builder(sql)
 *     .bindParameters(Map.of("id", 1, "address", "Kyoto"))
 *     .build();
 *
 * // Using individual key-value bindings
 * ParameterizedSql statement2 = NamedParameterSqlBuilder.builder(sql)
 *     .bindParameter("id", 1)
 *     .bindParameter("address", "Kyoto")
 *     .build();
 *
 * // Using a Java object for automatic parameter mapping
 * class CustomerParams {
 *     public final int id = 1;
 *     public final String address = "Kyoto";
 * }
 *
 * ParameterizedSql statement3 = NamedParameterSqlBuilder.builder(sql)
 *     .bindParameters(new CustomerParams())
 *     .build();
 *
 * List<Customer> customers = sorm.readList(Customer.class, statement);
 * </code></pre>
 *
 * @author yuu_nkjm
 */
public interface NamedParameterSqlBuilder extends ParameterizedSqlBuilder {

  /**
   * Binds multiple named parameters to the SQL statement using a map.
   *
   * <p>The map keys represent parameter names in the SQL statement (excluding the {@code :}
   * prefix), and the corresponding values are bound to those placeholders.
   *
   * <p><strong>Example:</strong>
   *
   * <pre><code>
   * String sql = "SELECT * FROM users WHERE id = :id AND name = :name";
   * ParameterizedSql statement = NamedParameterSqlBuilder.builder(sql)
   *     .bindParameters(Map.of("id", 1, "name", "Alice"))
   *     .build();
   * </code></pre>
   *
   * @param keyValuePairOfNamedParameters a map of parameter names to their corresponding values
   * @return this builder instance for method chaining
   */
  NamedParameterSqlBuilder bindParameters(Map<String, Object> keyValuePairOfNamedParameters);

  /**
   * Binds a single named parameter to the SQL statement.
   *
   * <p>The provided key corresponds to a named placeholder in the SQL statement (excluding the
   * {@code :} prefix), and the value is bound to that placeholder.
   *
   * <p><strong>Example:</strong>
   *
   * <pre><code>
   * String sql = "SELECT * FROM users WHERE id = :id";
   * ParameterizedSql statement = NamedParameterSqlBuilder.builder(sql)
   *     .bindParameter("id", 1)
   *     .build();
   * </code></pre>
   *
   * @param key the name of the parameter (without the {@code :} prefix)
   * @param value the value to bind to the parameter
   * @return this builder instance for method chaining
   */
  NamedParameterSqlBuilder bindParameter(String key, Object value);

  /**
   * Binds named parameters from an object. The object's field names are mapped to parameter names
   * in the SQL statement.
   *
   * <p>This method uses {@link ColumnToFieldAccessorMapper} to map the object's fields to SQL
   * parameter names. Only public fields and getter methods are considered.
   *
   * <p><strong>Example:</strong>
   *
   * <pre><code>
   * class User {
   *     public final int id = 1;
   *     public final String name = "Alice";
   * }
   *
   * String sql = "SELECT * FROM users WHERE id = :id AND name = :name";
   * ParameterizedSql statement = NamedParameterSqlBuilder.builder(sql)
   *     .bindParameters(new User())
   *     .build();
   * </code></pre>
   *
   * @param parametersContainer an object whose field names map to named parameters in the SQL
   *     statement
   * @return this builder instance for method chaining
   */
  NamedParameterSqlBuilder bindParameters(Object parametersContainer);

  /**
   * Creates a new instance of {@link NamedParameterSqlBuilder} for constructing SQL statements with
   * named parameters.
   *
   * <p><strong>Example:</strong>
   *
   * <pre><code>
   * String sql = "SELECT * FROM users WHERE id = :id";
   * NamedParameterSqlBuilder builder = NamedParameterSqlBuilder.builder(sql);
   * </code></pre>
   *
   * @param sql the SQL statement containing named placeholders (e.g., {@code :name})
   * @return a new {@link NamedParameterSqlBuilder} instance
   */
  static NamedParameterSqlBuilder builder(String sql) {
    return new NamedParameterSqlParserImpl(sql);
  }
}
