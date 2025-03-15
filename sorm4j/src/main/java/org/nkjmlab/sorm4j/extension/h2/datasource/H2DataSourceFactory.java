package org.nkjmlab.sorm4j.extension.h2.datasource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.nkjmlab.sorm4j.internal.util.SystemPropertyUtils;
import org.nkjmlab.sorm4j.util.function.exception.Try;

/**
 * A factory of local data source with h2 database.
 *
 * <ul>
 *   <li><a href="http://www.h2database.com/html/cheatSheet.html">H2 Database Engine</a>
 *   <li><a href="http://h2database.com/html/features.html#database_url">Database URL Overview</a>
 * </ul>
 *
 * @author nkjm
 */
public class H2DataSourceFactory {

  public static record Config(
      Path databaseDirectory, String databaseName, String username, String password, int tcpPort) {

    public static Config of(
        Path databaseDirectory, String databaseName, String username, String password) {
      return new Config(databaseDirectory, databaseName, username, password, 9092);
    }

    public static Config of(
        Path databaseDirectory,
        String databaseName,
        String username,
        String password,
        int tcpPort) {
      return new Config(databaseDirectory, databaseName, username, password, tcpPort);
    }

    @Override
    public String toString() {
      return "Config [databaseDirectory="
          + databaseDirectory
          + ", databaseName="
          + databaseName
          + ", username="
          + username
          + ", tcpPort="
          + tcpPort
          + "]";
    }
  }

  private final Config config;
  private final Path databaseDirectoryPath;
  private final Path databaseFilePath;

  private final String inMemoryModeJdbcUrl;
  private final String serverModeJdbcUrl;
  private final String embeddedModeJdbcUrl;
  private final String mixedModeJdbcUrl;

  private H2DataSourceFactory(Config config) {
    this.config = config;
    this.databaseDirectoryPath = resolveDatabaseDirectoryPath(config.databaseDirectory());
    Path databasePath = databaseDirectoryPath.resolve(config.databaseName()).normalize();
    this.databaseFilePath = Path.of(databasePath.toString() + ".mv.db");
    this.inMemoryModeJdbcUrl = "jdbc:h2:mem:" + config.databaseName() + ";DB_CLOSE_DELAY=-1";
    this.serverModeJdbcUrl = "jdbc:h2:tcp://localhost:" + config.tcpPort() + "/" + databasePath;
    this.embeddedModeJdbcUrl = "jdbc:h2:file:" + databasePath;
    this.mixedModeJdbcUrl = "jdbc:h2:" + databasePath + ";AUTO_SERVER=TRUE";
  }

  public static H2DataSourceFactory of(Config config) {
    return new H2DataSourceFactory(config);
  }

  private static Path resolveDatabaseDirectoryPath(Path databaseDirectoryPath) {
    final Set<String> allowPrefixes =
        Set.of("~/", "~\\", "./", ".\\", "%TEMP%", "$TMPDIR", "%USERPROFILE%");
    String databaseDirectoryPathStr = databaseDirectoryPath.toString();
    if (!databaseDirectoryPath.isAbsolute()
        && allowPrefixes.stream()
                .filter(prefix -> databaseDirectoryPathStr.startsWith(prefix))
                .count()
            == 0) {
      throw new IllegalArgumentException(
          "the databaseDirectory path should be startWith "
              + allowPrefixes
              + " or absolute path. The given is ["
              + databaseDirectoryPath
              + "]");
    }
    return SystemPropertyUtils.convertVariableInPath(databaseDirectoryPath);
  }

  @Override
  public String toString() {
    return "H2DataSourceFactory [config="
        + config
        + ", databaseDirectoryPath="
        + databaseDirectoryPath
        + ", databaseFilePath="
        + databaseFilePath
        + ", inMemoryModeJdbcUrl="
        + inMemoryModeJdbcUrl
        + ", serverModeJdbcUrl="
        + serverModeJdbcUrl
        + ", embeddedModeJdbcUrl="
        + embeddedModeJdbcUrl
        + ", mixedModeJdbcUrl="
        + mixedModeJdbcUrl
        + "]";
  }

  private static String toUrlOption(String[] options) {
    if (options.length == 0) {
      return "";
    }
    return ";" + String.join(";", options);
  }

  public String getInMemoryModeJdbcUrl() {
    return getInMemoryModeJdbcUrl(new String[0]);
  }

  public String getServerModeJdbcUrl() {
    return getServerModeJdbcUrl(new String[0]);
  }

  public String getEmbeddedModeJdbcUrl() {
    return getEmbeddedModeJdbcUrl(new String[0]);
  }

  public String getMixedModeJdbcUrl() {
    return getMixedModeJdbcUrl(new String[0]);
  }

  public String getInMemoryModeJdbcUrl(String... options) {
    return inMemoryModeJdbcUrl + toUrlOption(options);
  }

  public String getServerModeJdbcUrl(String... options) {
    return serverModeJdbcUrl + toUrlOption(options);
  }

  public String getEmbeddedModeJdbcUrl(String... options) {
    return embeddedModeJdbcUrl + toUrlOption(options);
  }

  public String getMixedModeJdbcUrl(String... options) {
    return mixedModeJdbcUrl + toUrlOption(options);
  }

  /**
   * Creates a new sever mode connection pool for H2 databases
   *
   * @return
   */
  public JdbcConnectionPool createInMemoryModeDataSource() {
    return JdbcConnectionPool.create(getInMemoryModeJdbcUrl(), getUsername(), getPassword());
  }

  /**
   * Creates a new sever mode connection pool for H2 databases
   *
   * @return
   */
  public JdbcConnectionPool createServerModeDataSource() {
    return JdbcConnectionPool.create(getServerModeJdbcUrl(), getUsername(), getPassword());
  }

  /**
   * Creates a new embedded mode connection pool for H2 databases
   *
   * @return
   */
  public JdbcConnectionPool createEmbeddedModeDataSource() {
    return JdbcConnectionPool.create(getEmbeddedModeJdbcUrl(), getUsername(), getPassword());
  }

  /**
   * Creates a new mixed mode connection pool for H2 databases
   *
   * @return
   */
  public JdbcConnectionPool createMixedModeDataSource() {
    return JdbcConnectionPool.create(getMixedModeJdbcUrl(), getUsername(), getPassword());
  }

  /**
   * Creates a new sever mode connection pool for H2 databases
   *
   * @return
   */
  public JdbcConnectionPool createInMemoryModeDataSource(String... options) {
    return JdbcConnectionPool.create(getInMemoryModeJdbcUrl(options), getUsername(), getPassword());
  }

  /**
   * Creates a new sever mode connection pool for H2 databases
   *
   * @return
   */
  public JdbcConnectionPool createServerModeDataSource(String... options) {
    return JdbcConnectionPool.create(getServerModeJdbcUrl(options), getUsername(), getPassword());
  }

  /**
   * Creates a new embedded mode connection pool for H2 databases
   *
   * @return
   */
  public JdbcConnectionPool createEmbeddedModeDataSource(String... options) {
    return JdbcConnectionPool.create(getEmbeddedModeJdbcUrl(options), getUsername(), getPassword());
  }

  /**
   * Creates a new mixed mode connection pool for H2 databases
   *
   * @return
   */
  public JdbcConnectionPool createMixedModeDataSource(String... options) {
    return JdbcConnectionPool.create(getMixedModeJdbcUrl(options), getUsername(), getPassword());
  }

  /**
   * Make a new database file in not exists
   *
   * @return true if and only if the directory was created,along with all necessary parent
   *     directories; false otherwise
   */
  public boolean makeDatabaseFileIfNotExists() {
    if (getDatabaseFilePath().toFile().exists()) {
      return false;
    }
    if (!databaseDirectoryPath.toFile().exists()) {
      databaseDirectoryPath.toFile().mkdirs();
    }
    try (Connection con =
        DriverManager.getConnection(getEmbeddedModeJdbcUrl(), getUsername(), getPassword())) {
      return true;
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  public Path getDatabaseFilePath() {
    return databaseFilePath;
  }

  public String getUsername() {
    return config.username();
  }

  public String getPassword() {
    return config.password();
  }

  public Path getDatabaseDirectoryPath() {
    return databaseDirectoryPath;
  }

  public String getDatabaseName() {
    return config.databaseName();
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Creates temporal an in memory data source.
   *
   * @return
   */
  public static DataSource createTemporalInMemoryDataSource() {
    return JdbcConnectionPool.create(
        "jdbc:h2:mem:" + UUID.randomUUID().toString() + ";DB_CLOSE_DELAY=-1", "", "");
  }

  public static class Builder {
    private Path databaseDirectory = Try.getOrElseThrow(() -> Files.createTempDirectory("h2db"));
    private String databaseName = "sormdb";
    private String username = "";
    private String password = "";
    private int tcpPort = 9092;

    public H2DataSourceFactory.Builder tcpPort(int tcpPort) {
      this.tcpPort = tcpPort;
      return this;
    }

    public H2DataSourceFactory.Builder username(String username) {
      this.username = username;
      return this;
    }

    public H2DataSourceFactory.Builder password(String password) {
      this.password = password;
      return this;
    }

    /**
     * Sets database directory.
     *
     * <p>following variables in the path of database directory will be expanded.
     *
     * <ul>
     *   <li>"~/" , "~\" or "%USERPROFILE%" to "user.home"
     *   <li>"%TEMP%" or "$TMPDIR" to "java.io.tmpdir"
     * </ul>
     *
     * @param databaseDirectory
     * @return
     */
    public H2DataSourceFactory.Builder databaseDirectory(Path databaseDirectory) {
      this.databaseDirectory = databaseDirectory;
      return this;
    }

    public H2DataSourceFactory.Builder databaseName(String databaseName) {
      this.databaseName = databaseName;
      return this;
    }

    /**
     * Builds a {@link H2DataSourceFactory} instance.
     *
     * @return
     */
    public H2DataSourceFactory build() {
      return new H2DataSourceFactory(
          new Config(databaseDirectory, databaseName, username, password, tcpPort));
    }
  }
}
