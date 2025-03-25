package org.nkjmlab.sorm4j.internal.util.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * A {@link DataSource} implementation that provides a simple way to create a database connection
 * using a JDBC URL, with optional username and password.
 *
 * <p>This interface offers factory methods to create a {@link DriverManagerDataSource} instance.
 *
 * <p>For example:
 *
 * <pre>
 * <code>
 *    DriverManagerDataSource dataSource = DriverManagerDataSource.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
 * </code>
 * </pre>
 */
public class DriverManagerDataSource implements DataSource {
  private final String jdbcUrl;
  private final String username;
  private final String password;

  private DriverManagerDataSource(String jdbcUrl, String username, String password) {
    this.jdbcUrl = jdbcUrl;
    this.username = username;
    this.password = password;
  }

  public static DriverManagerDataSource of(String jdbcUrl, String username, String password) {
    return new DriverManagerDataSource(jdbcUrl, username, password);
  }

  @Override
  public Connection getConnection() throws SQLException {
    return username != null
        ? DriverManager.getConnection(jdbcUrl, username, password)
        : DriverManager.getConnection(jdbcUrl);
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return DriverManager.getConnection(jdbcUrl, username, password);
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return DriverManager.getLogWriter();
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    DriverManager.setLogWriter(out);
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    DriverManager.setLoginTimeout(seconds);
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return DriverManager.getLoginTimeout();
  }
}
