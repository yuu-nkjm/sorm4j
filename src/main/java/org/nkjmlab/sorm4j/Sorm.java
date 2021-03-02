package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.config.OrmConfigStore.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.connectionsource.ConnectionSource;
import org.nkjmlab.sorm4j.mapping.OrmCache;
import org.nkjmlab.sorm4j.mapping.OrmConnectionImpl;
import org.nkjmlab.sorm4j.mapping.OrmTransaction;
import org.nkjmlab.sorm4j.mapping.TypedOrmTransaction;

public final class Sorm {
  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();


  private final ConnectionSource connectionSource;
  private final OrmConfigStore configStore;

  private Sorm(ConnectionSource connectionSource, OrmConfigStore configs) {
    this.configStore = configs;
    this.connectionSource = connectionSource;
  }


  public static Sorm of(ConnectionSource connectionSource) {
    return new Sorm(connectionSource, OrmConfigStore.DEFAULT_CONFIGURATIONS);
  }

  public static Sorm of(DataSource dataSource) {
    return of(ConnectionSource.of(dataSource));
  }


  public static Sorm of(String jdbcUrl, String user, String password) {
    return of(ConnectionSource.of(jdbcUrl, user, password));
  }

  public static Sorm withNewConfig(ConnectionSource connectionSource,
      OrmConfigStore newConfigStore) {
    OrmCache.refresh(newConfigStore.getCacheName());
    return new Sorm(connectionSource, newConfigStore);
  }

  public static Sorm withNewConfig(DataSource dataSource, OrmConfigStore newConfigStore) {
    return withNewConfig(ConnectionSource.of(dataSource), newConfigStore);
  }

  public static Sorm withNewConfig(String jdbcUrl, String user, String password,
      OrmConfigStore newConfigStore) {
    return withNewConfig(ConnectionSource.of(jdbcUrl, user, password), newConfigStore);
  }


  public static <T> TypedOrmConnection<T> toTypedOrmConnection(Class<T> objectClass,
      Connection conn) {
    return TypedOrmConnection.of(objectClass, conn);
  }

  public OrmTransaction beginTransaction() {
    return new OrmTransaction(getJdbcConnection(), configStore,
        OrmConfigStore.DEFAULT_ISOLATION_LEVEL);
  }

  public <T> TypedOrmTransaction<T> beginTransaction(Class<T> objectClass) {
    return new TypedOrmTransaction<T>(objectClass, getJdbcConnection(), configStore,
        DEFAULT_ISOLATION_LEVEL);
  }


  public OrmTransaction beginTransaction(int isolationLevel) {
    return new OrmTransaction(getJdbcConnection(), configStore, isolationLevel);
  }

  public <T> TypedOrmTransaction<T> beginTransaction(Class<T> objectClass, int isolationLevel) {
    return new TypedOrmTransaction<T>(objectClass, getJdbcConnection(), configStore,
        isolationLevel);
  }

  public <T, R> R execute(Class<T> objectClass, Function<TypedOrmConnection<T>, R> handler) {
    try (TypedOrmConnection<T> conn = getConnection(objectClass)) {
      return handler.apply(conn);
    }
  }

  public <R> R execute(Function<OrmConnection, R> handler) {
    try (OrmConnection conn = getConnection()) {
      return handler.apply(conn);
    }
  }

  public <T, R> R executeTransaction(Class<T> objectClass, int isolationLevel,
      Function<TypedOrmTransaction<T>, R> handler) {
    try (TypedOrmTransaction<T> transaction = beginTransaction(objectClass, isolationLevel)) {
      return handler.apply(transaction);
    }
  }

  public <T, R> R executeTransaction(Class<T> objectClass,
      Function<TypedOrmTransaction<T>, R> handler) {
    try (TypedOrmTransaction<T> transaction = beginTransaction(objectClass)) {
      return handler.apply(transaction);
    }
  }

  public <R> R executeTransaction(Function<OrmTransaction, R> handler) {
    try (OrmTransaction transaction = beginTransaction()) {
      return handler.apply(transaction);
    }
  }

  public <R> R executeWithJdbcConnection(Function<Connection, R> handler) {
    try (Connection conn = getJdbcConnection()) {
      return handler.apply(conn);
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  public OrmConnection getConnection() {
    return toOrmConnection(getJdbcConnection(), configStore);
  }


  public <T> TypedOrmConnection<T> getConnection(Class<T> objectClass) {
    return TypedOrmConnection.of(objectClass, getJdbcConnection(), configStore);
  }

  public Connection getJdbcConnection() {
    try {
      return connectionSource.getConnection();
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  public <T> void run(Class<T> objectClass, Consumer<TypedOrmConnection<T>> handler) {
    try (TypedOrmConnection<T> conn = getConnection(objectClass)) {
      handler.accept(conn);
    }
  }

  public void run(Consumer<OrmConnection> handler) {
    try (OrmConnection conn = getConnection()) {
      handler.accept(conn);
    }
  }

  public <T> void runTransaction(Class<T> objectClass, Consumer<TypedOrmTransaction<T>> handler) {
    try (TypedOrmTransaction<T> transaction = beginTransaction(objectClass)) {
      handler.accept(transaction);
    }
  }

  public <T> void runTransaction(Class<T> objectClass, int isolationLevel,
      Consumer<TypedOrmTransaction<T>> handler) {
    try (TypedOrmTransaction<T> transaction = beginTransaction(objectClass, isolationLevel)) {
      handler.accept(transaction);
    }
  }

  public void runTransaction(Consumer<OrmTransaction> handler, int isolationLevel) {
    try (OrmTransaction conn = beginTransaction(isolationLevel)) {
      handler.accept(conn);
    }
  }


  public void runTransaction(Consumer<OrmTransaction> handler) {
    try (OrmTransaction conn = beginTransaction()) {
      handler.accept(conn);
    }
  }

  public void runWithJdbcConnection(Consumer<Connection> handler) {
    try (Connection conn = getJdbcConnection()) {
      handler.accept(conn);
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  @Override
  public String toString() {
    return "OrmService [connectionSource=" + connectionSource + ", configStore=" + configStore
        + "]";
  }

  public ConnectionSource getConnectionSource() {
    return this.connectionSource;
  }


  public static OrmConnection toOrmConnection(Connection connection) {
    return toOrmConnection(connection, DEFAULT_CONFIGURATIONS);
  }

  public static OrmConnection toOrmConnection(Connection connection, OrmConfigStore configStore) {
    return new OrmConnectionImpl(connection, configStore);
  }


}
