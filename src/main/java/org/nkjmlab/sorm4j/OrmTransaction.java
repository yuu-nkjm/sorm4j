package org.nkjmlab.sorm4j;

/**
 * An transaction with object relation mapping.
 *
 * @author nkjm
 *
 */
public interface OrmTransaction extends OrmConnection {

  /**
   * ALWAYS rollback before closing the connection. If everything is successful, the rollback will
   * have no effect.
   */
  @Override
  void close();

  <T> TypedOrmTransaction<T> type(Class<T> objectClass);

}
