package org.nkjmlab.sorm4j.internal.extension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public final class LoggerFactory {

  private static final Map<String, SormLogger> loggers = new ConcurrentHashMap<>();

  public static SormLogger getLogger() {
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    String className = stackTrace[1].getClassName();
    return loggers.computeIfAbsent(className, k -> Log4jSormLogger.enableLogger
        ? Log4jSormLogger.getLogger()
        : (Slf4jSormLogger.enableLogger ? Slf4jSormLogger.getLogger() : JulSormLogger.getLogger()));
  }

  public static SormLogger getLogger(Class<?> clazz) {
    return loggers.computeIfAbsent(clazz.getName(), k -> Log4jSormLogger.enableLogger
        ? Log4jSormLogger.getLogger()
        : (Slf4jSormLogger.enableLogger ? Slf4jSormLogger.getLogger() : JulSormLogger.getLogger()));
  }



}
