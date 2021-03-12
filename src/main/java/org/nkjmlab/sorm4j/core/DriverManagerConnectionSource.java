package org.nkjmlab.sorm4j.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.ConnectionSource;
import org.nkjmlab.sorm4j.core.util.Try;

/**
 * A database connection source wrapped a {@link DriverManager}
 *
 * @author nkjm
 *
 */

public class DriverManagerConnectionSource implements ConnectionSource {

  private final String jdbcUrl;
  private final String user;
  private final String password;

  public DriverManagerConnectionSource(String jdbcUrl, String user, String password) {
    this.jdbcUrl = jdbcUrl;
    this.user = user;
    this.password = password;
  }

  @Override
  public Connection getConnection() throws SQLException {
    return Try.getOrThrow(() -> DriverManager.getConnection(jdbcUrl, user, password),
        Try::rethrow);
  }

  @Override
  public DataSource getDataSource() {
    throw new UnsupportedOperationException();
  }



}
