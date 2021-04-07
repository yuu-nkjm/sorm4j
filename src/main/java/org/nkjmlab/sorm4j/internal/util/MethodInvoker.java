package org.nkjmlab.sorm4j.internal.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class MethodInvoker {
  public MethodInvoker() {}

  private static final DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  /**
   * Gets summary of about method invoker.
   *
   * @param depth
   * @param label DEBUG, INFO, ERROR ....
   * @return
   */
  public static String getSummary(int depth, String label) {
    StackTraceElement e = getStackTraceElement(depth);
    return dateTimeFormatter.format(LocalDateTime.now()) + " " + String.format("%5s", label)
        + " [" + Thread.currentThread().getName() + "] " + getInvokerClassName(e) + "."
        + getInvokerMethodName(e) + "(" + getInvokerFileName(e) + ":" + getInvokerLineNumber(e)
        + ")";
  }

  private static StackTraceElement getStackTraceElement(int index) {
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    if (index < 0) {
      return stackTrace[0];
    } else if (index >= stackTrace.length) {
      return stackTrace[stackTrace.length - 1];
    } else {
      return stackTrace[index];
    }
  }

  private static String getInvokerClassName(StackTraceElement e) {
    return e.getClassName() != null ? e.getClassName() : "";
  }

  private static String getInvokerMethodName(StackTraceElement e) {
    return e.getMethodName() != null ? e.getMethodName() : "";
  }

  private static String getInvokerFileName(StackTraceElement e) {
    return e.getFileName() != null ? e.getFileName() : "";
  }

  private static int getInvokerLineNumber(StackTraceElement e) {
    return e.getLineNumber();
  }
}