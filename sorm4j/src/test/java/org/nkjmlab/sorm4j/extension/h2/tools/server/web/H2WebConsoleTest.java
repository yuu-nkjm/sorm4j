package org.nkjmlab.sorm4j.extension.h2.tools.server.web;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class H2WebConsoleTest {

  @Test
  void testOpenBlocking() throws SQLException {
    Connection conn = SormTestUtils.createNewDatabaseDataSource().getConnection();
    CompletableFuture.runAsync(
        () -> {
          try {
            Thread.sleep(1000);
            conn.close();
          } catch (InterruptedException | SQLException e) {
          }
        });
    H2WebConsole.openBlocking(conn);
  }

  @Test
  void testOpen() throws SQLException {
    Connection conn = SormTestUtils.createNewDatabaseDataSource().getConnection();
    H2WebConsole.open(conn);
  }
}
