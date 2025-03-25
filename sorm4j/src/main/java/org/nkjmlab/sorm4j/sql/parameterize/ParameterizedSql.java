package org.nkjmlab.sorm4j.sql.parameterize;

import java.util.Map;

import org.nkjmlab.sorm4j.internal.sql.parameterize.ParameterizedSqlImpl;

/**
 * Represents an SQL statement with parameters. This interface provides methods to retrieve the SQL
 * statement, its associated parameters, and a version of the SQL statement with parameters inlined.
 * It also provides factory methods to create instances of {@link ParameterizedSql}.
 *
 * <p>Instances of this interface are immutable, ensuring safe handling of SQL statements with
 * parameterized placeholders. The parameters can be ordered or named, depending on how the instance
 * is created.
 *
 * <p>Note: This class does not execute SQL statements. It is intended for safely constructing
 * parameterized SQL statements, which should be executed using an appropriate database library.
 *
 * @author nkjm
 */
public interface ParameterizedSql {

  /**
   * Returns the SQL statement containing parameter placeholders (e.g., {@code ?} for ordered
   * parameters or {@code :name} for named parameters).
   *
   * @return the SQL statement with placeholders
   */
  String getSql();

  /**
   * Returns the ordered parameters corresponding to the placeholders in the SQL statement. If named
   * parameters were used, this method may return an empty array.
   *
   * @return an array of parameters in the order they appear in the SQL statement
   */
  Object[] getParameters();

  /**
   * Returns the SQL statement with parameters inlined. This method replaces parameter placeholders
   * (e.g., {@code ?} or {@code :name}) with their corresponding values, producing a SQL string that
   * can be logged or used for debugging.
   *
   * <p><strong>Warning:</strong> The returned SQL string is not safe for direct execution, as it
   * may contain raw parameter values. Always use parameterized queries to prevent SQL injection.
   *
   * @return the SQL statement with parameters embedded as literal values
   */
  String getExecutableSql();

  /**
   * Creates a {@link ParameterizedSql} instance with no parameters.
   *
   * @param sql the SQL statement without parameters
   * @return a {@link ParameterizedSql} instance containing the provided SQL statement
   */
  static ParameterizedSql of(String sql) {
    return ParameterizedSqlImpl.of(sql);
  }

  /**
   * Creates a {@link ParameterizedSql} instance from the given SQL string and ordered parameters.
   * The parameters must be simple ordered values corresponding to the placeholders in the SQL
   * statement. This method does not support named parameters.
   *
   * <p><strong>Example usage:</strong>
   *
   * <pre><code>
   * String sql = "SELECT * FROM users WHERE id = ? AND age > ?";
   * ParameterizedSql statement = ParameterizedSql.withOrderedParameters(sql, 1, 18);
   * </code></pre>
   *
   * @param sql the SQL statement containing {@code ?} placeholders
   * @param parameters the ordered parameters corresponding to the placeholders in the SQL statement
   * @return a {@link ParameterizedSql} instance with the provided SQL statement and parameters
   */
  static ParameterizedSql withOrderedParameters(String sql, Object... parameters) {
    return OrderedParameterSqlBuilder.builder(sql).addParameters(parameters).build();
  }

  /**
   * Creates a {@link ParameterizedSql} instance from the given SQL string and named parameters.
   * Named parameters are specified using {@code :name} placeholders in the SQL statement and mapped
   * to corresponding values via a {@code Map}.
   *
   * <p><strong>Example usage:</strong>
   *
   * <pre><code>
   * String sql = "SELECT * FROM users WHERE id = :id AND city = :city";
   * Map<String, Object> params = Map.of("id", 1, "city", "Tokyo");
   * ParameterizedSql statement = ParameterizedSql.withNamedParameters(sql, params);
   * </code></pre>
   *
   * @param sql the SQL statement containing named placeholders (e.g., {@code :name})
   * @param parameters a map of parameter names to their corresponding values
   * @return a {@link ParameterizedSql} instance with the provided SQL statement and parameters
   */
  static ParameterizedSql withNamedParameters(String sql, Map<String, Object> parameters) {
    return NamedParameterSqlBuilder.builder(sql).bindParameters(parameters).build();
  }

  /**
   * Creates a {@link ParameterizedSql} instance from the given SQL string and a parameter container
   * object. The object's field names will be mapped to named parameters in the SQL statement.
   *
   * <p>This method automatically extracts values from the given object using reflection, mapping
   * field names to parameter names. The behavior follows the mapping rules defined in {@link
   * NamedParameterSqlBuilder#bindParameters(Object)}.
   *
   * <p><strong>Example usage:</strong>
   *
   * <pre><code>
   * class User {
   *     public final int id = 1;
   *     public final String city = "Osaka";
   * }
   *
   * String sql = "SELECT * FROM users WHERE id = :id AND city = :city";
   * ParameterizedSql statement = ParameterizedSql.withNamedParameters(sql, new User());
   * </code></pre>
   *
   * @param sql the SQL statement containing named placeholders (e.g., {@code :name})
   * @param parametersContainer an object whose field names map to parameter names
   * @return a {@link ParameterizedSql} instance with the provided SQL statement and parameters
   */
  static ParameterizedSql withParametersContainer(String sql, Object parametersContainer) {
    return NamedParameterSqlBuilder.builder(sql).bindParameters(parametersContainer).build();
  }
}
