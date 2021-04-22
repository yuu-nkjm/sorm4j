package org.nkjmlab.sorm4j.sql;

public interface ParameterizedSqlParser {
  /**
   * Convert {@link ParameterizedSql} objects.
   *
   * @return
   */
  ParameterizedSql parse();

}
