package org.nkjmlab.sorm4j.extension.h2.tools.server.tcp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class H2TcpServerTest {

  private static H2TcpServer tcpServer;

  @BeforeAll
  static void setUp() {
    tcpServer = H2TcpServer.builder("password").build();
  }

  @AfterAll
  static void tearDown() {
    if (tcpServer != null) {
      try {
        tcpServer.stop();
      } catch (SQLException e) {
        fail("Unexpected SQLException during stop: " + e.getMessage());
      }
    }
  }

  @Test
  void testTcpServerIsRunning() {
    assertNotNull(tcpServer, "TcpServer instance should not be null");

    tcpServer.start();
    assertTrue(tcpServer.isRunning(), "TcpServer should be running");

    try {
      tcpServer.stop();
    } catch (SQLException e) {
      fail("Unexpected SQLException during stop: " + e.getMessage());
    }
    assertFalse(tcpServer.isRunning(), "TcpServer should be stopped");
  }

  @Test
  void testIsPortFree() {
    int testPort = 9093;
    boolean isFree;

    try (ServerSocket socket = new ServerSocket(testPort)) {
      isFree = true;
    } catch (IOException e) {
      isFree = false;
    }

    assertEquals(
        isFree, tcpServer.isPortFree(), "Port availability check should match actual state");
  }

  @Test
  void testBuilderConfigurations() {
    H2TcpServer server =
        H2TcpServer.builder("secure-pass")
            .tcpPort(9094)
            .tcpSSL(true)
            .tcpAllowOthers(true)
            .tcpDaemon(true)
            .trace(true)
            .ifExists(false)
            .ifNotExists(true)
            .baseDir("/tmp/h2")
            .key("from", "to")
            .build();

    assertNotNull(server, "Builder should create a non-null H2TcpServer instance");
    assertEquals(9094, server.getPort(), "Port should be set correctly");
  }

  @Test
  void testStopWithoutStart() {
    H2TcpServer newServer = H2TcpServer.builder("password").build();
    ;

    assertDoesNotThrow(
        () -> newServer.stop(), "Stopping a non-started server should not throw an exception");
  }

  @Test
  void testStartStopMultipleTimes() {
    tcpServer.start();
    assertTrue(tcpServer.isRunning(), "TcpServer should be running");

    tcpServer.start();
    assertTrue(tcpServer.isRunning(), "Second start should not affect running state");

    try {
      tcpServer.stop();
    } catch (SQLException e) {
      fail("Unexpected SQLException during stop: " + e.getMessage());
    }
    assertFalse(tcpServer.isRunning(), "TcpServer should be stopped");

    try {
      tcpServer.stop();
    } catch (SQLException e) {
      fail("Unexpected SQLException during second stop: " + e.getMessage());
    }
    assertFalse(tcpServer.isRunning(), "Second stop should not throw an error");
  }

  @Test
  void testGetTcpServer() throws SQLException {
    tcpServer.start();
    assertNotNull(tcpServer.getTcpServer(), "TcpServer instance should not be null");
    tcpServer.stop();
  }
}
