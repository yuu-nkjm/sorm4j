package org.nkjmlab.sorm4j.extension.logger;

public class NopSormLogger implements SormLogger {

  public NopSormLogger() {}

  public static SormLogger getLogger() {
    return new NopSormLogger();
  }

  @Override
  public void trace(String format, Object... params) {}


  @Override
  public void debug(String format, Object... params) {}

  @Override
  public void info(String format, Object... params) {}

  @Override
  public void warn(String format, Object... params) {}

  @Override
  public void error(String format, Object... params) {}
}
