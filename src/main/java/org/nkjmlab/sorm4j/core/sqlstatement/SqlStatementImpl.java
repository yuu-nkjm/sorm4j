package org.nkjmlab.sorm4j.core.sqlstatement;

import java.util.Arrays;
import org.nkjmlab.sorm4j.sql.SqlStatement;

/**
 * This class represents a sql statement with ordered parameters.
 *
 * @author nkjm
 *
 */
public final class SqlStatementImpl implements SqlStatement {

  // with ? placeholder
  private final String sql;
  // ordered parameters
  private final Object[] parameters;

  public SqlStatementImpl(String sql, Object... parameters) {
    this.sql = sql;
    this.parameters = parameters;
  }


  @Override
  public String toString() {
    return "sql=[" + sql + "]" + ((parameters == null || parameters.length == 0) ? ""
        : ", parameters=" + Arrays.toString(parameters) + "");
  }

  @Override
  public final String getSql() {
    return sql;
  }

  @Override
  public final Object[] getParameters() {
    return parameters;
  }


}
