package org.nkjmlab.sorm4j;

import org.nkjmlab.sorm4j.mapping.ConfigStore;

/**
 * Functions of handling transaction.
 *
 * @author nkjm
 *
 */
public interface TransactionFunction {

  /**
   * Begin transaction. The isolation level is corresponding to
   * {@link ConfigStore#getTransactionIsolationLevel()}.
   */
  void begin();

  /**
   * Begins transaction with the given transaction isolation level.
   *
   * @param isolationLevel
   */

  void begin(int isolationLevel);

  /**
   * Closes the {@link java.sql.Connection Connection} associated with this instance.
   *
   * @see java.sql.Connection#close()
   *
   */
  void close();

  /**
   * Commits the {@link java.sql.Connection Connection} associated with this instance.
   *
   * @see java.sql.Connection#commit()
   *
   */
  void commit();

  /**
   * Rollback the {@link java.sql.Connection Connection} associated with this instance.
   *
   * @see java.sql.Connection#rollback()
   *
   */
  void rollback();

  /**
   * Sets the auto commit behavior for the {@link java.sql.Connection Connection} associated with
   * this instance.
   *
   * @see java.sql.Connection#setAutoCommit(boolean)
   *
   */
  void setAutoCommit(boolean autoCommit);


}
