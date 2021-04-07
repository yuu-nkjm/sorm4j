package org.nkjmlab.sorm4j.internal.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public final class LoggerFactory {

  private LoggerFactory() {}

  private static final boolean enableLogger = isEnable();


  private static boolean isEnable() {
    try {
      Class.forName("org.slf4j.Logger");
      return true;
    } catch (ClassNotFoundException e1) {
      return false;
    }
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
    if (enableLogger) {
      getLogger(clazz).error(msg, params);
    } else {
      systemErrorPrintln("ERROR", StringUtils.format(msg, params));
    }
  }


  public static void warn(Class<?> clazz, String msg, Object... params) {
    if (enableLogger) {
      getLogger(clazz).warn(msg, params);
    } else {
      systemErrorPrintln("WARN", StringUtils.format(msg, params));
    }
  }

  public static void info(Class<?> clazz, String msg, Object... params) {
    if (enableLogger) {
      getLogger(clazz).info(msg, params);
    } else {
      systemOutPrintln("INFO", StringUtils.format(msg, params));
    }
  }

  public static void debug(Class<?> clazz, String msg, Object... params) {
    if (enableLogger) {
      getLogger(clazz).debug(msg, params);
    } else {
      systemOutPrintln("DEBUG", StringUtils.format(msg, params));
    }
  }

  public static void trace(Class<?> clazz, String msg, Object... params) {
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


  private static class MethodInvoker {
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
}
