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
    return loggers.computeIfAbsent(className,
        k -> Slf4jSormLogger.enableLogger ? Slf4jSormLogger.getLogger(k)
            : SysoutSormLogger.getLogger(k));
  }

  public static SormLogger getLogger(Class<?> clazz) {
    return loggers.computeIfAbsent(clazz.getName(),
        k -> Slf4jSormLogger.enableLogger ? Slf4jSormLogger.getLogger(k)
            : SysoutSormLogger.getLogger(k));
  }



}
