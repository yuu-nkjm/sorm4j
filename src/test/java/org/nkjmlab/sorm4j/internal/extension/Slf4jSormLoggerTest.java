package org.nkjmlab.sorm4j.internal.extension;

import org.junit.jupiter.api.Test;

class Slf4jSormLoggerTest {
  @Test
  void testTrace() {
    Slf4jSormLogger.getLogger(Slf4jSormLoggerTest.class.getName()).trace("trace {}", 1);
  }

  @Test
  void testDebug() {
    Slf4jSormLogger.getLogger(Slf4jSormLoggerTest.class.getName()).debug("debug {}", 1);
  }

  @Test
  void testInfo() {
    Slf4jSormLogger.getLogger(Slf4jSormLoggerTest.class.getName()).info("info {}", 1);
  }

  @Test
  void testWarn() {
    Slf4jSormLogger.getLogger(Slf4jSormLoggerTest.class.getName()).warn("warn {}", 1);
  }

  @Test
  void testError() {
    Slf4jSormLogger.getLogger(Slf4jSormLoggerTest.class.getName()).error("error {}", 1);
  }
}
