package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.config.OrmConfigStore.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.config.OrmConfigStore.Builder;
import org.nkjmlab.sorm4j.connectionsource.ConnectionSource;
import org.nkjmlab.sorm4j.mapping.OrmCache;
import org.nkjmlab.sorm4j.mapping.OrmConnectionImpl;
import org.nkjmlab.sorm4j.mapping.OrmTransaction;
import org.nkjmlab.sorm4j.mapping.TypedOrmConnectionImpl;
import org.nkjmlab.sorm4j.mapping.TypedOrmTransaction;

public final class Sorm {
  // private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  private final ConnectionSource connectionSource;

  private final OrmConfigStore configStore;

  private static OrmConfigStore configure(OrmConfigStore newConfigStore) {
    OrmCache.refresh(newConfigStore.getConfigName());
    return OrmConfigStore.put(newConfigStore);
  }

  public static OrmConfigStore configure(String configName,
      Function<OrmConfigStore.Builder, OrmConfigStore> buildOrmConfigStore) {
    return configure(buildOrmConfigStore.apply(new Builder(configName)));
  }

  public static OrmConfigStore updateDefaultConfigStore(
      Function<OrmConfigStore.Builder, OrmConfigStore> buildOrmConfigStore) {
    return configure(buildOrmConfigStore.apply(new Builder(DEFAULT_CONFIG_NAME)));
  }

  public static OrmConfigStore resetDefaultConfigStore() {
    return configure(OrmConfigStore.INITIAL_DEFAULT_CONFIG_STORE);
  }


  public static Sorm create(ConnectionSource connectionSource) {
    return create(connectionSource, DEFAULT_CONFIG_NAME);
  }

  public static Sorm create(ConnectionSource connectionSource, String configName) {
    return new Sorm(connectionSource, OrmConfigStore.get(configName));
  }

  public static Sorm create(DataSource dataSource) {
    return create(dataSource, DEFAULT_CONFIG_NAME);
  }

  public static Sorm create(DataSource dataSource, String configName) {
    return create(ConnectionSource.of(dataSource), configName);
  }

  public static Sorm create(String jdbcUrl, String user, String password) {
    return create(jdbcUrl, user, password, DEFAULT_CONFIG_NAME);
  }

  public static Sorm create(String jdbcUrl, String user, String password, String configName) {
    return create(ConnectionSource.of(jdbcUrl, user, password), configName);
  }


  public static OrmConnection getOrmConnection(Connection connection) {
    return getOrmConnection(connection, OrmConfigStore.getDefaultConfigStore());
  }

  private static OrmConnection getOrmConnection(Connection connection, OrmConfigStore configStore) {
    return new OrmConnectionImpl(connection, configStore);
  }

  public static OrmConnection getOrmConnection(Connection connection, String configName) {
    return getOrmConnection(connection, OrmConfigStore.get(configName));
  }

  public static <T> TypedOrmConnection<T> getTypedOrmConnection(Connection conn,
      Class<T> objectClass) {
    return getTypedOrmConnection(conn, objectClass, OrmConfigStore.getDefaultConfigStore());
  }

  public static <T> TypedOrmConnection<T> getTypedOrmConnection(Connection conn,
      Class<T> objectClass, String configName) {
    return getTypedOrmConnection(conn, objectClass, OrmConfigStore.get(configName));
  }

  private static <T> TypedOrmConnection<T> getTypedOrmConnection(Connection connection,
      Class<T> objectClass, OrmConfigStore options) {
    return new TypedOrmConnectionImpl<T>(objectClass, connection, options);
  }


  private Sorm(ConnectionSource connectionSource, OrmConfigStore configs) {
    this.configStore = configs;
    this.connectionSource = connectionSource;
  }

  public OrmTransaction beginTransaction() {
    return new OrmTransaction(getJdbcConnection(), configStore, DEFAULT_ISOLATION_LEVEL);
  }

  public <T> TypedOrmTransaction<T> beginTransaction(Class<T> objectClass) {
    return new TypedOrmTransaction<T>(objectClass, getJdbcConnection(), configStore,
        DEFAULT_ISOLATION_LEVEL);
  }

  public <T> TypedOrmTransaction<T> beginTransaction(Class<T> objectClass, int isolationLevel) {
    return new TypedOrmTransaction<T>(objectClass, getJdbcConnection(), configStore,
        isolationLevel);
  }

  public OrmTransaction beginTransaction(int isolationLevel) {
    return new OrmTransaction(getJdbcConnection(), configStore, isolationLevel);
  }

  public <T, R> R execute(Class<T> objectClass,
      OrmFunctionHandler<TypedOrmConnection<T>, R> handler) {
    try (TypedOrmConnection<T> conn = getConnection(objectClass)) {
      return handler.apply(conn);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  public <R> R execute(OrmFunctionHandler<OrmConnection, R> handler) {
    try (OrmConnection conn = getConnection()) {
      return handler.apply(conn);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  public <T, R> R executeTransaction(Class<T> objectClass,
      OrmFunctionHandler<TypedOrmTransaction<T>, R> handler) {
    try (TypedOrmTransaction<T> transaction = beginTransaction(objectClass)) {
      return handler.apply(transaction);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }



  public <T, R> R executeTransaction(Class<T> objectClass, int isolationLevel,
      OrmFunctionHandler<TypedOrmTransaction<T>, R> handler) {
    try (TypedOrmTransaction<T> transaction = beginTransaction(objectClass, isolationLevel)) {
      return handler.apply(transaction);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  public <R> R executeTransaction(int isolationLevel,
      OrmFunctionHandler<OrmTransaction, R> handler) {
    try (OrmTransaction transaction = beginTransaction()) {
      return handler.apply(transaction);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }


  public <R> R executeTransaction(OrmFunctionHandler<OrmTransaction, R> handler) {
    try (OrmTransaction transaction = beginTransaction()) {
      return handler.apply(transaction);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  public <R> R executeWithJdbcConnection(OrmFunctionHandler<Connection, R> handler) {
    try (Connection conn = getJdbcConnection()) {
      return handler.apply(conn);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  public OrmConfigStore getConfigStore() {
    return this.configStore;
  }


  public OrmConnection getConnection() {
    return getOrmConnection(getJdbcConnection(), configStore);
  }

  public <T> TypedOrmConnection<T> getConnection(Class<T> objectClass) {
    return getTypedOrmConnection(getJdbcConnection(), objectClass, configStore);
  }

  public ConnectionSource getConnectionSource() {
    return this.connectionSource;
  }

  public Connection getJdbcConnection() {
    try {
      return connectionSource.getConnection();
    } catch (SQLException e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }


  public <T> void run(Class<T> objectClass, OrmConsumerHandler<TypedOrmConnection<T>> handler) {
    try (TypedOrmConnection<T> conn = getConnection(objectClass)) {
      try {
        handler.accept(conn);
      } catch (Throwable e) {
        throw OrmException.wrapIfNotOrmException(e);
      }
    }
  }

  public void run(OrmConsumerHandler<OrmConnection> handler) {
    try (OrmConnection conn = getConnection()) {
      handler.accept(conn);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  public <T> void runTransaction(Class<T> objectClass,
      OrmConsumerHandler<TypedOrmTransaction<T>> handler) {
    try (TypedOrmTransaction<T> transaction = beginTransaction(objectClass)) {
      handler.accept(transaction);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  public <T> void runTransaction(Class<T> objectClass, int isolationLevel,
      OrmConsumerHandler<TypedOrmTransaction<T>> handler) {
    try (TypedOrmTransaction<T> transaction = beginTransaction(objectClass, isolationLevel)) {
      handler.accept(transaction);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }


  public void runTransaction(OrmConsumerHandler<OrmTransaction> handler) {
    try (OrmTransaction conn = beginTransaction()) {
      handler.accept(conn);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

  public void runTransaction(int isolationLevel, OrmConsumerHandler<OrmTransaction> handler) {
    try (OrmTransaction conn = beginTransaction(isolationLevel)) {
      handler.accept(conn);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
  }

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

  public static OrmConnection toUntyped(TypedOrmConnection<?> conn) {
    return getOrmConnection(conn.getJdbcConnection(), conn.getConfigStore().getConfigName());
  }

  public static <T> TypedOrmConnection<T> toTyped(OrmConnection conn, Class<T> objectClass) {
    return getTypedOrmConnection(conn.getJdbcConnection(), objectClass,
        conn.getConfigStore().getConfigName());
  }

  @FunctionalInterface
  public interface OrmConsumerHandler<T> {

    void accept(T t) throws Throwable;

  }

  @FunctionalInterface
  public interface OrmFunctionHandler<T, R> {

    R apply(T t) throws Throwable;

  }



}
