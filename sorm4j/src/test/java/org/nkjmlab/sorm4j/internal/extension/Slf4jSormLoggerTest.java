package org.nkjmlab.sorm4j.internal.extension;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.util.logger.Slf4jSormLogger;

class Slf4jSormLoggerTest {
  @Test
  void testTrace() {
    Slf4jSormLogger.getLogger().trace("trace {}", 1);
  }

  @Test
  void testDebug() {
    Slf4jSormLogger.getLogger().debug("debug {}", 1);
  }

  @Test
  void testInfo() {
    Slf4jSormLogger.getLogger().info("info {}", 1);
  }

  @Test
  void testWarn() {
    Slf4jSormLogger.getLogger().warn("warn {}", 1);
  }

  @Test
  void testError() {
    Slf4jSormLogger.getLogger().error("error {}", 1);
  }
}
