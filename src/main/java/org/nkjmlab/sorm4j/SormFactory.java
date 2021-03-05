package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.mapping.OrmConfigStore.*;
import java.sql.Connection;
import java.util.function.Function;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.mapping.DataSourceConnectionSource;
import org.nkjmlab.sorm4j.mapping.DriverManagerConnectionSource;
import org.nkjmlab.sorm4j.mapping.OrmConfigStore;
import org.nkjmlab.sorm4j.mapping.OrmConfigStoreBuilderImpl;
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

  private static OrmConfigStore configure(OrmConfigStore newConfigStore) {
    return OrmConfigStore.refreshAndRegister(newConfigStore);
  }

  public static OrmConfigStore configure(String configName,
      Function<OrmConfigStoreBuilderImpl, OrmConfigStore> buildOrmConfigStore) {
    return configure(buildOrmConfigStore.apply(new OrmConfigStoreBuilderImpl(configName)));
  }

  public static OrmConfigStore updateDefaultConfigStore(
      Function<OrmConfigStoreBuilderImpl, OrmConfigStore> buildOrmConfigStore) {
    return configure(buildOrmConfigStore.apply(new OrmConfigStoreBuilderImpl(DEFAULT_CONFIG_NAME)));
  }

  public static OrmConfigStore resetDefaultConfigStore() {
    return configure(OrmConfigStore.INITIAL_DEFAULT_CONFIG_STORE);
  }


  public static Sorm create(ConnectionSource connectionSource) {
    return create(connectionSource, DEFAULT_CONFIG_NAME);
  }

  public static Sorm create(ConnectionSource connectionSource, String configName) {
    return new SormImpl(connectionSource, OrmConfigStore.get(configName));
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
    return getOrmConnection(connection, OrmConfigStore.getDefaultConfigStore());
  }

  public static OrmConnection getOrmConnection(Connection connection, OrmConfigStore configStore) {
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

  public static <T> TypedOrmConnection<T> getTypedOrmConnection(Connection connection,
      Class<T> objectClass, OrmConfigStore options) {
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

  public static OrmConfigStoreBuilder createConfigStoreBuilder(String newConfig) {
    return new OrmConfigStoreBuilderImpl(newConfig);
  }

  static ConnectionSource getConnectionSource(String jdbcUrl, String user, String password) {
    return new DriverManagerConnectionSource(jdbcUrl, user, password);
  }

  static ConnectionSource getConnectionSource(DataSource dataSource) {
    return new DataSourceConnectionSource(dataSource);
  }
}
