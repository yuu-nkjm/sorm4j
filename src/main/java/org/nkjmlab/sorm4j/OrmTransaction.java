package org.nkjmlab.sorm4j;

public interface OrmTransaction extends OrmConnection {

  /**
   * ALWAYS rollback before closing the connection. If everything is successful, the rollback will
   * have no effect.
   */
  @Override
  void close();

}
