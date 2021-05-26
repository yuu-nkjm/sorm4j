package org.nkjmlab.sorm4j.internal.extension;

import org.nkjmlab.sorm4j.internal.util.Try;
import org.slf4j.Logger;

public class Slf4jSormLogger implements SormLogger {

  public static final boolean enableLogger = isEnableSlf4j();

  private static boolean isEnableSlf4j() {
    boolean ret = Try.getOrDefault(() -> {
      Class.forName("org.slf4j.Logger");
      return true;
    }, false);
    if (!ret) {
      System.err.println("sorm4j: [org.slf4j.Logger] is not found at the classpath. "
          + "If you want to use SLF4J, you should add SLF4J logger at the classpath.");
    }
    return ret;
  }

  private Logger logger;

  private Slf4jSormLogger(Logger logger) {
    this.logger = logger;
  }

  @Override
  public void trace(String format, Object... arguments) {
    this.logger.trace(format, arguments);
  }

  @Override
  public void debug(String format, Object... arguments) {
    this.logger.debug(format, arguments);
  }

  @Override
  public void info(String format, Object... arguments) {
    this.logger.info(format, arguments);

  }

  @Override
  public void warn(String format, Object... arguments) {
    this.logger.warn(format, arguments);
  }

  @Override
  public void error(String format, Object... arguments) {
    this.logger.error(format, arguments);
  }

  public static SormLogger getLogger(String className) {
    return new Slf4jSormLogger(org.slf4j.LoggerFactory.getLogger(className));
  }


}
