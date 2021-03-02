package org.nkjmlab.sorm4j.connectionsource;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public interface ConnectionSource {

  Connection getConnection() throws SQLException;

  DataSource getDataSource();

  static ConnectionSource of(String jdbcUrl, String user, String password) {
    return new DriverManagerConnectionSource(jdbcUrl, user, password);
  }

  static ConnectionSource of(DataSource dataSource) {
    return new DataSourceConnectionSource(dataSource);
  }
}
