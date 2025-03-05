package org.nkjmlab.sorm4j.util.h2.tools.server;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.util.h2.tools.server.H2Startup;

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
