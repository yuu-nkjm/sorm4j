package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.core.ConfigStore.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.core.ConfigStore;
import org.nkjmlab.sorm4j.core.ConfiguratorImpl;
import org.nkjmlab.sorm4j.core.DataSourceConnectionSource;
import org.nkjmlab.sorm4j.core.DriverManagerConnectionSource;
import org.nkjmlab.sorm4j.core.OrmConnectionImpl;
import org.nkjmlab.sorm4j.core.SormImpl;
import org.nkjmlab.sorm4j.core.TypedOrmConnectionImpl;

/**
 * Main entry point of Som4j, which is factory for {@link Sorm} object.
 *
 * @author nkjm
 *
 */
public final class SormFactory {

  private SormFactory() {};

  private static void configure(ConfigStore newConfigStore) {
    ConfigStore.refreshAndRegister(newConfigStore);
  }

  /**
   * Registers new configuration.
   *
   * @param configName
   * @param configurator
   */
  public static void registerNewConfig(String configName, Consumer<Configurator> configurator) {
    ConfiguratorImpl conf = new ConfiguratorImpl(configName);
    configurator.accept(conf);
    configure(conf.build());
  }

  /**
   * Registers new configuration from the current configuration.
   *
   * @param configName
   * @param sorm
   * @param configurator
   */
  public static void registerNewModifiedConfig(String configName, Sorm sorm,
      Consumer<Configurator> configurator) {
    ConfiguratorImpl conf = new ConfiguratorImpl(configName, sorm.getConfigStore());
    configurator.accept(conf);
    configure(conf.build());
  }

  /**
   * Updates default configuration by configurator.
   *
   * @param configurator
   */
  public static void updateDefaultConfig(Consumer<Configurator> configurator) {
    ConfiguratorImpl conf = new ConfiguratorImpl(DEFAULT_CONFIG_NAME);
    configurator.accept(conf);
    configure(conf.build());
  }

  /**
   * Resets default configuration.
   */
  public static void resetDefaultConfig() {
    configure(ConfigStore.INITIAL_DEFAULT_CONFIG_STORE);
  }


  /**
   * Create a {@link Sorm} object which uses {@link ConnectionSource}.
   *
   * @param connectionSource
   * @return
   */
  public static Sorm create(ConnectionSource connectionSource) {
    return create(connectionSource, DEFAULT_CONFIG_NAME);
  }

  /**
   * Create a {@link Sorm} object which uses {@link ConnectionSource} and the configurations
   * corresponding to the given configName.
   *
   * @param connectionSource
   * @return
   */
  public static Sorm create(ConnectionSource connectionSource, String configName) {
    return new SormImpl(connectionSource, ConfigStore.get(configName));
  }

  /**
   * Create a {@link Sorm} object which uses {@link DataSource}.
   *
   * @param dataSource
   * @return
   */
  public static Sorm create(DataSource dataSource) {
    return create(dataSource, DEFAULT_CONFIG_NAME);
  }

  /**
   * Create a {@link Sorm} object which uses {@link DataSource} and the configurations corresponding
   * to the given configName.
   *
   * @param dataSource
   * @param configName
   * @return
   */
  public static Sorm create(DataSource dataSource, String configName) {
    return create(createConnectionSource(dataSource), configName);
  }

  /**
   * Create a {@link Sorm} object which uses {@link DriverManager}.
   *
   * @param jdbcUrl
   * @param user
   * @param password
   * @return
   */
  public static Sorm create(String jdbcUrl, String user, String password) {
    return create(jdbcUrl, user, password, DEFAULT_CONFIG_NAME);
  }

  /**
   * Create a {@link Sorm} object which uses {@link DriverManager} and the configurations
   * corresponding to the given configName.
   *
   * @param jdbcUrl
   * @param user
   * @param password
   * @param configName
   * @return
   */
  public static Sorm create(String jdbcUrl, String user, String password, String configName) {
    return create(createConnectionSource(jdbcUrl, user, password), configName);
  }


  /**
   * Create a {@link OrmConnection} wrapping the given JDBC Connection
   *
   * @param connection
   * @return
   */
  public static OrmConnection getOrmConnection(Connection connection) {
    return getOrmConnection(connection, ConfigStore.getDefaultConfigStore());
  }

  private static OrmConnection getOrmConnection(Connection connection, ConfigStore configStore) {
    return new OrmConnectionImpl(connection, configStore);
  }

  /**
   * Create a {@link OrmConnection} wrapping the given JDBC Connection with the specified
   * configurations
   *
   * @param connection
   * @param configName
   * @return
   */
  public static OrmConnection getOrmConnection(Connection connection, String configName) {
    return getOrmConnection(connection, ConfigStore.get(configName));
  }

  /**
   * Create a {@link TypedOrmConnection} wrapping the given JDBC Connection.
   *
   * @param <T>
   * @param conn
   * @param objectClass
   * @return
   */
  public static <T> TypedOrmConnection<T> getTypedOrmConnection(Connection conn,
      Class<T> objectClass) {
    return getTypedOrmConnection(conn, objectClass, ConfigStore.getDefaultConfigStore());
  }

  /**
   * Create a {@link TypedOrmConnection} wrapping the given JDBC Connection with the specified
   * configurations
   *
   * @param <T>
   * @param conn
   * @param objectClass
   * @param configName
   * @return
   */
  public static <T> TypedOrmConnection<T> getTypedOrmConnection(Connection conn,
      Class<T> objectClass, String configName) {
    return getTypedOrmConnection(conn, objectClass, ConfigStore.get(configName));
  }

  private static <T> TypedOrmConnection<T> getTypedOrmConnection(Connection connection,
      Class<T> objectClass, ConfigStore configStore) {
    return new TypedOrmConnectionImpl<T>(objectClass, connection, configStore);
  }

  private static ConnectionSource createConnectionSource(String jdbcUrl, String user,
      String password) {
    return new DriverManagerConnectionSource(jdbcUrl, user, password);
  }

  private static ConnectionSource createConnectionSource(DataSource dataSource) {
    return new DataSourceConnectionSource(dataSource);
  }
}
