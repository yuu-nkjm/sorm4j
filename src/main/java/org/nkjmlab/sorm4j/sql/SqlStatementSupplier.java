package org.nkjmlab.sorm4j.sql;

public interface SqlStatementSupplier {
  /**
   * Convert {@link SqlStatement} objects.
   *
   * @return
   */
  SqlStatement toSqlStatement();

}
