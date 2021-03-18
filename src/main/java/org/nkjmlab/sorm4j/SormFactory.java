package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.core.mapping.ConfigStore;
import org.nkjmlab.sorm4j.core.mapping.ConfiguratorImpl;
import org.nkjmlab.sorm4j.core.mapping.DriverManagerDataSource;
import org.nkjmlab.sorm4j.core.mapping.OrmConnectionImpl;
import org.nkjmlab.sorm4j.core.mapping.SormImpl;
import org.nkjmlab.sorm4j.core.mapping.TypedOrmConnectionImpl;

/**
 * Main entry point of this library. It creates {@link Sorm} object.
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
    return new SormImpl(dataSource, ConfigStore.get(configName));
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
   * @param username
   * @param password
   * @param configName
   * @return
   */
  public static Sorm create(String jdbcUrl, String username, String password, String configName) {
    return create(createDriverManagerConnectionSource(jdbcUrl, username, password), configName);
  }


  private static DataSource createDriverManagerConnectionSource(String jdbcUrl, String username,
      String password) {
    return new DriverManagerDataSource(jdbcUrl, username, password);
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
   * @param srcConfigName
   * @param newConfigName
   * @param configuratorConsumer
   */
  public static void registerModifiedConfig(String srcConfigName, String newConfigName,
      Consumer<Configurator> configuratorConsumer) {
    configure(new ConfiguratorImpl(newConfigName, ConfigStore.get(srcConfigName)),
        configuratorConsumer);
  }

  /**
   * Updates default configuration. This updates does not effect existing {@link Sorm} objects.
   *
   * @param configuratorConsumer
   */
  public static void updateDefaultConfig(Consumer<Configurator> configuratorConsumer) {
    configure(new ConfiguratorImpl(SormFactory.DEFAULT_CONFIG_NAME), configuratorConsumer);
  }

  public static void resetDefaultConfig() {
    ConfigStore.refreshAndRegister(ConfigStore.INITIAL_DEFAULT_CONFIG_STORE);
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
