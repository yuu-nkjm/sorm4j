package org.nkjmlab.sorm4j.core.connectionsource;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.ConnectionSource;

/**
 * A database connection source wrapped a {@link DataSource}
 *
 * @author nkjm
 *
 */
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

  @Override
  public String toString() {
    return "DataSourceConnectionSource [dataSource=" + dataSource + "]";
  }

}
