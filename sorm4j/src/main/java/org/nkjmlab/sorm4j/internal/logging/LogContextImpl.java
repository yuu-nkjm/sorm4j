package org.nkjmlab.sorm4j.internal.logging;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.nkjmlab.sorm4j.context.logging.LogContext;
import org.nkjmlab.sorm4j.context.logging.SormLogger;
import org.nkjmlab.sorm4j.internal.util.logger.JulSormLogger;
import org.nkjmlab.sorm4j.internal.util.logger.Log4jSormLogger;
import org.nkjmlab.sorm4j.internal.util.logger.Slf4jSormLogger;

public class LogContextImpl implements LogContext {
  private final Set<LogContext.Category> enabledCategories;

  private final Supplier<SormLogger> loggerSupplier;

  private final Map<Class<?>, SormLogger> loggers = new ConcurrentHashMap<>();

  public LogContextImpl(
      Supplier<SormLogger> loggerSupplier, Set<LogContext.Category> enabledCategories) {
    this.loggerSupplier = loggerSupplier != null ? loggerSupplier : getDefaultLoggerSupplier();
    this.enabledCategories =
        enabledCategories.size() == 0 ? Collections.emptySet() : EnumSet.copyOf(enabledCategories);
  }

  public static Supplier<SormLogger> getDefaultLoggerSupplier() {
    return Log4jSormLogger.enableLogger
        ? Log4jSormLogger::getLogger
        : (Slf4jSormLogger.enableLogger ? Slf4jSormLogger::getLogger : JulSormLogger::getLogger);
  }

  @Override
  public Optional<LogPoint> createLogPoint(LogContext.Category category, Class<?> callerClass) {
    return isEnable(category)
        ? Optional.of(new LogPoint(category.name(), getLogger(callerClass)))
        : Optional.empty();
  }

  @Override
  public SormLogger getLogger(Class<?> clazz) {
    return loggers.computeIfAbsent(clazz, k -> loggerSupplier.get());
  }

  @Override
  public boolean isEnable(LogContext.Category category) {
    return enabledCategories.contains(category);
  }

  @Override
  public String toString() {
    return "LoggerContext [enabledCategories="
        + enabledCategories
        + ", logger="
        + getLogger(LogContext.class)
        + "]";
  }
}
