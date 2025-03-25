package org.nkjmlab.sorm4j.sql.parameterize;

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
public interface ParameterizedSqlBuilder {

  /**
   * Creates a {@link ParameterizedSql} instance from a predefined SQL statement.
   *
   * @return a {@link ParameterizedSql} instance
   */
  ParameterizedSql build();

  static OrderedParameterSqlBuilder orderedParameterBuilder(String sql) {
    return OrderedParameterSqlBuilder.builder(sql);
  }

  static NamedParameterSqlBuilder namedParameterBuilder(String sql) {
    return NamedParameterSqlBuilder.builder(sql);
  }
}
