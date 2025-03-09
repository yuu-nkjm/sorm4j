package org.nkjmlab.sorm4j.sql.parameterize;

import java.util.Map;

/**
 * A factory interface for creating {@link ParameterizedSql} instances. This interface provides
 * methods to generate SQL statements with ordered or named parameters, including special parameter
 * handling (e.g., list parameters and embedded parameters).
 *
 * <p>Implementations of this factory allow dynamic SQL construction by binding parameters in a
 * structured manner.
 *
 * @author nkjm
 */
public interface ParameterizedSqlFactory {

  /**
   * Creates a {@link ParameterizedSql} instance from a predefined SQL statement.
   *
   * @return a {@link ParameterizedSql} instance
   */
  ParameterizedSql create();

  /**
   * Parses the given SQL statement and ordered parameters to create a {@link ParameterizedSql}
   * instance. The provided parameters may include special parameter types such as list parameters
   * and embedded parameters.
   *
   * @param sql the SQL statement containing placeholders (e.g., {@code ?})
   * @param parameters ordered parameters corresponding to the placeholders in the SQL statement
   * @return a {@link ParameterizedSql} instance containing the parsed SQL and its parameters
   */
  static ParameterizedSql create(String sql, Object... parameters) {
    return OrderedParameterSqlFactory.create(sql, parameters);
  }

  /**
   * Parses the given SQL statement and named parameters to create a {@link ParameterizedSql}
   * instance. Named parameters are mapped to their corresponding values, which may include special
   * parameter types such as list parameters and embedded parameters.
   *
   * @param sql the SQL statement containing named placeholders (e.g., {@code :name})
   * @param parameters a map of parameter names to their corresponding values
   * @return a {@link ParameterizedSql} instance containing the parsed SQL and its parameters
   */
  static ParameterizedSql create(String sql, Map<String, Object> parameters) {
    return NamedParameterSqlFactory.create(sql, parameters);
  }

  /**
   * Creates an {@link OrderedParameterSqlFactory} instance for the given SQL statement. This
   * factory enables the creation of parameterized SQL using ordered parameters.
   *
   * @param sql the SQL statement containing placeholders (e.g., {@code ?})
   * @return an instance of {@link OrderedParameterSqlFactory}
   */
  static OrderedParameterSqlFactory createOrderedParameterSqlFactory(String sql) {
    return OrderedParameterSqlFactory.of(sql);
  }

  /**
   * Creates a {@link NamedParameterSqlFactory} instance for the given SQL statement. This factory
   * enables the creation of parameterized SQL using named parameters.
   *
   * @param sql the SQL statement containing named placeholders (e.g., {@code :name})
   * @return an instance of {@link NamedParameterSqlFactory}
   */
  static NamedParameterSqlFactory createNamedParameterSqlFactory(String sql) {
    return NamedParameterSqlFactory.of(sql);
  }
}
