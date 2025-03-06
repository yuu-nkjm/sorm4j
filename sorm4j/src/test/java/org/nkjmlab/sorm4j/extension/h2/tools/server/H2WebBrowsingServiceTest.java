package org.nkjmlab.sorm4j.extension.h2.tools.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

class H2WebBrowsingServiceTest {

  @Test
  void test() throws StreamReadException, DatabindException, IOException, SQLException {
    DataSource ds = SormTestUtils.createNewDatabaseDataSource();
    H2WebBrowsingService server = new H2WebBrowsingService(true);
    Connection conn = ds.getConnection();
    conn.close();
    server.open(conn);
    server.getWebServer().stop();
  }
}
