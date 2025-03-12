package org.nkjmlab.sorm4j.extension.h2.tools.server.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.server.web.WebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class H2WebConsoleServiceTest {

  private static H2WebConsoleService h2WebConsoleService;
  private static DataSource dataSource;

  @BeforeAll
  static void setUp() {
    JdbcDataSource ds = new JdbcDataSource();
    ds.setURL("jdbc:h2:mem:testdbwebconsole;DB_CLOSE_DELAY=-1");
    ds.setUser("");
    ds.setPassword("");
    dataSource = ds;

    h2WebConsoleService = new H2WebConsoleService(dataSource);
  }

  @AfterAll
  static void tearDown() {
    if (h2WebConsoleService != null) {
      h2WebConsoleService.stop();
    }
  }

  @Test
  void testWebServerIsRunning() {
    WebServer webServer = h2WebConsoleService.getWebServer();
    assertNotNull(webServer, "WebServer should not be null");
    assertTrue(webServer.isRunning(false), "WebServer should be running");
    webServer.stop();
    assertFalse(webServer.isRunning(false), "WebServer should be stopped");
  }

  @Test
  void testOpenSession() {
    String url = h2WebConsoleService.openBrowser();
    assertNotNull(url, "Session URL should not be null");
    assertTrue(url.startsWith("http"), "Session URL should start with http");
  }
}
