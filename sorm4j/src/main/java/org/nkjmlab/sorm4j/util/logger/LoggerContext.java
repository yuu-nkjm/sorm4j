package org.nkjmlab.sorm4j.util.logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.nkjmlab.sorm4j.internal.util.logger.JulSormLogger;
import org.nkjmlab.sorm4j.internal.util.logger.Log4jSormLogger;
import org.nkjmlab.sorm4j.internal.util.logger.Slf4jSormLogger;

/**
 * Logger for Sorm4j.
 *
 * @author nkjm
 */
public final class LoggerContext {

  /** Logging Categories */
  public enum Category {
    MAPPING,
    EXECUTE_QUERY,
    MULTI_ROW,
    EXECUTE_UPDATE;
  }

  public static Builder builder() {
    return new Builder();
  }

  private final Set<LoggerContext.Category> enabledCategories;

  private final Supplier<SormLogger> loggerSupplier;

  private final Map<Class<?>, SormLogger> loggers = new ConcurrentHashMap<>();

  private LoggerContext(
      Supplier<SormLogger> loggerSupplier, Set<LoggerContext.Category> enabledCategories) {
    this.loggerSupplier = loggerSupplier;
    this.enabledCategories =
        enabledCategories.size() == 0 ? Collections.emptySet() : EnumSet.copyOf(enabledCategories);
  }

  public Optional<LogPoint> createLogPoint(LoggerContext.Category category, Class<?> callerClass) {
    return isEnable(category)
        ? Optional.of(new LogPoint(category.name(), getLogger(callerClass)))
        : Optional.empty();
  }

  public SormLogger getLogger(Class<?> clazz) {
    return loggers.computeIfAbsent(clazz, k -> loggerSupplier.get());
  }

  public boolean isEnable(LoggerContext.Category category) {
    return enabledCategories.contains(category);
  }

  @Override
  public String toString() {
    return "LoggerContext [enabledCategories="
        + enabledCategories
        + ", logger="
        + getLogger(LoggerContext.class)
        + "]";
  }

  public static Supplier<SormLogger> getDefaultLoggerSupplier() {
    return Log4jSormLogger.enableLogger
        ? Log4jSormLogger::getLogger
        : (Slf4jSormLogger.enableLogger ? Slf4jSormLogger::getLogger : JulSormLogger::getLogger);
  }

  public static class Builder {

    private Supplier<SormLogger> loggerSupplier;

    private final Set<LoggerContext.Category> onCategories = new HashSet<>();

    private Builder() {}

    public LoggerContext build() {
      this.loggerSupplier = loggerSupplier != null ? loggerSupplier : getDefaultLoggerSupplier();
      return new LoggerContext(loggerSupplier, onCategories);
    }

    /**
     * Disables logging for the given categories.
     *
     * @param categories
     */
    public Builder disable(LoggerContext.Category... categories) {
      Arrays.stream(categories).forEach(name -> onCategories.remove(name));
      return this;
    }

    /** Disables logging all categories. See {@link LoggerContext.Category}. */
    public Builder disableAll() {
      disable(LoggerContext.Category.values());
      return this;
    }

    /**
     * Enables logging for the given parameter of {@link LoggerContext.Category}.
     *
     * @param categories are in {@link LoggerContext.Category}
     */
    public Builder enable(LoggerContext.Category... categories) {
      Arrays.stream(categories).forEach(name -> onCategories.add(name));
      return this;
    }

    /** Enables logging all categories. See {@link LoggerContext.Category}. */
    public Builder enableAll() {
      enable(LoggerContext.Category.values());
      return this;
    }

    public Builder setLoggerSupplier(Supplier<SormLogger> loggerSupplier) {
      this.loggerSupplier = loggerSupplier;
      return this;
    }
  }
}
