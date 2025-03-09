package org.nkjmlab.sorm4j.sql.parameterize;

import java.util.Map;

/**
 * Represents an SQL statement with ordered parameters. This interface provides methods to retrieve
 * the SQL statement, its associated parameters, and a version of the SQL statement with parameters
 * inlined. It also provides factory methods to create instances of {@link ParameterizedSql}.
 *
 * <p>Instances of this interface are immutable and allow safe handling of SQL statements with
 * parameterized placeholders.
 *
 * @author nkjm
 */
public interface ParameterizedSql {

  /**
   * Returns the SQL statement with parameter placeholders (e.g., {@code ?}).
   *
   * @return the SQL statement with placeholders
   */
  String getSql();

  /**
   * Returns the ordered parameters corresponding to the placeholders in the SQL statement.
   *
   * @return an array of parameters in the order they appear in the SQL statement
   */
  Object[] getParameters();

  /**
   * Returns the SQL statement with parameters inlined. This method replaces parameter placeholders
   * (e.g., {@code ?}) with their corresponding values, producing an executable SQL string that can
   * be logged or used for debugging.
   *
   * <p>Note: The returned SQL string is not necessarily safe for direct execution, as it may
   * include raw parameter values that could be vulnerable to SQL injection if improperly used.
   * Always use parameterized queries for execution.
   *
   * @return the SQL statement with parameters embedded as literal values
   */
  String getExecutableSql();

  /**
   * Creates a {@link ParameterizedSql} instance from the given SQL string and ordered parameters.
   * The provided parameters must be simple ordered parameters without using special placeholders
   * such as named parameters, list parameters, or embedded parameters.
   *
   * @param sql the SQL statement containing placeholders
   * @param parameters the ordered parameters corresponding to the placeholders in the SQL statement
   * @return a {@link ParameterizedSql} instance containing the provided SQL statement and
   *     parameters
   */
  static ParameterizedSql of(String sql, Object... parameters) {
    return ParameterizedSqlFactory.create(sql, parameters);
  }

  static ParameterizedSql of(String sql, Map<String, Object> parameters) {
    return ParameterizedSqlFactory.create(sql, parameters);
  }
}
