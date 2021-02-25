
package org.nkjmlab.sorm4j.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Function;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcConnectionPool;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceHelper {

  private final DataSource dataSource;

  public DataSourceHelper(String propertiesFilePath,
      Function<String, Function<String, Function<String, DataSource>>> dataSourceFunction) {
    try {
      Properties properties = new Properties();
      properties.load(
          Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFilePath));
      String url = properties.getProperty("url");
      String user = properties.getProperty("user");
      String password = properties.getProperty("password");
      this.dataSource = dataSourceFunction.apply(url).apply(user).apply(password);
    } catch (IOException e) {
      throw new RuntimeException("Could not load " + propertiesFilePath + " from the classpath");
    }
  }

  public static DataSource createDataSourceH2(String url, String user, String password) {
    return JdbcConnectionPool.create(url, user, password);
  }

  public static DataSource createDataSourceHikari(String url, String user, String password) {
    HikariConfig config = new HikariConfig();
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.setJdbcUrl(url);
    config.setUsername(user);
    config.setPassword(password);
    return new HikariDataSource(config);
  }


  public Connection getConnection() {
    try {
      return dataSource.getConnection();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


  public DataSource getDataSource() {
    return dataSource;
  }


}
