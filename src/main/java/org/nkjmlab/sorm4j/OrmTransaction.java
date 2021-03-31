package org.nkjmlab.sorm4j;

/**
 * An transaction with object relation mapping.
 *
 * @author nkjm
 *
 */
public interface OrmTransaction extends OrmConnection {

  /**
   * {@link #rollback()} is called before closing the connection.
   */
  @Override
  void close();

  @Override
  <T> TypedOrmTransaction<T> type(Class<T> objectClass);

}
