package org.nkjmlab.sorm4j;

/**
 * An transaction with typed object relation mapping.
 *
 * @author nkjm
 *
 */

public interface TypedOrmTransaction<T> extends TypedOrmConnection<T> {


  /**
   * {@link #rollback()} is called before closing the connection.
   */
  @Override
  void close();

  @Override
  <S> TypedOrmTransaction<S> type(Class<S> objectClass);

  @Override
  OrmTransaction untype();
}
