package org.nkjmlab.sorm4j;

import java.sql.Connection;

/**
 * Main API for object relation mapping.
 *
 * @author nkjm
 *
 */
public interface OrmConnection extends Orm, AutoCloseable {

  /**
   * Gets {@link Connection}.
   *
   * @return
   */
  Connection getJdbcConnection();

  /**
   * Begin transaction. The isolation level is corresponding to
   * {@link Sorm.Builder#setTransactionIsolationLevel(int)}.
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
  @Override
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
