package org.nkjmlab.sorm4j.util.h2.server;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class H2StartupTest {

  @Test
  public void testStartDefaultLocalTcpServer() {
    boolean result = H2Startup.startDefaultLocalTcpServer();
    assertTrue(result);
  }

  @Test
  public void testStartDefaultWebConsole() {
    boolean result = H2Startup.startDefaultWebConsole();
    assertTrue(result);
  }
}
