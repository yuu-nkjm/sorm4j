package org.nkjmlab.sorm4j.extension.logger;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

/**
 * Logger for Sorm4j.
 *
 * @author nkjm
 *
 */
public final class LoggerContext {

  /**
   * Categories of {@link LoggerContext}.
   *
   */
  public enum Category {
    MAPPING, EXECUTE_QUERY, MULTI_ROW, EXECUTE_UPDATE, HANDLE_PREPAREDSTATEMENT;
  }


  private final Set<LoggerContext.Category> onCategories;

  private final Supplier<SormLogger> loggerSupplier;
  @Experimental
  public volatile boolean forceLogging = false;

  private final Map<String, SormLogger> loggers = new ConcurrentHashMap<>();

  public SormLogger getLogger() {
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    String className = stackTrace[1].getClassName();
    return loggers.computeIfAbsent(className, k -> loggerSupplier.get());
  }

  public LoggerContext(Supplier<SormLogger> loggerSupplier,
      Set<LoggerContext.Category> onCategories) {
    this.loggerSupplier = loggerSupplier;
    this.onCategories = onCategories;
  }

  @Experimental
  public Optional<LogPoint> createLogPoint(LoggerContext.Category category) {
    return isLogging(category) ? Optional.of(new LogPoint(category.name(), getLogger()))
        : Optional.empty();
  }

  private boolean isLogging(Category category) {
    return forceLogging || onCategories.contains(category);
  }

  @Experimental
  public static final class LogPoint {

    private final String name;
    private final SormLogger logger;
    private long startTime;

    private LogPoint(String name, SormLogger logger) {
      this.name = name;
      this.logger = logger;
    }


    public String getTag() {
      return name + ":" + (hashCode() / 10000);
    }

    public void logBeforeSql(Connection connection, String sql, Object... parameters) {
      logger.logBeforeSql(getTag(), connection, sql, parameters);
      this.startTime = System.nanoTime();
    }

    public void logBeforeSql(Connection connection, ParameterizedSql sql) {
      logger.logBeforeSql(getTag(), connection, sql);
      this.startTime = System.nanoTime();
    }

    public void logBeforeMultiRow(Connection con, Class<?> objectClass, int length,
        String tableName) {
      logger.logBeforeMultiRow(getTag(), con, objectClass, length, tableName);
      this.startTime = System.nanoTime();
    }


    public void logAfterQuery(Object ret) {
      logger.logAfterQuery(getTag(), getElapsedTime(), ret);
    }


    public void logAfterMultiRow(int[] result) {
      logger.logAfterMultiRow(name, getElapsedTime(), result);
    }

    private long getElapsedTime() {
      return System.nanoTime() - startTime;
    }


    public void logAfterUpdate(int ret) {
      logger.logAfterUpdate(name, getElapsedTime(), ret);
    }


    public SormLogger getLogger() {
      return logger;
    }


    public void logMapping(String mappingInfo) {
      logger.logMapping(getTag(), mappingInfo);
    }


  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private Supplier<SormLogger> loggerSupplier;
    private final Set<LoggerContext.Category> onCategories = new HashSet<>();

    public Builder() {}

    public void setLoggerSupplier(Supplier<SormLogger> loggerSupplier) {
      this.loggerSupplier = loggerSupplier;
    }

    private static Supplier<SormLogger> getDefaultLoggerSupplier() {
      return Log4jSormLogger.enableLogger ? Log4jSormLogger::getLogger
          : (Slf4jSormLogger.enableLogger ? Slf4jSormLogger::getLogger : JulSormLogger::getLogger);
    }

    public LoggerContext build() {
      this.loggerSupplier = loggerSupplier != null ? loggerSupplier : getDefaultLoggerSupplier();
      return new LoggerContext(loggerSupplier, onCategories);
    }

    /**
     * Enables logging for the given parameter of {@link Category}.
     *
     * @param categories are in {@link Category}
     */
    public void on(LoggerContext.Category... categories) {
      Arrays.stream(categories).forEach(name -> onCategories.add(name));
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
     * Enables logging all categories. See {@link Category}.
     */
    public void onAll() {
      on(LoggerContext.Category.values());
    }

    /**
     * Disables logging all categories. See {@link Category}.
     */
    public void offAll() {
      off(LoggerContext.Category.values());
    }
  }

  @Override
  public String toString() {
    return "LoggerContext [onCategories=" + onCategories + ", logger=" + getLogger() + "]";
  }

}
