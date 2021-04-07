package org.nkjmlab.sorm4j.internal.util;

public final class LogPoint {

  private final String name;
  private final long startTime;

  public LogPoint(String name) {
    this.name = name;
    this.startTime = System.nanoTime();
  }

  public String getTagAndElapsedTime() {
    return "[" + getTag() + "]" + " ["
        + String.format("%.3f", (double) (System.nanoTime() - startTime) / 1000 / 1000)
        + " msec] :";
  }

  public String getTag() {
    return name + ":" + (hashCode() / 10000);
  }


  public void info(Class<?> clazz, String msg, Object... params) {
    LoggerFactory.info(clazz, msg, params);
  }

  public void debug(Class<?> clazz, String msg, Object... params) {
    LoggerFactory.debug(clazz, msg, params);
  }

  public void trace(Class<?> clazz, String msg, Object... params) {
    LoggerFactory.trace(clazz, msg, params);
  }


}
