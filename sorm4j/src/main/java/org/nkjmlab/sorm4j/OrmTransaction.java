package org.nkjmlab.sorm4j;

/**
 * An transaction with object relation mapping.
 *
 * @author nkjm
 *
 */
public interface OrmTransaction extends OrmConnection {

  /**
   * Closes the {@link java.sql.Connection Connection} associated with this instance.
   * {@link OrmConnection#rollback()} is called before closing the connection.
   *
   * @see java.sql.Connection#close()
   *
   */
  @Override
  void close();

}
