package org.nkjmlab.sorm4j;

/**
 * An transaction with object relation mapping.
 *
 * @author nkjm
 *
 */
public interface OrmTransaction extends OrmConnection {

  /**
   * Closes the {@link java.sql.Connection} associated with this instance.
   *
   * <p>
   * <strong>Note:</strong> If you do not explicitly commit in a opened transaction, it will be
   * rolled back.
   *
   * @see java.sql.Connection#close()
   *
   */
  @Override
  void close();

}
