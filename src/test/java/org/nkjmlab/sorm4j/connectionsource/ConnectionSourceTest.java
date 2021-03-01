package org.nkjmlab.sorm4j.connectionsource;

import java.sql.SQLException;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;

class ConnectionSourceTest {

  String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;";
  String user = "sa";
  String pw = "";

  @Test
  void test() {
    JdbcConnectionPool cp = JdbcConnectionPool.create(url, user, pw);
    DataSourceConnectionSource cs = new DataSourceConnectionSource(cp);
    org.assertj.core.api.Assertions.assertThat(cs.getDataSource()).isEqualTo(cp);
    org.assertj.core.api.Assertions.assertThat(cs.toString())
        .contains(DataSourceConnectionSource.class.getSimpleName());
    try {
      org.assertj.core.api.Assertions.assertThat(cs.getConnection()).isNotNull();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    Sorm.of(cs);
  }

}
