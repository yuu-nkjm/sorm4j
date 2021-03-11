package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.mapping.OrmConfigStore.*;
import java.sql.Connection;
import java.sql.SQLException;
import org.nkjmlab.sorm4j.ConnectionSource;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormFactory;
import org.nkjmlab.sorm4j.TypedOrmConnection;
import org.nkjmlab.sorm4j.util.Try;

/**
 * An entry point of object-relation mapping.
 *
 * @author nkjm
 *
 */
public final class SormImpl implements Sorm {
  // private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  private final ConnectionSource connectionSource;

  private final OrmConfigStore configStore;


  public SormImpl(ConnectionSource connectionSource, OrmConfigStore configs) {
    this.configStore = configs;
    this.connectionSource = connectionSource;
  }

  @Override
  public OrmTransaction beginTransaction() {
    return new OrmTransaction(getJdbcConnection(), configStore, DEFAULT_ISOLATION_LEVEL);
  }

  @Override
  public <T> TypedOrmTransaction<T> beginTransaction(Class<T> objectClass) {
    return new TypedOrmTransaction<T>(objectClass, getJdbcConnection(), configStore,
        DEFAULT_ISOLATION_LEVEL);
  }

  @Override
  public <T> TypedOrmTransaction<T> beginTransaction(Class<T> objectClass, int isolationLevel) {
    return new TypedOrmTransaction<T>(objectClass, getJdbcConnection(), configStore,
        isolationLevel);
  }

  @Override
  public OrmTransaction beginTransaction(int isolationLevel) {
    return new OrmTransaction(getJdbcConnection(), configStore, isolationLevel);
  }

  @Override
  public <T, R> R applyAndGet(Class<T> objectClass,
      OrmFunctionHandler<TypedOrmConnection<T>, R> handler) {
    try (TypedOrmConnection<T> conn = getConnection(objectClass)) {
      return handler.apply(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public <R> R applyAndGet(OrmFunctionHandler<OrmConnection, R> handler) {
    try (OrmConnection conn = getConnection()) {
      return handler.apply(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public <T, R> R applyTransactionAndGet(Class<T> objectClass,
      OrmFunctionHandler<TypedOrmTransaction<T>, R> handler) {
    try (TypedOrmTransaction<T> transaction = beginTransaction(objectClass)) {
      return handler.apply(transaction);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }



  @Override
  public <T, R> R applyTransactionAndGet(Class<T> objectClass, int isolationLevel,
      OrmFunctionHandler<TypedOrmTransaction<T>, R> handler) {
    try (TypedOrmTransaction<T> transaction = beginTransaction(objectClass, isolationLevel)) {
      return handler.apply(transaction);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public <R> R applyTransactionAndGet(int isolationLevel,
      OrmFunctionHandler<OrmTransaction, R> handler) {
    try (OrmTransaction transaction = beginTransaction()) {
      return handler.apply(transaction);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }


  @Override
  public <R> R applyTransactionAndGet(OrmFunctionHandler<OrmTransaction, R> handler) {
    try (OrmTransaction transaction = beginTransaction()) {
      return handler.apply(transaction);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public <R> R applyToJdbcConnectionAndGet(OrmFunctionHandler<Connection, R> handler) {
    try (Connection conn = getJdbcConnection()) {
      return handler.apply(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
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
      throw Try.rethrow(e);
    }
  }


  @Override
  public <T> void apply(Class<T> objectClass, OrmConsumerHandler<TypedOrmConnection<T>> handler) {
    try (TypedOrmConnection<T> conn = getConnection(objectClass)) {
      try {
        handler.accept(conn);
      } catch (Exception e) {
        throw Try.rethrow(e);
      }
    }
  }

  @Override
  public void apply(OrmConsumerHandler<OrmConnection> handler) {
    try (OrmConnection conn = getConnection()) {
      handler.accept(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public <T> void applyTransaction(Class<T> objectClass,
      OrmConsumerHandler<TypedOrmTransaction<T>> handler) {
    try (TypedOrmTransaction<T> transaction = beginTransaction(objectClass)) {
      handler.accept(transaction);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public <T> void applyTransaction(Class<T> objectClass, int isolationLevel,
      OrmConsumerHandler<TypedOrmTransaction<T>> handler) {
    try (TypedOrmTransaction<T> transaction = beginTransaction(objectClass, isolationLevel)) {
      handler.accept(transaction);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }


  @Override
  public void applyTransaction(OrmConsumerHandler<OrmTransaction> handler) {
    try (OrmTransaction conn = beginTransaction()) {
      handler.accept(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public void applyTransaction(int isolationLevel, OrmConsumerHandler<OrmTransaction> handler) {
    try (OrmTransaction conn = beginTransaction(isolationLevel)) {
      handler.accept(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public void applyToJdbcConnection(OrmConsumerHandler<Connection> handler) {
    try (Connection conn = getJdbcConnection()) {
      handler.accept(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }


  @Override
  public String toString() {
    return "Sorm [connectionSource=" + connectionSource + ", configStore=" + configStore + "]";
  }


  public static final class OrmTransaction extends OrmConnectionImpl {
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

  public static class TypedOrmTransaction<T> extends TypedOrmConnectionImpl<T> {
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
