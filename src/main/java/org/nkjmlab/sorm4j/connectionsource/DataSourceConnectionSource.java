package org.nkjmlab.sorm4j.connectionsource;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class DataSourceConnectionSource implements ConnectionSource {

  private final DataSource dataSource;

  public DataSourceConnectionSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  @Override
  public DataSource getDataSource() {
    return dataSource;
  }
}
