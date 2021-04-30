package org.nkjmlab.sorm4j.internal.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public final class LoggerFactory {

  private LoggerFactory() {}

  private static final boolean enableLogger = isEnableSlf4j();


  private static boolean isEnableSlf4j() {
    return Try.getOrDefault(() -> {
      Class.forName("org.slf4j.Logger");
      return true;
    }, false);
  }

  /**
   *
   * private static final org.slf4j.Logger log =
   * org.nkjmlab.sorm4j.internal.util.LoggerFactory.getLogger();
   *
   * @return
   */
  public static Logger getLogger() {
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    String className = stackTrace[1].getClassName();
    return org.slf4j.LoggerFactory.getLogger(className);
  }

  private static final Map<Class<?>, Logger> loggers = new ConcurrentHashMap<>();

  public static Logger getLogger(Class<?> clazz) {
    return loggers.computeIfAbsent(clazz, k -> org.slf4j.LoggerFactory.getLogger(clazz));
  }

  public static void error(Class<?> clazz, String msg, Object... params) {
    error(enableLogger, clazz, msg, params);
  }

  public static void error(boolean enableLogger, Class<?> clazz, String msg, Object... params) {
    if (enableLogger) {
      getLogger(clazz).error(msg, params);
    } else {
      systemErrorPrintln("ERROR", StringUtils.format(msg, params));
    }
  }


  public static void warn(Class<?> clazz, String msg, Object... params) {
    warn(enableLogger, clazz, msg, params);
  }

  public static void warn(boolean enableLogger, Class<?> clazz, String msg, Object... params) {
    if (enableLogger) {
      getLogger(clazz).warn(msg, params);
    } else {
      systemErrorPrintln("WARN", StringUtils.format(msg, params));
    }
  }


  public static void info(Class<?> clazz, String msg, Object... params) {
    info(enableLogger, clazz, msg, params);
  }

  public static void info(boolean enableLogger, Class<?> clazz, String msg, Object... params) {
    if (enableLogger) {
      getLogger(clazz).info(msg, params);
    } else {
      systemOutPrintln("INFO", StringUtils.format(msg, params));
    }
  }

  public static void debug(Class<?> clazz, String msg, Object... params) {
    debug(enableLogger, clazz, msg, params);
  }

  public static void debug(boolean enableLogger, Class<?> clazz, String msg, Object... params) {
    if (enableLogger) {
      getLogger(clazz).debug(msg, params);
    } else {
      systemOutPrintln("DEBUG", StringUtils.format(msg, params));
    }
  }

  public static void trace(Class<?> clazz, String msg, Object... params) {
    trace(enableLogger, clazz, msg, params);
  }

  public static void trace(boolean enableLogger, Class<?> clazz, String msg, Object... params) {
    if (enableLogger) {
      getLogger(clazz).trace(msg, params);
    } else {
      systemOutPrintln("TRACE", StringUtils.format(msg, params));
    }
  }

  private static void systemErrorPrintln(String label, String msg) {
    System.err.println(MethodInvoker.getSummary(5, label) + " " + msg);
  }

  private static void systemOutPrintln(String label, String msg) {
    System.out.println(MethodInvoker.getSummary(5, label) + " " + msg);
  }
}
