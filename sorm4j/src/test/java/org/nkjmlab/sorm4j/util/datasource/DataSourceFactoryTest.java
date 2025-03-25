package org.nkjmlab.sorm4j.util.datasource;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

class DataSourceFactoryTest {

  @Test
  void testCreateString() throws SQLException {
    DataSource dataSource = DataSourceFactory.create("jdbc:h2:mem:test234;DB_CLOSE_DELAY=-1;");
    dataSource.getConnection();
  }

  @Test
  void testCreateStringStringString() throws SQLException {
    DataSource dataSource =
        DataSourceFactory.create(
            "jdbc:h2:mem:test9890111;DB_CLOSE_DELAY=-1;", "username", "password");
    dataSource.getConnection();
  }
}
