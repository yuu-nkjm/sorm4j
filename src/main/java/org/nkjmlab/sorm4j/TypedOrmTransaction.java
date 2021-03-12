package org.nkjmlab.sorm4j;

/**
 * An transaction with typed object relation mapping.
 *
 * @author nkjm
 *
 */

public interface TypedOrmTransaction<T> extends TypedOrmConnection<T> {


  /**
   * ALWAYS rollback before closing the connection. If everything is successful, the rollback will
   * have no effect.
   */
  @Override
  void close();
}
