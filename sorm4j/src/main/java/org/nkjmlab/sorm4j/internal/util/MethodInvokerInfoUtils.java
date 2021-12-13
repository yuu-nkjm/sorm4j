package org.nkjmlab.sorm4j.internal.util;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class MethodInvokerInfoUtils {


  public static String getInvokerInfo(int depth, StackTraceElement[] stackTraceElements) {
    StackTraceElement e = getStackTraceElement(depth, stackTraceElements);
    return e.getClassName() + " ("
        + (e.getFileName() != null ? new File(e.getFileName()).getName() : "") + ":"
        + e.getLineNumber() + ") ";
  }

  private static StackTraceElement getStackTraceElement(int index,
      StackTraceElement[] stackTraceElements) {
    if (index < 0) {
      return stackTraceElements[0];
    } else if (index >= stackTraceElements.length) {
      return stackTraceElements[stackTraceElements.length - 1];
    } else {
      return stackTraceElements[index];
    }
  }

  public static String getOutsideInvoker(String libPrefix) {
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();

    {
      Optional<StackTraceElement> c = Arrays.stream(stackTrace)
          .filter(s -> !s.getClassName().startsWith(libPrefix)
              && !s.getClassName().startsWith("java.") && !s.getClassName().startsWith("jdk."))
          .findFirst();

      if (c.isPresent()) {
        return c.map(se -> getInvoker(se)).get();
      }
    }

    {
      Optional<StackTraceElement> c = Arrays.stream(stackTrace)
          .filter(s -> !s.getClassName().startsWith(libPrefix)).findFirst();

      if (c.isPresent()) {
        return c.map(se -> getInvoker(se)).get();
      }
    }
    return stackTrace.length == 0 ? "" : getInvoker(stackTrace[stackTrace.length - 1]);
  }

  public static String getInvoker(StackTraceElement se) {
    return se.getClassName() + "." + se.getMethodName() + "(" + se.getFileName() + ":"
        + se.getLineNumber() + ")";
  }
}
