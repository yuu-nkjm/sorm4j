package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.mapping.OrmConfigStore.*;
import java.sql.Connection;
import java.sql.SQLException;
import org.nkjmlab.sorm4j.ConnectionSource;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormFactory;
import org.nkjmlab.sorm4j.TypedOrmConnection;

public final class SormImpl implements Sorm {
  // private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  private final ConnectionSource connectionSource;

  private final OrmConfigStore configStore;


  public SormImpl(ConnectionSource connectionSource, OrmConfigStore configs) {
    this.configStore = configs;
    this.connectionSource = connectionSource;
  }

  @Override
  public OrmConnection beginTransaction() {
    return new OrmTransaction(getJdbcConnection(), configStore, DEFAULT_ISOLATION_LEVEL);
  }

  @Override
  public <T> TypedOrmConnection<T> beginTransaction(Class<T> objectClass) {
    return new TypedOrmTransaction<T>(objectClass, getJdbcConnection(), configStore,
        DEFAULT_ISOLATION_LEVEL);
  }

  @Override
  public <T> TypedOrmConnection<T> beginTransaction(Class<T> objectClass, int isolationLevel) {
    return new TypedOrmTransaction<T>(objectClass, getJdbcConnection(), configStore,
        isolationLevel);
  }

  @Override
  public OrmConnection beginTransaction(int isolationLevel) {
    return new OrmTransaction(getJdbcConnection(), configStore, isolationLevel);
  }

  @Override
  public <T, R> R execute(Class<T> objectClass,
      OrmFunctionHandler<TypedOrmConnection<T>, R> handler) {
    try (TypedOrmConnection<T> conn = getConnection(objectClass)) {
      return handler.apply(conn);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  @Override
  public <R> R execute(OrmFunctionHandler<OrmConnection, R> handler) {
    try (OrmConnection conn = getConnection()) {
      return handler.apply(conn);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  @Override
  public <T, R> R executeTransaction(Class<T> objectClass,
      OrmFunctionHandler<TypedOrmConnection<T>, R> handler) {
    try (TypedOrmConnection<T> transaction = beginTransaction(objectClass)) {
      return handler.apply(transaction);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }



  @Override
  public <T, R> R executeTransaction(Class<T> objectClass, int isolationLevel,
      OrmFunctionHandler<TypedOrmConnection<T>, R> handler) {
    try (TypedOrmConnection<T> transaction = beginTransaction(objectClass, isolationLevel)) {
      return handler.apply(transaction);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  @Override
  public <R> R executeTransaction(int isolationLevel,
      OrmFunctionHandler<OrmConnection, R> handler) {
    try (OrmConnection transaction = beginTransaction()) {
      return handler.apply(transaction);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }


  @Override
  public <R> R executeTransaction(OrmFunctionHandler<OrmConnection, R> handler) {
    try (OrmConnection transaction = beginTransaction()) {
      return handler.apply(transaction);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  @Override
  public <R> R executeWithJdbcConnection(OrmFunctionHandler<Connection, R> handler) {
    try (Connection conn = getJdbcConnection()) {
      return handler.apply(conn);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  @Override
  public OrmConfigStore getConfigStore() {
    return this.configStore;
  }


  @Override
  public OrmConnection getConnection() {
    return SormFactory.getOrmConnection(getJdbcConnection(), configStore);
  }

  @Override
  public <T> TypedOrmConnection<T> getConnection(Class<T> objectClass) {
    return SormFactory.getTypedOrmConnection(getJdbcConnection(), objectClass, configStore);
  }

  @Override
  public ConnectionSource getConnectionSource() {
    return this.connectionSource;
  }

  @Override
  public Connection getJdbcConnection() {
    try {
      return connectionSource.getConnection();
    } catch (SQLException e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }


  @Override
  public <T> void run(Class<T> objectClass, OrmConsumerHandler<TypedOrmConnection<T>> handler) {
    try (TypedOrmConnection<T> conn = getConnection(objectClass)) {
      try {
        handler.accept(conn);
      } catch (Throwable e) {
        throw OrmException.wrapIfNotOrmException(e);
      }
    }
  }

  @Override
  public void run(OrmConsumerHandler<OrmConnection> handler) {
    try (OrmConnection conn = getConnection()) {
      handler.accept(conn);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  @Override
  public <T> void runTransaction(Class<T> objectClass,
      OrmConsumerHandler<TypedOrmConnection<T>> handler) {
    try (TypedOrmConnection<T> transaction = beginTransaction(objectClass)) {
      handler.accept(transaction);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  @Override
  public <T> void runTransaction(Class<T> objectClass, int isolationLevel,
      OrmConsumerHandler<TypedOrmConnection<T>> handler) {
    try (TypedOrmConnection<T> transaction = beginTransaction(objectClass, isolationLevel)) {
      handler.accept(transaction);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }


  @Override
  public void runTransaction(OrmConsumerHandler<OrmConnection> handler) {
    try (OrmConnection conn = beginTransaction()) {
      handler.accept(conn);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  @Override
  public void runTransaction(int isolationLevel, OrmConsumerHandler<OrmConnection> handler) {
    try (OrmConnection conn = beginTransaction(isolationLevel)) {
      handler.accept(conn);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  @Override
  public void runWithJdbcConnection(OrmConsumerHandler<Connection> handler) {
    try (Connection conn = getJdbcConnection()) {
      handler.accept(conn);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }


  @Override
  public String toString() {
    return "Sorm [connectionSource=" + connectionSource + ", configStore=" + configStore + "]";
  }


  private static final class OrmTransaction extends OrmConnectionImpl {
    // private static final org.slf4j.Logger log =
    // org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

    public OrmTransaction(Connection connection, OrmConfigStore options, int isolationLevel) {
      super(connection, options);
      begin(isolationLevel);
    }

    /**
     * ALWAYS rollback before closing the connection if there's any caught/uncaught exception, the
     * transaction will be rolled back if everything is successful / commit is successful, the
     * rollback will have no effect.
     */
    @Override
    public void close() {
      rollback();
      super.close();
    }

  }

  private static class TypedOrmTransaction<T> extends TypedOrmConnectionImpl<T> {
    // private static final org.slf4j.Logger log =
    // org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

    public TypedOrmTransaction(Class<T> objectClass, Connection connection, OrmConfigStore options,
        int isolationLevel) {
      super(objectClass, connection, options);
      begin(isolationLevel);
    }

    /**
     * ALWAYS rollback before closing the connection if there's any caught/uncaught exception, the
     * transaction will be rolled back if everything is successful / commit is successful, the
     * rollback will have no effect.
     */
    @Override
    public void close() {
      rollback();
      super.close();
    }


  }

}
