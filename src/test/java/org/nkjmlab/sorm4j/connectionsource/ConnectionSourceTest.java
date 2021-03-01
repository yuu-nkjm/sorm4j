package org.nkjmlab.sorm4j.connectionsource;

import java.sql.SQLException;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.Test;

class ConnectionSourceTest {

  String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;";
  String user = "sa";
  String pw = "";

  @Test
  void test() {
    JdbcConnectionPool cp = JdbcConnectionPool.create(url, user, pw);
    DataSourceConnectionSource ds = new DataSourceConnectionSource(cp);
    org.assertj.core.api.Assertions.assertThat(ds.getDataSource()).isEqualTo(cp);
    org.assertj.core.api.Assertions.assertThat(ds.toString())
        .contains(DataSourceConnectionSource.class.getSimpleName());
    try {
      org.assertj.core.api.Assertions.assertThat(ds.getConnection()).isNotNull();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
