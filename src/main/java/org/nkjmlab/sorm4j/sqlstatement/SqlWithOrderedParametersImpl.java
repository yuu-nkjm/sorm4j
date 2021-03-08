package org.nkjmlab.sorm4j.sqlstatement;

import static org.nkjmlab.sorm4j.sqlstatement.SqlStatement.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SQL with ordered parameters. The instance could be convert to {@link SqlStatement}. The class
 * could treat {@link List} parameter.
 *
 * @author nkjm
 *
 */
public final class SqlWithOrderedParametersImpl implements SqlWithOrderedParameters {

  private final String sql;
  private final List<Object> parameters = new ArrayList<>();

  SqlWithOrderedParametersImpl(String sql) {
    this.sql = sql;
  }

  @Override
  public SqlWithOrderedParameters add(Object... parameters) {
    Arrays.asList(parameters).forEach(v -> add(v));
    return this;
  }

  public SqlWithOrderedParameters add(Object parameter) {
    if (parameter instanceof List) {
      this.parameters.add(literal(parameter));
    } else {
      this.parameters.add(parameter);
    }
    return this;
  }

  @Override
  public SqlStatement toSqlStatement() {
    return SqlStatement.of(sql, parameters.toArray());
  }



}
