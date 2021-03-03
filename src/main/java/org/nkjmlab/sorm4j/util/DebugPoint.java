package org.nkjmlab.sorm4j.util;

public final class DebugPoint {

  private final String name;
  private final long startTime;

  public DebugPoint(String name) {
    this.name = name;
    this.startTime = System.nanoTime();
  }

  public String getFormattedNameAndElapsedTime() {
    return "[" + name + "]" + " ["
        + String.format("%.3f", (double) (System.nanoTime() - startTime) / 1000 / 1000)
        + " msec] :";
  }

  public String getName() {
    return name;
  }
}
