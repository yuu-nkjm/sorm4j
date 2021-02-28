package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.connectionsource.ConnectionSource;
import org.nkjmlab.sorm4j.connectionsource.DataSourceConnectionSource;
import org.nkjmlab.sorm4j.connectionsource.DriverManagerConnectionSource;
import org.nkjmlab.sorm4j.mapping.ColumnsMapping;
import org.nkjmlab.sorm4j.mapping.TableMapping;

public class Sorm {
  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  private final ConnectionSource connectionSource;

  private final OrmConfigStore configStore;

  private Sorm(ConnectionSource connectionSource, OrmConfigStore configs) {
    this.configStore = configs;
    this.connectionSource = connectionSource;
  }

  public static Sorm of(DataSource dataSource) {
    return of(dataSource, OrmConfigStore.DEFAULT_CONFIGURATIONS);
  }

  public static Sorm of(ConnectionSource connectionSource, OrmConfigStore configs) {
    return new Sorm(connectionSource, configs);
  }


  public static Sorm of(String jdbcUrl, String user, String password) {
    return of(jdbcUrl, user, password, OrmConfigStore.DEFAULT_CONFIGURATIONS);
  }


  public static Sorm of(DataSource dataSource, OrmConfigStore configs) {
    return of(new DataSourceConnectionSource(dataSource), configs);
  }

  public static Sorm of(String jdbcUrl, String user, String password, OrmConfigStore configs) {
    return of(new DriverManagerConnectionSource(jdbcUrl, user, password), configs);
  }

  public void runWithJdbcConnection(Consumer<Connection> handler) {
    try (Connection conn = getJdbcConnection()) {
      handler.accept(conn);
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  public <R> R executeWithJdbcConnection(Function<Connection, R> handler) {
    try (Connection conn = getJdbcConnection()) {
      return handler.apply(conn);
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }


  public void run(Consumer<OrmConnection> handler) {
    try (OrmConnection conn = getConnection()) {
      handler.accept(conn);
    }
  }

  public <T> void run(Class<T> objectClass, Consumer<TypedOrmConnection<T>> handler) {
    try (TypedOrmConnection<T> conn = getConnection(objectClass)) {
      handler.accept(conn);
    }
  }

  public <R> R execute(Function<OrmConnection, R> handler) {
    try (OrmConnection conn = getConnection()) {
      return handler.apply(conn);
    }
  }

  public <T, R> R execute(Class<T> objectClass, Function<TypedOrmConnection<T>, R> handler) {
    try (TypedOrmConnection<T> conn = getConnection(objectClass)) {
      return handler.apply(conn);
    }
  }


  public void runTransaction(Consumer<OrmTransaction> handler) {
    try (OrmTransaction conn = beginTransaction()) {
      handler.accept(conn);
    }
  }

  public <T> void runTransaction(Class<T> objectClass, Consumer<TypedOrmTransaction<T>> handler) {
    try (TypedOrmTransaction<T> transaction = beginTransaction(objectClass)) {
      handler.accept(transaction);
    }
  }

  public <R> R executeTransaction(Function<OrmTransaction, R> handler) {
    try (OrmTransaction transaction = beginTransaction()) {
      return handler.apply(transaction);
    }
  }

  public <T, R> R executeTransaction(Class<T> objectClass,
      Function<TypedOrmTransaction<T>, R> handler) {
    try (TypedOrmTransaction<T> transaction = beginTransaction(objectClass)) {
      return handler.apply(transaction);
    }
  }

  public OrmTransaction beginTransaction() {
    return OrmTransaction.of(getJdbcConnection());
  }

  public <T> TypedOrmTransaction<T> beginTransaction(Class<T> objectClass) {
    return TypedOrmTransaction.of(objectClass, getJdbcConnection());
  }


  public OrmTransaction beginTransaction(int isolationLevel) {
    return OrmTransaction.of(getJdbcConnection(), isolationLevel);
  }

  public <T> TypedOrmTransaction<T> beginTransaction(Class<T> objectClass, int isolationLevel) {
    return TypedOrmTransaction.of(objectClass, getJdbcConnection(), isolationLevel);
  }

  public OrmConnection getConnection() {
    return OrmConnection.of(getJdbcConnection());
  }

  public <T> TypedOrmConnection<T> getConnection(Class<T> objectClass) {
    return TypedOrmConnection.of(objectClass, getJdbcConnection());
  }

  public Connection getJdbcConnection() {
    try {
      return connectionSource.getConnection();
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  private static final ConcurrentMap<String, ConcurrentMap<String, TableMapping<?>>> tableMappingsCaches =
      new ConcurrentHashMap<>(); // key => Cache Name
  private static final ConcurrentMap<String, ConcurrentMap<Class<?>, ColumnsMapping<?>>> columnsMappingsCaches =
      new ConcurrentHashMap<>(); // key => Cache Name

  public static ConcurrentMap<String, TableMapping<?>> getTableMappings(String cacheName) {
    return tableMappingsCaches.computeIfAbsent(cacheName, n -> new ConcurrentHashMap<>());
  }

  public static ConcurrentMap<Class<?>, ColumnsMapping<?>> getColumnsMappings(String cacheName) {
    return columnsMappingsCaches.computeIfAbsent(cacheName, n -> new ConcurrentHashMap<>());
  }

  public static OrmMapper toOrmMapper(Connection conn) {
    return OrmMapper.of(conn);
  }

  public static <T> TypedOrmUpdater<T> toTypedOrmMapper(Class<T> objectClass, Connection conn) {
    return TypedOrmMapper.of(objectClass, conn);
  }

  public static OrmConnection toOrmConnection(Connection conn) {
    return OrmConnection.of(conn);
  }

  public static <T> TypedOrmConnection<T> toTypedOrmConnection(Class<T> objectClass,
      Connection conn) {
    return TypedOrmConnection.of(objectClass, conn);
  }

  public static OrmTransaction toOrmTransaction(Connection conn) {
    return OrmTransaction.of(conn);
  }

  public static <T> TypedOrmTransaction<T> toTypedOrmTransaction(Class<T> objectClass,
      Connection conn) {
    return TypedOrmTransaction.of(objectClass, conn);
  }


  @Override
  public String toString() {
    return "OrmService [connectionSource=" + connectionSource + ", configStore=" + configStore
        + "]";
  }



}
