package org.nkjmlab.sorm4j.extension.logger;

import java.util.logging.Level;
import org.nkjmlab.sorm4j.internal.util.StringUtils;

public class JulSormLogger implements SormLogger {

  private final java.util.logging.Logger logger;


  public JulSormLogger(java.util.logging.Logger logger) {
    this.logger = logger;
  }

  public static SormLogger getLogger() {
    return new JulSormLogger(java.util.logging.Logger.getLogger(JulSormLogger.class.getName()));
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
