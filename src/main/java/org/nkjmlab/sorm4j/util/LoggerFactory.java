package org.nkjmlab.sorm4j.util;

import org.slf4j.Logger;

public final class LoggerFactory {

  public static Logger getLogger() {
    String className = getInvokerClassName(3);
    return org.slf4j.LoggerFactory.getLogger(className);
  }

  public static String getInvokerClassName(int depth) {
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    StackTraceElement e = stackTrace[stackTrace.length - 1];
    return e.getClassName() != null ? e.getClassName() : "";
  }



}
