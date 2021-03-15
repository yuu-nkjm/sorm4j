package org.nkjmlab.sorm4j.core.util;

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
}
