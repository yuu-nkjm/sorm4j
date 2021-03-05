package org.nkjmlab.sorm4j;

import java.sql.Connection;
import org.nkjmlab.sorm4j.mapping.OrmConfigStore;


public interface Sorm {

  OrmConnection beginTransaction();

  <T> TypedOrmConnection<T> beginTransaction(Class<T> objectClass);

  <T> TypedOrmConnection<T> beginTransaction(Class<T> objectClass, int isolationLevel);

  OrmConnection beginTransaction(int isolationLevel);

  <T, R> R execute(Class<T> objectClass, OrmFunctionHandler<TypedOrmConnection<T>, R> handler);

  <R> R execute(OrmFunctionHandler<OrmConnection, R> handler);

  <T, R> R executeTransaction(Class<T> objectClass,
      OrmFunctionHandler<TypedOrmConnection<T>, R> handler);

  <T, R> R executeTransaction(Class<T> objectClass, int isolationLevel,
      OrmFunctionHandler<TypedOrmConnection<T>, R> handler);

  <R> R executeTransaction(int isolationLevel, OrmFunctionHandler<OrmConnection, R> handler);

  <R> R executeTransaction(OrmFunctionHandler<OrmConnection, R> handler);

  <R> R executeWithJdbcConnection(OrmFunctionHandler<Connection, R> handler);

  OrmConfigStore getConfigStore();

  OrmConnection getConnection();

  <T> TypedOrmConnection<T> getConnection(Class<T> objectClass);

  ConnectionSource getConnectionSource();

  Connection getJdbcConnection();

  <T> void run(Class<T> objectClass, OrmConsumerHandler<TypedOrmConnection<T>> handler);

  void run(OrmConsumerHandler<OrmConnection> handler);

  <T> void runTransaction(Class<T> objectClass, OrmConsumerHandler<TypedOrmConnection<T>> handler);

  <T> void runTransaction(Class<T> objectClass, int isolationLevel,
      OrmConsumerHandler<TypedOrmConnection<T>> handler);

  void runTransaction(OrmConsumerHandler<OrmConnection> handler);

  void runTransaction(int isolationLevel, OrmConsumerHandler<OrmConnection> handler);

  void runWithJdbcConnection(OrmConsumerHandler<Connection> handler);

  @FunctionalInterface
  public interface OrmConsumerHandler<T> {

    void accept(T t) throws Throwable;

  }

  @FunctionalInterface
  public interface OrmFunctionHandler<T, R> {

    R apply(T t) throws Throwable;

  }


}
