package org.nkjmlab.sorm4j.util.logger;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.internal.util.logger.JulSormLogger;
import org.nkjmlab.sorm4j.internal.util.logger.Log4jSormLogger;
import org.nkjmlab.sorm4j.internal.util.logger.Slf4jSormLogger;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class SormLoggerTest {

  private static List<SormLogger> loggers =
      List.of(JulSormLogger.getLogger(), Log4jSormLogger.getLogger(), Slf4jSormLogger.getLogger());

  @Test
  void testLogAfterQuery() {
    loggers.get(0).logAfterQuery("result", 1L, "obj");
    LoggerContext lc = SormTestUtils.SORM.getContext().getLoggerContext();
    lc.enableForceLogging();
    // lc.createLogPoint(LoggerContext.Category.EXECUTE_QUERY,
    // getClass()).get().logAfterQuery("obj");
    lc.disableForceLogging();

  }

  @Test
  void testTrace() {
    loggers.forEach(logger -> {
      logger.trace("trace {}", 1);
      logger.trace(1, "trace {}", 1);
    });
  }

  @Test
  void testDebug() {
    loggers.forEach(logger -> {
      logger.debug("debug {}", 1);
      logger.debug(1, "debug {}", 1);
    });
  }

  @Test
  void testInfo() {
    loggers.forEach(logger -> {
      logger.info("info {}", 1);
      logger.info(1, "info {}", 1);
    });
  }

  @Test
  void testWarn() {
    loggers.forEach(logger -> {
      logger.warn("warn {}", 1);
      logger.warn(1, "warn {}", 1);
    });
  }

  @Test
  void testError() {
    loggers.forEach(logger -> {
      logger.error("error {}", 1);
      logger.error(1, "error {}", 1);
    });
  }


}
