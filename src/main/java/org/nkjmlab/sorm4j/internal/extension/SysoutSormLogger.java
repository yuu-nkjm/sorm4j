package org.nkjmlab.sorm4j.internal.extension;

import org.nkjmlab.sorm4j.internal.util.StringUtils;

public class SysoutSormLogger implements SormLogger {

  private static final String TRACE = "TRACE";
  private static final String DEBUG = "DEBUG";
  private static final String INFO = "INFO";
  private static final String WARN = "WARN";
  private static final String ERROR = "ERROR";

  private static void systemErrorPrintln(String label, String msg, Object... params) {
    System.err.println(MethodInvoker.getSummary(5, label) + " " + StringUtils.format(msg, params));
  }

  private static void systemOutPrintln(String label, String msg, Object... params) {
    System.out.println(MethodInvoker.getSummary(5, label) + " " + StringUtils.format(msg, params));
  }


  @Override
  public void trace(String format, Object... arguments) {
    systemOutPrintln(TRACE, format, arguments);
  }


  @Override
  public void debug(String format, Object... arguments) {
    systemOutPrintln(DEBUG, format, arguments);
  }

  @Override
  public void info(String format, Object... arguments) {
    systemOutPrintln(INFO, format, arguments);

  }

  @Override
  public void warn(String format, Object... arguments) {
    systemErrorPrintln(WARN, format, arguments);
  }


  @Override
  public void error(String format, Object... arguments) {
    systemErrorPrintln(ERROR, format, arguments);
  }

  public static SormLogger getLogger(String className) {
    return new SysoutSormLogger();
  }

}
