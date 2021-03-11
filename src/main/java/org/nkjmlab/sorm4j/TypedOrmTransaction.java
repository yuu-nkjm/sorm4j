package org.nkjmlab.sorm4j;

public interface TypedOrmTransaction<T> extends TypedOrmConnection<T> {


  /**
   * ALWAYS rollback before closing the connection. If everything is successful, the rollback will
   * have no effect.
   */
  @Override
  void close();
}
