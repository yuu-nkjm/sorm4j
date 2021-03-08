package org.nkjmlab.sorm4j.sqlstatement;

public interface SqlWithOrderedParameters {


  SqlWithOrderedParameters add(Object parameter);

  SqlWithOrderedParameters add(Object... parameters);

  SqlStatement toSqlStatement();

  static SqlStatement toSqlStatement(String sql, Object... parameters) {
    return from(sql).add(parameters).toSqlStatement();
  }

  static SqlWithOrderedParameters from(String sql) {
    return new SqlWithOrderedParametersImpl(sql);
  }

}
