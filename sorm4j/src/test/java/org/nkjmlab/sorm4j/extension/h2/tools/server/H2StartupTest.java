package org.nkjmlab.sorm4j.extension.h2.tools.server;

import org.junit.jupiter.api.Test;

class H2StartupTest {

  @Test
  public void testStartDefaultLocalTcpServer() {
    H2Startup.startDefaultLocalTcpServer();
  }

  @Test
  public void testStartDefaultWebConsole() {
    H2Startup.startDefaultWebConsole();
  }
}
