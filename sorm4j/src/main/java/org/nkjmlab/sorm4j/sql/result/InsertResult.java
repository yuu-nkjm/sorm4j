package org.nkjmlab.sorm4j.sql.result;

import org.nkjmlab.sorm4j.common.container.RowMap;

/** Represents a result from an insert operation with the auto-generated keys. */
public interface InsertResult {

  /**
   * Returns the row count by executing a insert statement {@link
   * java.sql.PreparedStatement#executeUpdate()}.
   */
  int[] getRowsModified();

  /**
   * Returns count of rows modified.
   *
   * @return
   */
  int countRowsModified();

  /**
   * Returns the auto-generated keys.
   *
   * @return
   */
  RowMap getGeneratedKeys();
}
