package org.nkjmlab.sorm4j.mapping;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.ConnectionSource;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.util.Try;

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
        OrmException::new);
  }

  @Override
  public DataSource getDataSource() {
    throw new UnsupportedOperationException();
  }



}
