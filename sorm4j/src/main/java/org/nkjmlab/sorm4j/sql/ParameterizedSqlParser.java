package org.nkjmlab.sorm4j.sql;

public interface ParameterizedSqlParser {
  /**
   * Parse to {@link ParameterizedSql} objects.
   *
   * @return
   */
  ParameterizedSql parse();

}
