package org.nkjmlab.sorm4j.util;

import org.slf4j.Logger;

public final class LoggerFactory {
  public static Logger getLogger(Class<?> clazz) {
    return org.slf4j.LoggerFactory.getLogger(clazz);
  }

  public static Logger getLogger() {
    String className = getInvokerClassName(3);
    return org.slf4j.LoggerFactory.getLogger(className);

  }

  public static String getInvokerClassName(int depth) {
    StackTraceElement e = getStackTraceElement(depth);
    return e.getClassName() != null ? e.getClassName() : "";
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



}
