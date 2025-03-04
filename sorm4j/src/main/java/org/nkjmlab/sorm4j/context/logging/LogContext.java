package org.nkjmlab.sorm4j.context.logging;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.nkjmlab.sorm4j.internal.logging.LogContextImpl;
import org.nkjmlab.sorm4j.internal.logging.LogPoint;

/**
 * Logger for Sorm4j.
 *
 * @author nkjm
 */
public interface LogContext {

  /** Logging Categories */
  public enum Category {
    MAPPING,
    EXECUTE_QUERY,
    MULTI_ROW,
    EXECUTE_UPDATE;
  }

  public static LogContext.Builder builder() {
    return new LogContext.Builder();
  }

  Optional<LogPoint> createLogPoint(Category category, Class<?> callerClass);

  SormLogger getLogger(Class<?> clazz);

  boolean isEnable(Category category);

  public static class Builder {

    private Supplier<SormLogger> loggerSupplier;

    private final Set<LogContext.Category> onCategories = new HashSet<>();

    Builder() {}

    public LogContext build() {
      return new LogContextImpl(loggerSupplier, onCategories);
    }

    /**
     * Disables logging for the given categories.
     *
     * @param categories
     */
    public Builder disable(LogContext.Category... categories) {
      Arrays.stream(categories).forEach(name -> onCategories.remove(name));
      return this;
    }

    /** Disables logging all categories. See {@link LogContext.Category}. */
    public Builder disableAll() {
      disable(LogContext.Category.values());
      return this;
    }

    /**
     * Enables logging for the given parameter of {@link LogContext.Category}.
     *
     * @param categories are in {@link LogContext.Category}
     */
    public Builder enable(LogContext.Category... categories) {
      Arrays.stream(categories).forEach(name -> onCategories.add(name));
      return this;
    }

    /** Enables logging all categories. See {@link LogContext.Category}. */
    public Builder enableAll() {
      enable(LogContext.Category.values());
      return this;
    }

    public Builder setLoggerSupplier(Supplier<SormLogger> loggerSupplier) {
      this.loggerSupplier = loggerSupplier;
      return this;
    }
  }
}
