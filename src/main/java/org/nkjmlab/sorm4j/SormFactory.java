package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.core.connectionsource.DataSourceConnectionSource;
import org.nkjmlab.sorm4j.core.connectionsource.DriverManagerConnectionSource;
import org.nkjmlab.sorm4j.core.mapping.ConfigStore;
import org.nkjmlab.sorm4j.core.mapping.ConfiguratorImpl;
import org.nkjmlab.sorm4j.core.mapping.OrmConnectionImpl;
import org.nkjmlab.sorm4j.core.mapping.SormImpl;
import org.nkjmlab.sorm4j.core.mapping.TypedOrmConnectionImpl;

/**
 * Main entry point of Som4j, which is factory for {@link Sorm} object.
 *
 * @author nkjm
 *
 */
public final class SormFactory {
  /**
   * Default config name of Sorm.
   */
  public static final String DEFAULT_CONFIG_NAME = "DEFAULT_CONFIG";

  private static void configure(ConfiguratorImpl configurator,
      Consumer<Configurator> configuratorConsumer) {
    configuratorConsumer.accept(configurator);
    ConfigStore.refreshAndRegister(configurator.build());
  };

  /**
   * Create a {@link Sorm} object which uses {@link ConnectionSource}.
   *
   * @param connectionSource
   * @return
   */
  public static Sorm create(ConnectionSource connectionSource) {
    return create(connectionSource, SormFactory.DEFAULT_CONFIG_NAME);
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
    return create(dataSource, SormFactory.DEFAULT_CONFIG_NAME);
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
    return create(jdbcUrl, user, password, SormFactory.DEFAULT_CONFIG_NAME);
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

  private static ConnectionSource createConnectionSource(DataSource dataSource) {
    return new DataSourceConnectionSource(dataSource);
  }

  private static ConnectionSource createConnectionSource(String jdbcUrl, String user,
      String password) {
    return new DriverManagerConnectionSource(jdbcUrl, user, password);
  }

  /**
   * Registers configuration under the given name.
   *
   * @param configName
   * @param configuratorConsumer
   */
  public static void registerConfig(String configName,
      Consumer<Configurator> configuratorConsumer) {
    configure(new ConfiguratorImpl(configName), configuratorConsumer);
  }

  /**
   * Registers modified configuration of the given Sorm object under the given name.
   *
   * @param newConfigName
   * @param srcConfigName
   * @param configuratorConsumer
   */
  public static void registerModifiedConfig(String newConfigName, String srcConfigName,
      Consumer<Configurator> configuratorConsumer) {
    configure(new ConfiguratorImpl(newConfigName, ConfigStore.get(srcConfigName)),
        configuratorConsumer);
  }

  /**
   * Updates default configuration.
   *
   * @param configuratorConsumer
   */
  public static void updateDefaultConfig(Consumer<Configurator> configuratorConsumer) {
    configure(new ConfiguratorImpl(SormFactory.DEFAULT_CONFIG_NAME), configuratorConsumer);
  }

  /**
   * Create a {@link OrmConnection} wrapping the given JDBC Connection
   *
   * @param connection
   * @return
   */
  public static OrmConnection toOrmConnection(Connection connection) {
    return toOrmConnection(connection, ConfigStore.getDefaultConfigStore());
  }

  /**
   * Create a {@link TypedOrmConnection} wrapping the given JDBC Connection.
   *
   * @param <T>
   * @param conn
   * @param objectClass
   * @return
   */
  public static <T> TypedOrmConnection<T> toOrmConnection(Connection conn, Class<T> objectClass) {
    return toOrmConnection(conn, objectClass, ConfigStore.getDefaultConfigStore());
  }

  private static <T> TypedOrmConnection<T> toOrmConnection(Connection connection,
      Class<T> objectClass, ConfigStore configStore) {
    return new TypedOrmConnectionImpl<T>(objectClass, connection, configStore);
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
  public static <T> TypedOrmConnection<T> toOrmConnection(Connection conn, Class<T> objectClass,
      String configName) {
    return toOrmConnection(conn, objectClass, ConfigStore.get(configName));
  }

  private static OrmConnection toOrmConnection(Connection connection, ConfigStore configStore) {
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
  public static OrmConnection toOrmConnection(Connection connection, String configName) {
    return toOrmConnection(connection, ConfigStore.get(configName));
  }


  private SormFactory() {}

}
