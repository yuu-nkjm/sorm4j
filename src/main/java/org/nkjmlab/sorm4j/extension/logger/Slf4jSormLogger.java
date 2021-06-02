package org.nkjmlab.sorm4j.extension.logger;

import org.nkjmlab.sorm4j.internal.util.Try;

public class Slf4jSormLogger implements SormLogger {

  public static final boolean enableLogger = isEnable();

  private static boolean isEnable() {
    boolean ret = Try.getOrDefault(() -> {
      Class.forName("org.slf4j.Logger");
      return true;
    }, false);
    // if (!ret) {
    // System.err.println("sorm4j: [org.slf4j.Logger] is not found at the classpath. "
    // + "If you want to use SLF4J, you should add SLF4J logger at the classpath.");
    // }
    return ret;
  }

  private final org.slf4j.Logger logger;

  private Slf4jSormLogger(org.slf4j.Logger logger) {
    this.logger = logger;
  }

  public static SormLogger getLogger() {
    return new Slf4jSormLogger(org.slf4j.LoggerFactory.getLogger(Slf4jSormLogger.class));
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
