package org.nkjmlab.sorm4j.extension.logger;

import org.junit.jupiter.api.Test;

class Log4jSormLoggerTest {


  @Test
  void testTrace() {
    Log4jSormLogger.getLogger().trace("log");
  }

  @Test
  void testDebug() {
    Log4jSormLogger.getLogger().debug("log");
  }

  @Test
  void testInfo() {
    Log4jSormLogger.getLogger().info("log");
  }

  @Test
  void testWarn() {
    Log4jSormLogger.getLogger().warn("log");
  }

  @Test
  void testError() {
    Log4jSormLogger.getLogger().error("log");
  }

}
