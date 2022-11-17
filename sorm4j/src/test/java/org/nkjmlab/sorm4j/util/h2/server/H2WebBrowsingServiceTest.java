package org.nkjmlab.sorm4j.util.h2.server;

import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.common.DriverManagerDataSource;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

class H2WebBrowsingServiceTest {

  @Test
  void test() throws StreamReadException, DatabindException, IOException, SQLException {
    DriverManagerDataSource ds = SormTestUtils.createNewDatabaseDataSource();
    H2WebBrowsingService server = new H2WebBrowsingService(true);
    server.open(ds);
    server.open(ds.getConnection());
    server.getWebServer().stop();
  }


}
