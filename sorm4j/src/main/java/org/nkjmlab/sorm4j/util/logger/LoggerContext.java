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
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.util.logger.JulSormLogger;
import org.nkjmlab.sorm4j.internal.util.logger.Log4jSormLogger;
import org.nkjmlab.sorm4j.internal.util.logger.Slf4jSormLogger;

/**
 * Logger for Sorm4j.
 *
 * @author nkjm
 *
 */
public final class LoggerContext {

  /**
   * Logging Categories
   *
   */
  public enum Category {
    MAPPING, EXECUTE_QUERY, MULTI_ROW, EXECUTE_UPDATE;
  }

  public static Builder builder() {
    return new Builder();
  }


  private final Set<LoggerContext.Category> onCategories;

  private final Supplier<SormLogger> loggerSupplier;

  private volatile boolean forceLogging = false;

  private final Map<Class<?>, SormLogger> loggers = new ConcurrentHashMap<>();

  private LoggerContext(Supplier<SormLogger> loggerSupplier,
      Set<LoggerContext.Category> onCategories) {
    this.loggerSupplier = loggerSupplier;
    this.onCategories =
        onCategories.size() == 0 ? Collections.emptySet() : EnumSet.copyOf(onCategories);
  }

  public Optional<LogPoint> createLogPoint(LoggerContext.Category category, Class<?> callerClass) {
    return isEnable(category) ? Optional.of(new LogPoint(category.name(), getLogger(callerClass)))
        : Optional.empty();
  }

  @Experimental
  public void disableForceLogging() {
    this.forceLogging = false;
  }

  @Experimental
  public void enableForceLogging() {
    this.forceLogging = true;
  }

  public SormLogger getLogger(Class<?> clazz) {
    return loggers.computeIfAbsent(clazz, k -> loggerSupplier.get());
  }

  public boolean isEnable(LoggerContext.Category category) {
    return forceLogging || onCategories.contains(category);
  }

  @Override
  public String toString() {
    return "LoggerContextImpl [onCategories=" + onCategories + ", logger="
        + getLogger(LoggerContext.class) + ", forceLogging=" + forceLogging + "]";
  }

  public static class Builder {

    private static Supplier<SormLogger> getDefaultLoggerSupplier() {
      return Log4jSormLogger.enableLogger ? Log4jSormLogger::getLogger
          : (Slf4jSormLogger.enableLogger ? Slf4jSormLogger::getLogger : JulSormLogger::getLogger);
    }

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
    public void off(LoggerContext.Category... categories) {
      Arrays.stream(categories).forEach(name -> onCategories.remove(name));
    }

    /**
     * Disables logging all categories. See {@link LoggerContext.Category}.
     */
    public void offAll() {
      off(LoggerContext.Category.values());
    }

    /**
     * Enables logging for the given parameter of {@link LoggerContext.Category}.
     *
     * @param categories are in {@link LoggerContext.Category}
     */
    public void on(LoggerContext.Category... categories) {
      Arrays.stream(categories).forEach(name -> onCategories.add(name));
    }

    /**
     * Enables logging all categories. See {@link LoggerContext.Category}.
     */
    public void onAll() {
      on(LoggerContext.Category.values());
    }

    public void setLoggerSupplier(Supplier<SormLogger> loggerSupplier) {
      this.loggerSupplier = loggerSupplier;
    }
  }


}
