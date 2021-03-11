package org.nkjmlab.sorm4j;

import java.sql.Connection;
import org.nkjmlab.sorm4j.mapping.OrmConfigStore;

/**
 * An interface of executing object-relation mapping.
 *
 * @author nkjm
 *
 */
public interface Sorm {

  OrmConnection beginTransaction();

  <T> TypedOrmTransaction<T> beginTransaction(Class<T> objectClass);

  <T> TypedOrmTransaction<T> beginTransaction(Class<T> objectClass, int isolationLevel);

  OrmTransaction beginTransaction(int isolationLevel);

  <T, R> R applyAndGet(Class<T> objectClass, OrmFunctionHandler<TypedOrmConnection<T>, R> handler);

  <R> R applyAndGet(OrmFunctionHandler<OrmConnection, R> handler);

  <T, R> R applyTransactionAndGet(Class<T> objectClass,
      OrmFunctionHandler<TypedOrmTransaction<T>, R> handler);

  <T, R> R applyTransactionAndGet(Class<T> objectClass, int isolationLevel,
      OrmFunctionHandler<TypedOrmTransaction<T>, R> handler);

  <R> R applyTransactionAndGet(int isolationLevel, OrmFunctionHandler<OrmTransaction, R> handler);

  <R> R applyTransactionAndGet(OrmFunctionHandler<OrmTransaction, R> handler);

  <R> R applyToJdbcConnectionAndGet(OrmFunctionHandler<Connection, R> handler);

  OrmConfigStore getConfigStore();

  OrmConnection getConnection();

  <T> TypedOrmConnection<T> getConnection(Class<T> objectClass);

  ConnectionSource getConnectionSource();

  Connection getJdbcConnection();

  <T> void apply(Class<T> objectClass, OrmConsumerHandler<TypedOrmConnection<T>> handler);

  void apply(OrmConsumerHandler<OrmConnection> handler);

  /**
   * Applies transaction
   *
   * @param <T>
   * @param objectClass
   * @param handler
   */
  <T> void applyTransaction(Class<T> objectClass,
      OrmConsumerHandler<TypedOrmTransaction<T>> handler);

  <T> void applyTransaction(Class<T> objectClass, int isolationLevel,
      OrmConsumerHandler<TypedOrmTransaction<T>> handler);

  void applyTransaction(OrmConsumerHandler<OrmTransaction> handler);

  void applyTransaction(int isolationLevel, OrmConsumerHandler<OrmTransaction> handler);

  void applyToJdbcConnection(OrmConsumerHandler<Connection> handler);

  /**
   * Interface for object-relation handling without a return value.
   *
   * @param <T>
   */
  @FunctionalInterface
  public interface OrmConsumerHandler<T> {

    void accept(T t) throws Exception;

  }

  /**
   * Interface for object-relation handling with a return value.
   *
   * @param <T>
   */
  @FunctionalInterface
  public interface OrmFunctionHandler<T, R> {

    R apply(T t) throws Exception;

  }


}
