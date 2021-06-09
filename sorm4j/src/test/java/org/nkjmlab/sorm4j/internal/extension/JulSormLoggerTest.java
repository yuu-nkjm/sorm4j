package org.nkjmlab.sorm4j.internal.extension;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.extension.logger.JulSormLogger;

class JulSormLoggerTest {

  @Test
  void testTrace() {
    JulSormLogger.getLogger().trace("trace {}", 1);
  }

  @Test
  void testDebug() {
    JulSormLogger.getLogger().debug("debug {}", 1);
  }

  @Test
  void testInfo() {
    JulSormLogger.getLogger().info("info {}", 1);
  }

  @Test
  void testWarn() {
    JulSormLogger.getLogger().warn("warn {}", 1);
  }

  @Test
  void testError() {
    JulSormLogger.getLogger().error("error {}", 1);
  }


}
