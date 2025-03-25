package org.nkjmlab.sorm4j.extension.h2.tools.server.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class H2WebServerTest {

  private static H2WebServer webServer;
  private static DataSource dataSource;

  @BeforeEach
  void setUp() {
    JdbcDataSource ds = new JdbcDataSource();
    ds.setURL("jdbc:h2:mem:testdbwebconsole;DB_CLOSE_DELAY=-1");
    ds.setUser("");
    ds.setPassword("");
    dataSource = ds;

    webServer = H2WebServer.builder().webDaemon(true).build();
  }

  @AfterEach
  void tearDown() {
    if (webServer != null) {
      webServer.stop();
    }
  }

  @Test
  void testWebServerLifecycle() {
    assertFalse(webServer.isRunning(), "WebServer should be initially stopped");
    webServer.start();
    assertTrue(webServer.isRunning(), "WebServer should be running after start()");
    webServer.stop();
    assertFalse(webServer.isRunning(), "WebServer should be stopped after stop()");
  }

  @Test
  void testPortAvailability() {
    assertTrue(webServer.isPortFree(), "Port should be free before starting server");
    webServer.start();
    assertFalse(webServer.isPortFree(), "Port should be in use after starting server");
  }

  @Test
  void testGetServerInfo() {
    webServer.start();
    assertNotNull(webServer.getServer(), "getServer() should not return null");
    assertNotNull(webServer.getService(), "getService() should not return null");
    assertNotNull(webServer.getStatus(), "getStatus() should not return null");
  }

  @Test
  void testCreateSession() throws SQLException {
    webServer.start();
    String sessionUrl =
        webServer.createSession(SormTestUtils.createNewDatabaseDataSource().getConnection());
    assertNotNull(sessionUrl, "Session URL should not be null");
    assertTrue(sessionUrl.startsWith("http"), "Session URL should start with http");
  }

  @Test
  void testCreateSessionWithConnection() throws SQLException {
    Connection conn = dataSource.getConnection();
    webServer.start();
    String sessionUrl = webServer.createSession(conn);
    assertNotNull(sessionUrl, "Session URL should not be null");
    assertTrue(sessionUrl.startsWith("http"), "Session URL should start with http");
  }

  @Test
  void testBuilderPattern() {
    H2WebServer.Builder builder =
        H2WebServer.builder()
            .webPort(9090)
            .webSSL(true)
            .webAllowOthers(true)
            .baseDir("/testDir")
            .trace(true)
            .ifExists(true)
            .ifNotExists(true);
    H2WebServer builtServer = builder.build();
    assertEquals(9090, builtServer.getPort(), "Expected server port is 9090");
  }
}
