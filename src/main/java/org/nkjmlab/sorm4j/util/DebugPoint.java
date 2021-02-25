package org.nkjmlab.sorm4j.util;

public final class DebugPoint {

  public final String name;
  private final long startTime;

  public DebugPoint(String name) {
    this.name = name;
    this.startTime = System.nanoTime();
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "(" + name + ")";
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
