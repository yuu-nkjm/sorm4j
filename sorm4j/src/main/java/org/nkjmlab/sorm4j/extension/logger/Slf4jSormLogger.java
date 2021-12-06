package org.nkjmlab.sorm4j.extension.logger;

import org.nkjmlab.sorm4j.internal.util.MethodInvokerInfoUtils;
import org.nkjmlab.sorm4j.internal.util.StringUtils;
import org.nkjmlab.sorm4j.internal.util.Try;

public class Slf4jSormLogger extends AbstractSormLogger implements SormLogger {

  public static final boolean enableLogger = isEnable();

  private static boolean isEnable() {
    return Try.getOrElse(() -> {
      Class.forName("org.slf4j.Logger");
      return true;
    }, false);
  }

  private final org.slf4j.Logger logger;

  private Slf4jSormLogger(org.slf4j.Logger logger) {
    this.logger = logger;
  }

  public static SormLogger getLogger() {
    if (!enableLogger) {
      System.err.println("sorm4j: [org.slf4j.Logger] is not found at the classpath. "
          + "If you want to use SLF4J, you should add SLF4J logger at the classpath.");
    }
    return new Slf4jSormLogger(org.slf4j.LoggerFactory.getLogger(Slf4jSormLogger.class));
  }

  @Override
  public void trace(String format, Object... params) {
    logger.trace(MethodInvokerInfoUtils.getInvokerInfo(1, new Throwable().getStackTrace())
        + StringUtils.format(format, params));
  }


  @Override
  public void debug(String format, Object... params) {
    logger.debug(MethodInvokerInfoUtils.getInvokerInfo(1, new Throwable().getStackTrace())
        + StringUtils.format(format, params));
  }

  @Override
  public void info(String format, Object... params) {
    logger.info(MethodInvokerInfoUtils.getInvokerInfo(1, new Throwable().getStackTrace())
        + StringUtils.format(format, params));
  }

  @Override
  public void warn(String format, Object... params) {
    logger.warn(MethodInvokerInfoUtils.getInvokerInfo(1, new Throwable().getStackTrace())
        + StringUtils.format(format, params));
  }

  @Override
  public void error(String format, Object... params) {
    logger.error(MethodInvokerInfoUtils.getInvokerInfo(1, new Throwable().getStackTrace())
        + StringUtils.format(format, params));
  }

  @Override
  public void trace(int depth, String format, Object... params) {
    logger
        .trace(MethodInvokerInfoUtils.getInvokerInfo(depth, new Throwable().getStackTrace())
            + StringUtils.format(format, params));
  }


  @Override
  public void debug(int depth, String format, Object... params) {
    logger
        .debug(MethodInvokerInfoUtils.getInvokerInfo(depth, new Throwable().getStackTrace())
            + StringUtils.format(format, params));
  }

  @Override
  public void info(int depth, String format, Object... params) {
    logger.info(MethodInvokerInfoUtils.getInvokerInfo(depth, new Throwable().getStackTrace())
        + StringUtils.format(format, params));
  }

  @Override
  public void warn(int depth, String format, Object... params) {
    logger.warn(MethodInvokerInfoUtils.getInvokerInfo(depth, new Throwable().getStackTrace())
        + StringUtils.format(format, params));
  }

  @Override
  public void error(int depth, String format, Object... params) {
    logger
        .error(MethodInvokerInfoUtils.getInvokerInfo(depth, new Throwable().getStackTrace())
            + StringUtils.format(format, params));
  }

}
