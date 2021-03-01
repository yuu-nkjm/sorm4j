package org.nkjmlab.sorm4j;

public interface TransactionFunction {

  /**
   * Closes the {@link java.sql.Connection Connection} associated with this instance.
   *
   * @see java.sql.Connection#close()
   * @since 1.0
   */
  void close();

  /**
   * Commits the {@link java.sql.Connection Connection} associated with this instance.
   *
   * @see java.sql.Connection#commit()
   * @since 1.0
   */
  void commit();

  /**
   * Rollback the {@link java.sql.Connection Connection} associated with this instance.
   *
   * @see java.sql.Connection#rollback()
   * @since 1.0
   */
  void rollback();

  /**
   * Sets the auto commit behavior for the {@link java.sql.Connection Connection} associated with
   * this instance.
   *
   * @see java.sql.Connection#setAutoCommit(boolean)
   * @since 1.0
   */
  void setAutoCommit(boolean autoCommit);

  void begin(int isolationLevel);

  void begin();


}
