package org.nkjmlab.sorm4j.util.h2.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.util.datasource.DriverManagerDataSource;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

class H2WebBrowsingServiceTest {

  @Test
  void test() throws StreamReadException, DatabindException, IOException, SQLException {
    DriverManagerDataSource ds = SormTestUtils.createNewDatabaseDataSource();
    H2WebBrowsingService server = new H2WebBrowsingService(true);
    Connection conn = ds.getConnection();
    conn.close();
    server.open(conn);
    server.getWebServer().stop();
  }
}
