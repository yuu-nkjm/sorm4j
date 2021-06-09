package org.nkjmlab.sorm4j.extension.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nkjmlab.sorm4j.internal.util.StringUtils;

public class JulSormLogger extends AbstractSormLogger implements SormLogger {

  private final java.util.logging.Logger logger;
  private static java.util.logging.Logger defaultLogger = getDefaultLogger();



  public JulSormLogger(java.util.logging.Logger logger) {
    this.logger = logger;
  }

  private static Logger getDefaultLogger() {
    Logger logger = java.util.logging.Logger.getLogger(JulSormLogger.class.getName());
    logger.setLevel(Level.FINE);
    ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(Level.FINE);
    logger.addHandler(consoleHandler);
    return logger;
  }

  public static SormLogger getLogger() {
    return new JulSormLogger(defaultLogger);
  }

  @Override
  public void trace(String format, Object... params) {
    if (logger.isLoggable(Level.FINER)) {
      this.logger.finer(StringUtils.format(format, params));
    }
  }


  @Override
  public void debug(String format, Object... params) {
    if (logger.isLoggable(Level.FINE)) {
      this.logger.fine(StringUtils.format(format, params));
    }
  }

  @Override
  public void info(String format, Object... params) {
    if (logger.isLoggable(Level.INFO)) {
      this.logger.info(StringUtils.format(format, params));
    }
  }

  @Override
  public void warn(String format, Object... params) {
    if (logger.isLoggable(Level.WARNING)) {
      this.logger.warning(StringUtils.format(format, params));
    }
  }

  @Override
  public void error(String format, Object... params) {
    if (logger.isLoggable(Level.SEVERE)) {
      this.logger.severe(StringUtils.format(format, params));
    }
  }


}
