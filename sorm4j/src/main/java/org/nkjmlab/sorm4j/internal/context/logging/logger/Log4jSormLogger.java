package org.nkjmlab.sorm4j.internal.context.logging.logger;

import org.apache.logging.log4j.Level;
import org.nkjmlab.sorm4j.context.logging.SormLogger;
import org.nkjmlab.sorm4j.internal.util.MethodInvokerInfoUtils;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;
import org.nkjmlab.sorm4j.internal.util.Try;

public final class Log4jSormLogger extends AbstractSormLogger implements SormLogger {

  public static final boolean enableLogger = isEnable();

  private static boolean isEnable() {
    return Try.getOrElse(
        () -> {
          Class.forName("org.apache.logging.log4j.Logger");
          return true;
        },
        false);
  }

  private final org.apache.logging.log4j.Logger logger;
  private final ParameterizedStringFormatter logMessageFormatter;

  public static SormLogger getLogger() {
    return getLogger(ParameterizedStringFormatter.LENGTH_512);
  }

  public static SormLogger getLogger(ParameterizedStringFormatter logMessageFormatter) {
    if (!enableLogger) {
      System.err.println(
          "sorm4j: [org.apache.logging.log4j.Logger] is not found at the classpath."
              + "If you want to use Log4j2, you should add Log4j2 logger at the classpath.");
    }
    return new Log4jSormLogger(
        org.apache.logging.log4j.LogManager.getLogger(), logMessageFormatter);
  }

  private Log4jSormLogger(
      org.apache.logging.log4j.Logger logger, ParameterizedStringFormatter logMessageFormatter) {
    this.logger = logger;
    this.logMessageFormatter = logMessageFormatter;
  }

  @Override
  public void trace(String format, Object... params) {
    printf(2, Level.TRACE, format, params);
  }

  @Override
  public void debug(String format, Object... params) {
    printf(2, Level.DEBUG, format, params);
  }

  private void printf(int depth, Level level, String format, Object... params) {
    this.logger.printf(
        level,
        System.lineSeparator()
            + "  "
            + MethodInvokerInfoUtils.getInvokerInfo(depth, new Throwable().getStackTrace())
            + logMessageFormatter.format(format, params));
  }

  @Override
  public void info(String format, Object... params) {
    printf(2, Level.INFO, format, params);
  }

  @Override
  public void warn(String format, Object... params) {
    printf(2, Level.WARN, format, params);
  }

  @Override
  public void error(String format, Object... params) {
    printf(2, Level.ERROR, format, params);
  }

  @Override
  public void trace(int depth, String format, Object... params) {
    printf(depth, Level.TRACE, format, params);
  }

  @Override
  public void debug(int depth, String format, Object... params) {
    printf(depth, Level.DEBUG, format, params);
  }

  @Override
  public void info(int depth, String format, Object... params) {
    printf(depth, Level.INFO, format, params);
  }

  @Override
  public void warn(int depth, String format, Object... params) {
    printf(depth, Level.WARN, format, params);
  }

  @Override
  public void error(int depth, String format, Object... params) {
    printf(depth, Level.ERROR, format, params);
  }
}
