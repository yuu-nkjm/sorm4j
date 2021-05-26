package org.nkjmlab.sorm4j.internal.extension;

import org.junit.jupiter.api.Test;

class SysoutSormLoggerTest {

  @Test
  void testTrace() {
    SysoutSormLogger.getLogger(SysoutSormLoggerTest.class.getName()).trace("trace {}", 1);
  }

  @Test
  void testDebug() {
    SysoutSormLogger.getLogger(SysoutSormLoggerTest.class.getName()).debug("debug {}", 1);
  }

  @Test
  void testInfo() {
    SysoutSormLogger.getLogger(SysoutSormLoggerTest.class.getName()).info("info {}", 1);
  }

  @Test
  void testWarn() {
    SysoutSormLogger.getLogger(SysoutSormLoggerTest.class.getName()).warn("warn {}", 1);
  }

  @Test
  void testError() {
    SysoutSormLogger.getLogger(SysoutSormLoggerTest.class.getName()).error("error {}", 1);
  }


}
