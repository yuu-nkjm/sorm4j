package org.nkjmlab.sorm4j;

import java.sql.Connection;
import org.nkjmlab.sorm4j.core.mapping.ConfigStore;

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
   * This interface is only designed for {@link Sorm} interface.
   *
   * @param <T>
   */
  @FunctionalInterface
  interface ConsumerHandler<T> {
    /**
     * Will be invoked with an open connection. The handle may be closed when this callback returns.
     *
     * @param t
     * @throws Exception
     */
    void accept(T t) throws Exception;
  }

  /**
   * Interface for object-relation handling with a return value.
   *
   * This interface is only designed for {@link Sorm} interface.
   *
   * @param <T>
   */
  @FunctionalInterface
  interface FunctionHandler<T, R> {

    /**
     * Will be invoked with an open connection. The handle may be closed when this callback returns.
     *
     * @param t
     * @return
     * @throws Exception
     */
    R apply(T t) throws Exception;
  }

  /**
   * Accepts a {@link TypedOrmConnection} handler for a task with object-relation mapping. The
   * connection will be closed after the process of handler.
   *
   * @param <T>
   * @param objectClass
   * @param handler
   */
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

  /**
   * Accepts a {@link OrmTransaction} handler for a task with object-relation mapping. The
   * connection will be closed after the process of handler.
   *
   * @param handler
   */
  void acceptTransactionHandler(ConsumerHandler<OrmTransaction> handler);

  /**
   * Applies a {@link TypedOrmConnection} handler for a task with object-relation mapping and gets
   * the result. The connection will be closed after the process of handler.
   *
   * @param <T>
   * @param <R>
   * @param objectClass
   * @param handler
   * @return
   */
  <T, R> R apply(Class<T> objectClass, FunctionHandler<TypedOrmConnection<T>, R> handler);

  /**
   * Applies a {@link OrmConnection} handler for a task with object-relation mapping and gets the
   * result. The connection will be closed after the process of handler.
   *
   * @param <R>
   * @param handler
   * @return
   */
  <R> R apply(FunctionHandler<OrmConnection, R> handler);

  /**
   * Applies a {@link Connection} handler for a task with object-relation mapping and gets the
   * result. The connection will be closed after the process of handler.
   *
   * @param <R>
   * @param handler
   * @return
   */
  <R> R applyJdbcConnectionHandler(FunctionHandler<Connection, R> handler);

  /**
   * Applies a {@link TypedOrmTransaction} handler for a task with object-relation mapping and gets
   * the result. The connection will be closed after the process of handler.
   *
   * @param <T>
   * @param <R>
   * @param objectClass
   * @param handler
   * @return
   */
  <T, R> R applyTransactionHandler(Class<T> objectClass,
      FunctionHandler<TypedOrmTransaction<T>, R> handler);

  /**
   * Applies a {@link OrmTransaction} handler for a task with object-relation mapping and gets the
   * result. The connection will be closed after the process of handler.
   *
   * @param <R>
   * @param handler
   * @return
   */
  <R> R applyTransactionHandler(FunctionHandler<OrmTransaction, R> handler);

  /**
   * Creates a Sorm object with the given config name. The config name should be registered by
   * {@link SormFactory#registerConfig(String, java.util.function.Consumer)} or
   * {@link SormFactory#registerModifiedConfig(String, Sorm, java.util.function.Consumer)}
   *
   * @param configName
   * @return
   */
  Sorm createWith(String configName);

  /**
   * (non-public API) Get the config store of this object. It is only used for Sorm4j framework.
   *
   * @return
   */
  ConfigStore getConfigStore();


  /**
   * Gets {@link ConnectionSource}.
   *
   * @return
   */
  ConnectionSource getConnectionSource();

  /**
   * Gets JDBC {@link Connection}.
   *
   * @return
   */
  Connection getJdbcConnection();

  /**
   * Open {@link OrmConnection}. You should always use try-with-resources to ensure the database
   * connection is released. We recommend using {@link #accept(ConsumerHandler)} or
   * {@link #apply(FunctionHandler)} .
   *
   * @return
   */
  OrmConnection openConnection();

  /**
   * Open {@link TypedOrmConnection}. You should always use try-with-resources to ensure the
   * database connection is released. We recommend using {@link #accept(Class, ConsumerHandler)} or
   * {@link #apply(Class, FunctionHandler)} .
   *
   * @return
   */
  <T> TypedOrmConnection<T> openConnection(Class<T> objectClass);

  /**
   * Open {@link OrmTransaction}. You should always use try-with-resources to ensure the database
   * connection is released. We recommend using {@link #acceptTransactionHandler(ConsumerHandler)}
   * or {@link #applyTransactionHandler(FunctionHandler)}. Default transaction level is
   * {@link Connection#TRANSACTION_READ_COMMITTED}.
   *
   * @return
   */
  OrmTransaction openTransaction();

  /**
   * Open {@link TypedOrmTransaction}. You should always use try-with-resources to ensure the
   * database connection is released. We recommend using
   * {@link #acceptTransactionHandler(Class, ConsumerHandler)} or
   * {@link #applyTransactionHandler(Class, FunctionHandler)}. Default transaction level is
   * {@link Connection#TRANSACTION_READ_COMMITTED}.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  <T> TypedOrmTransaction<T> openTransaction(Class<T> objectClass);


}
