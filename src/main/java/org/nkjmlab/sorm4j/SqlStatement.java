package org.nkjmlab.sorm4j;

import java.util.Arrays;

/**
 * This class represents a sql statement with ordered parameters.
 *
 * @author nkjm
 *
 */
public class SqlStatement {

  // with ? placeholder
  private final String sql;
  // ordered parameters
  private final Object[] parameters;

  public SqlStatement(String sql, Object... parameters) {
    this.sql = sql;
    this.parameters = parameters;
  }

  @Override
  public String toString() {
    return " [" + sql + "]"
        + ((parameters != null && parameters.length != 0)
            ? " with [" + Arrays.toString(parameters) + "]"
            : "");
  }

  public final String getSql() {
    return sql;
  }

  public final Object[] getParameters() {
    return parameters;
  }


}
