package org.nkjmlab.sorm4j.util.logger;

import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.internal.util.logger.JulSormLogger;
import org.nkjmlab.sorm4j.internal.util.logger.Log4jSormLogger;
import org.nkjmlab.sorm4j.internal.util.logger.Slf4jSormLogger;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

class SormLoggerTest {

  private static List<SormLogger> loggers =
      List.of(JulSormLogger.getLogger(), Log4jSormLogger.getLogger(), Slf4jSormLogger.getLogger());



  @Test
  void testLogAfterQuery() {
    Sorm sorm = createSormWithNewContextAndTables();

    LoggerContext lc = sorm.getContext().getLoggerContext();
    lc.enableForceLogging();
    Optional<LogPoint> lp = lc.createLogPoint(LoggerContext.Category.EXECUTE_QUERY, getClass());
    lp.get().logAfterQuery("obj");
    lp.get().logAfterMultiRow(new int[] {1});
    lp.get().logAfterUpdate(1);
    try (Connection conn = sorm.getJdbcConnection()) {
      lp.get().logBeforeMultiRow(conn, SormLoggerTest.class, 1, "players");
      lp.get().logBeforeSql(conn, ParameterizedSql.of("select * from players"));
      lp.get().logBeforeSql(conn, "select * from players where id=1", 1);
      lp.get().logMapping("mapping info");
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
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
