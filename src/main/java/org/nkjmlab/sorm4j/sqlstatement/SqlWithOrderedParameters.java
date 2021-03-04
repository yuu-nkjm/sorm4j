package org.nkjmlab.sorm4j.sqlstatement;

import static org.nkjmlab.sorm4j.sqlstatement.SqlStatement.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SqlWithOrderedParameters {

  private final String sql;
  private final List<Object> parameters = new ArrayList<>();

  private SqlWithOrderedParameters(String sql) {
    this.sql = sql;
  }


  public SqlWithOrderedParameters addAll(Object... parameters) {
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

  public SqlStatement toSqlStatement() {
    return SqlStatement.of(sql, parameters.toArray());
  }

  public static SqlWithOrderedParameters from(String sql) {
    return new SqlWithOrderedParameters(sql);
  }

  public static SqlStatement toSqlStatement(String sql, Object... parameters) {
    return from(sql).addAll(parameters).toSqlStatement();
  }



}
