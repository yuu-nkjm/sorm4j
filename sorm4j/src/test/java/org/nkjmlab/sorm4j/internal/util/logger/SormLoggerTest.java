package org.nkjmlab.sorm4j.internal.util.logger;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.util.logger.LoggerContext.Category.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.util.logger.LogPoint;
import org.nkjmlab.sorm4j.util.logger.SormLogger;

class SormLoggerTest {
  private static final StringWriter stringWriter = new StringWriter();
  private static Appender appender;

  @BeforeAll
  static void preapre() {
    appender = addAppender(stringWriter, "stringwriter");
  }

  @AfterAll
  static void finish() {
    appender.stop();
  }

  private static Appender addAppender(final Writer writer, final String writerName) {
    final LoggerContext context = LoggerContext.getContext(false);
    final Configuration config = context.getConfiguration();
    final PatternLayout layout = PatternLayout.createDefaultLayout(config);
    final Appender appender =
        WriterAppender.createAppender(layout, null, writer, writerName, false, true);
    appender.start();
    config.addAppender(appender);
    final Level level = null;
    final Filter filter = null;
    for (final LoggerConfig loggerConfig : config.getLoggers().values()) {
      loggerConfig.addAppender(appender, level, filter);
    }
    config.getRootLogger().addAppender(appender, level, filter);
    return appender;
  }


  @Test
  void testLogAfterQuery() {
    org.nkjmlab.sorm4j.util.logger.LoggerContext lc =
        org.nkjmlab.sorm4j.util.logger.LoggerContext.builder().enableAll().build();

    Sorm sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables(
        SormContext.builder().setLoggerContext(lc).build());

    Optional<LogPoint> lp = lc.createLogPoint(EXECUTE_QUERY, getClass());
    {
      String text = proc(stringWriter, () -> lp.get().logAfterQuery("one result"));
      assertThat(text.length()).isLessThan(300);
      assertThat(text).contains("Read [1] objects");
    }
    {
      String text = proc(stringWriter, () -> lp.get().logAfterMultiRow(new int[] {1}));
      assertThat(text.length()).isLessThan(300);
      assertThat(text).contains("Affect [1] objects");
    }
    {
      String text = proc(stringWriter, () -> lp.get().logAfterUpdate(1));
      assertThat(text.length()).isLessThan(300);
      assertThat(text).contains("Affect [1] rows");
    }

    try (Connection conn = sorm.getJdbcConnection()) {
      {
        String text = proc(stringWriter,
            () -> lp.get().logBeforeMultiRow(conn, SormLoggerTest.class, 1, "players"));
        assertThat(text.length()).isLessThan(500);
        assertThat(text).contains("players");
      }
      {
        String text = proc(stringWriter,
            () -> lp.get().logBeforeSql(conn, ParameterizedSql.of("select * from players")));
        assertThat(text.length()).isLessThan(400);
        assertThat(text).contains("select * from players");
      }
      {
        String text = proc(stringWriter,
            () -> lp.get().logBeforeSql(conn, "select * from players where id=?", 1));
        assertThat(text.length()).isLessThan(450);
        assertThat(text).contains("select * from players where id=1");
      }
      {
        String text = proc(stringWriter, () -> lp.get().logMapping("mapping"));
        assertThat(text.length()).isLessThan(300);
        assertThat(text).contains("mapping");
      }
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
    appender.stop();

  }



  @Test
  void testLog4jSormLoggerOut() throws IOException {
    SormLogger logger = new Log4jSormLogger(org.apache.logging.log4j.LogManager.getLogger());
    outTestHelper(logger);
  }

  @Test
  void testSlf4jSormLoggerOut() throws IOException {

    SormLogger logger =
        new Slf4jSormLogger(org.slf4j.LoggerFactory.getLogger(SormLoggerTest.class));
    outTestHelper(logger);
  }

  @Test
  void testJulSormLoggerOut() throws IOException {
    PrintStream syserr = System.err;
    ByteArrayOutputStream content = new ByteArrayOutputStream();
    System.setErr(new PrintStream(content));
    SormLogger logger = JulSormLogger.getLogger();
    outTestHelper(logger, content);
    System.setErr(syserr);
  }


  @Test
  void testLog4jSormLoggerError() {
    SormLogger logger = Log4jSormLogger.getLogger();
    errorTestHelper(logger);
  }

  @Test
  void testSlf4jSormLoggerError() {
    SormLogger logger = Slf4jSormLogger.getLogger();
    errorTestHelper(logger);
  }

  void testJulSormLoggerError() {
    PrintStream syserr = System.err;
    ByteArrayOutputStream content = new ByteArrayOutputStream();
    System.setErr(new PrintStream(content));
    SormLogger logger = JulSormLogger.getLogger();
    errorTestHelper(logger, content);
    System.setErr(syserr);
  }

  private void outTestHelper(SormLogger logger) throws IOException {
    {
      String text = proc(stringWriter, () -> logger.info("info test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("info test 1");
    }
    {
      String text = proc(stringWriter, () -> logger.info(1, "info test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("info test 1");
    }
    {
      String text = proc(stringWriter, () -> logger.debug("debug test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("debug test 1");
    }
    {
      String text = proc(stringWriter, () -> logger.debug(1, "debug test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("debug test 1");
    }
    {
      String text = proc(stringWriter, () -> logger.trace("trace test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("trace test 1");
    }
    {
      String text = proc(stringWriter, () -> logger.trace(1, "trace test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("trace test 1");
    }
  }


  private void outTestHelper(SormLogger logger, ByteArrayOutputStream target) throws IOException {
    {
      String text = proc(target, () -> logger.info("info test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("info test 1");
    }
    {
      String text = proc(target, () -> logger.info(1, "info test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("info test 1");
    }
    {
      String text = proc(target, () -> logger.debug("debug test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("debug test 1");
    }
    {
      String text = proc(target, () -> logger.debug(1, "debug test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("debug test 1");
    }
    {
      String text = proc(target, () -> logger.trace("trace test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("trace test 1");
    }
    {
      String text = proc(target, () -> logger.trace(1, "trace test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("trace test 1");
    }
  }

  private void errorTestHelper(SormLogger logger) {
    {
      String text = proc(stringWriter, () -> logger.error("error test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("error test 1");
    }
    {
      String text = proc(stringWriter, () -> logger.error(1, "error test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("error test 1");
    }
    {
      String text = proc(stringWriter, () -> logger.warn("warn test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("warn test 1");
    }
    {
      String text = proc(stringWriter, () -> logger.warn(1, "warn test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("warn test 1");
    }

  }

  private void errorTestHelper(SormLogger logger, ByteArrayOutputStream target) {
    {
      String text = proc(target, () -> logger.error("error test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("error test 1");
    }
    {
      String text = proc(target, () -> logger.error(1, "error test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("error test 1");
    }
    {
      String text = proc(target, () -> logger.warn("warn test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("warn test 1");
    }
    {
      String text = proc(target, () -> logger.warn(1, "warn test {}", 1));
      assertThat(text.length()).isLessThan(256);
      assertThat(text).contains("warn test 1");
    }

  }


  private String proc(ByteArrayOutputStream target, Runnable run) {
    target.reset();
    run.run();
    return target.toString();
  }

  private String proc(StringWriter sw, Runnable run) {
    sw.getBuffer().delete(0, sw.getBuffer().length());
    run.run();
    return sw.toString();

  }

}
