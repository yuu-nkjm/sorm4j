package org.nkjmlab.sorm4j.core.util;

import org.slf4j.Logger;

public final class LoggerFactory {

  private LoggerFactory() {}

  public static Logger getLogger() {
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    String className = stackTrace[1].getClassName();
    return org.slf4j.LoggerFactory.getLogger(className);
  }

}
