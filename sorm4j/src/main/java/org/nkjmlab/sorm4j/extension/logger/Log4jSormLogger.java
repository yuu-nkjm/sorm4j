package org.nkjmlab.sorm4j.extension.logger;

import org.nkjmlab.sorm4j.internal.util.Try;

public class Log4jSormLogger extends AbstractSormLogger implements SormLogger {

  public static final boolean enableLogger = isEnable();

  private static boolean isEnable() {
    return Try.getOrDefault(() -> {
      Class.forName("org.apache.logging.log4j.Logger");
      return true;
    }, false);
  }

  private final org.apache.logging.log4j.Logger logger;

  public static SormLogger getLogger() {
    if (!enableLogger) {
      System.err.println("sorm4j: [org.apache.logging.log4j.Logger] is not found at the classpath."
          + "If you want to use Log4j2, you should add Log4j2 logger at the classpath.");
    }
    return new Log4jSormLogger(org.apache.logging.log4j.LogManager.getLogger());
  }

  private Log4jSormLogger(org.apache.logging.log4j.Logger logger) {
    this.logger = logger;
  }


  @Override
  public void trace(String format, Object... params) {
    if (logger.isTraceEnabled()) {
      this.logger.trace(format, params);
    }
  }


  @Override
  public void debug(String format, Object... params) {
    if (logger.isDebugEnabled()) {
      this.logger.debug(format, params);
    }
  }

  @Override
  public void info(String format, Object... params) {
    if (logger.isInfoEnabled()) {
      this.logger.info(format, params);
    }
  }

  @Override
  public void warn(String format, Object... params) {
    if (logger.isWarnEnabled()) {
      this.logger.warn(format, params);
    }
  }

  @Override
  public void error(String format, Object... params) {
    if (logger.isErrorEnabled()) {
      this.logger.error(format, params);
    }
  }



}