package org.nkjmlab.sorm4j;

import java.sql.Connection;
import org.nkjmlab.sorm4j.mapping.ConfigStore;

/**
 * An interface of executing object-relation mapping.
 *
 * @author nkjm
 *
 */
public interface Sorm {

  /**
   * Interface for object-relation handling without a return value.
   *
   * @param <T>
   */
  @FunctionalInterface
  public interface ConsumerHandler<T> {

    void accept(T t) throws Exception;

  }

  /**
   * Interface for object-relation handling with a return value.
   *
   * @param <T>
   */
  @FunctionalInterface
  public interface FunctionHandler<T, R> {

    R apply(T t) throws Exception;

  }

  <T> void accept(Class<T> objectClass, ConsumerHandler<TypedOrmConnection<T>> handler);

  /**
   * Accepts a {@link OrmConnection} handler for a task with object-relation mapping. The connection
   * will be closed after the process of handler.
   *
   * @param handler
   */
  void accept(ConsumerHandler<OrmConnection> handler);

  /**
   * Accepts a {@link Connection} handler for a task with object-relation mapping. The connection
   * will be closed after the process of handler.
   *
   * @param handler
   */
  void acceptJdbcConnectionHandler(ConsumerHandler<Connection> handler);

  /**
   * Accepts a {@link TypedOrmTransaction} handler for a task with object-relation mapping. The
   * transaction will be closed after the process of handler. When the transaction is not committed,
   * the transaction will be rollback.
   *
   * @param <T>
   * @param objectClass
   * @param handler
   */
  <T> void acceptTransactionHandler(Class<T> objectClass,
      ConsumerHandler<TypedOrmTransaction<T>> handler);

  void acceptTransactionHandler(ConsumerHandler<OrmTransaction> handler);

  <T, R> R apply(Class<T> objectClass, FunctionHandler<TypedOrmConnection<T>, R> handler);

  <R> R apply(FunctionHandler<OrmConnection, R> handler);

  <R> R applyJdbcConnectionHandler(FunctionHandler<Connection, R> handler);

  <T, R> R applyTransactionHandler(Class<T> objectClass,
      FunctionHandler<TypedOrmTransaction<T>, R> handler);

  <R> R applyTransactionHandler(FunctionHandler<OrmTransaction, R> handler);

  /**
   * Creates a Sorm object with the given config name. The config name should be registered by
   * {@link SormFactory#registerNewConfigStore(String, java.util.function.Function)} or
   * {@link SormFactory#registerNewModifiedConfigStore(String, java.util.function.Function)}
   *
   * @param configName
   * @return
   */
  Sorm createWith(String configName);

  ConfigStore getConfigStore();

  ConnectionSource getConnectionSource();

  Connection getJdbcConnection();

  OrmConnection openConnection();

  <T> TypedOrmConnection<T> openConnection(Class<T> objectClass);

  /**
   * Open transaction. Default transaction level is
   * {@link ConfigStoreBuilder#DEFAULT_TRANSACTION_ISOLATION_LEVEL}.
   *
   * @return
   */
  OrmTransaction openTransaction();

  /**
   * Open transaction. Default transaction level is
   * {@link ConfigStoreBuilder#DEFAULT_TRANSACTION_ISOLATION_LEVEL}.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  <T> TypedOrmTransaction<T> openTransaction(Class<T> objectClass);



}
