package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.mapping.ConfigStore.*;
import java.sql.Connection;
import java.util.function.Function;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.mapping.ConfigStore;
import org.nkjmlab.sorm4j.mapping.ConfigStoreBuilderImpl;
import org.nkjmlab.sorm4j.mapping.DataSourceConnectionSource;
import org.nkjmlab.sorm4j.mapping.DriverManagerConnectionSource;
import org.nkjmlab.sorm4j.mapping.OrmConnectionImpl;
import org.nkjmlab.sorm4j.mapping.SormImpl;
import org.nkjmlab.sorm4j.mapping.TypedOrmConnectionImpl;

/**
 * Main entry point of Som4j, which is factory for {@link Sorm} object.
 *
 * @author nkjm
 *
 */
public class SormFactory {

  private SormFactory() {};

  private static ConfigStore configure(ConfigStore newConfigStore) {
    return ConfigStore.refreshAndRegister(newConfigStore);
  }

  public static ConfigStore registerNewConfigStore(String configName,
      Function<ConfigStoreBuilder, ConfigStore> buildFunction) {
    return configure(buildFunction.apply(new ConfigStoreBuilderImpl(configName)));
  }

  public static ConfigStore registerNewModifiedConfigStore(String configName,
      ConfigStore srcConfigStore, Function<ConfigStoreBuilder, ConfigStore> buildFunction) {
    return configure(buildFunction.apply(new ConfigStoreBuilderImpl(configName, srcConfigStore)));
  }

  public static ConfigStore updateDefaultConfigStore(
      Function<ConfigStoreBuilderImpl, ConfigStore> buildOrmConfigStore) {
    return configure(buildOrmConfigStore.apply(new ConfigStoreBuilderImpl(DEFAULT_CONFIG_NAME)));
  }

  public static ConfigStore resetDefaultConfigStore() {
    return configure(ConfigStore.INITIAL_DEFAULT_CONFIG_STORE);
  }


  public static Sorm create(ConnectionSource connectionSource) {
    return create(connectionSource, DEFAULT_CONFIG_NAME);
  }

  public static Sorm create(ConnectionSource connectionSource, String configName) {
    return new SormImpl(connectionSource, ConfigStore.get(configName));
  }

  public static Sorm create(DataSource dataSource) {
    return create(dataSource, DEFAULT_CONFIG_NAME);
  }

  public static Sorm create(DataSource dataSource, String configName) {
    return create(getConnectionSource(dataSource), configName);
  }

  public static Sorm create(String jdbcUrl, String user, String password) {
    return create(jdbcUrl, user, password, DEFAULT_CONFIG_NAME);
  }

  public static Sorm create(String jdbcUrl, String user, String password, String configName) {
    return create(getConnectionSource(jdbcUrl, user, password), configName);
  }


  public static OrmConnection getOrmConnection(Connection connection) {
    return getOrmConnection(connection, ConfigStore.getDefaultConfigStore());
  }

  public static OrmConnection getOrmConnection(Connection connection, ConfigStore configStore) {
    return new OrmConnectionImpl(connection, configStore);
  }

  public static OrmConnection getOrmConnection(Connection connection, String configName) {
    return getOrmConnection(connection, ConfigStore.get(configName));
  }

  public static <T> TypedOrmConnection<T> getTypedOrmConnection(Connection conn,
      Class<T> objectClass) {
    return getTypedOrmConnection(conn, objectClass, ConfigStore.getDefaultConfigStore());
  }

  public static <T> TypedOrmConnection<T> getTypedOrmConnection(Connection conn,
      Class<T> objectClass, String configName) {
    return getTypedOrmConnection(conn, objectClass, ConfigStore.get(configName));
  }

  public static <T> TypedOrmConnection<T> getTypedOrmConnection(Connection connection,
      Class<T> objectClass, ConfigStore options) {
    return new TypedOrmConnectionImpl<T>(objectClass, connection, options);
  }

  public static OrmConnection toUntyped(TypedOrmConnection<?> conn) {
    return SormFactory.getOrmConnection(conn.getJdbcConnection(),
        conn.getConfigStore().getConfigName());
  }

  public static <T> TypedOrmConnection<T> toTyped(OrmConnection conn, Class<T> objectClass) {
    return SormFactory.getTypedOrmConnection(conn.getJdbcConnection(), objectClass,
        conn.getConfigStore().getConfigName());
  }


  static ConnectionSource getConnectionSource(String jdbcUrl, String user, String password) {
    return new DriverManagerConnectionSource(jdbcUrl, user, password);
  }

  static ConnectionSource getConnectionSource(DataSource dataSource) {
    return new DataSourceConnectionSource(dataSource);
  }
}
