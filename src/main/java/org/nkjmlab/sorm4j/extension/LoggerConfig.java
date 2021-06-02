package org.nkjmlab.sorm4j.extension;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.extension.logger.JulSormLogger;
import org.nkjmlab.sorm4j.extension.logger.Log4jSormLogger;
import org.nkjmlab.sorm4j.extension.logger.Slf4jSormLogger;
import org.nkjmlab.sorm4j.extension.logger.SormLogger;
import org.nkjmlab.sorm4j.internal.util.StringUtils;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

/**
 * Logger for Sorm4j.
 *
 * @author nkjm
 *
 */
public final class LoggerConfig {

  /**
   * Categories of {@link LoggerConfig}.
   *
   */
  public enum Category {
    MAPPING, EXECUTE_QUERY, MULTI_ROW, EXECUTE_UPDATE, HANDLE_PREPAREDSTATEMENT;
  }


  private final Set<LoggerConfig.Category> onCategories;

  private final Supplier<SormLogger> loggerSupplier;
  @Experimental
  public volatile boolean forceLogging = false;

  private final Map<String, SormLogger> loggers = new ConcurrentHashMap<>();

  public SormLogger getLogger() {
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    String className = stackTrace[1].getClassName();
    return loggers.computeIfAbsent(className, k -> loggerSupplier.get());
  }

  public LoggerConfig(Supplier<SormLogger> loggerSupplier,
      Set<LoggerConfig.Category> onCategories) {
    this.loggerSupplier = loggerSupplier;
    this.onCategories = onCategories;
  }

  @Experimental
  public Optional<LogPoint> createLogPoint(LoggerConfig.Category category) {
    return isLogging(category) ? Optional.of(new LogPoint(category.name(), getLogger()))
        : Optional.empty();
  }

  private boolean isLogging(Category category) {
    return forceLogging || onCategories.contains(category);
  }

  @Experimental
  public static final class LogPoint {

    private final String name;
    private long startTime;
    public final SormLogger logger;

    private LogPoint(String name, SormLogger logger) {
      this.name = name;
      this.logger = logger;
    }

    public String getTagAndElapsedTime() {
      return "[" + getTag() + "]" + " ["
          + String.format("%.3f", (double) (System.nanoTime() - startTime) / 1000 / 1000)
          + " msec] :";
    }

    public String getTag() {
      return name + ":" + (hashCode() / 10000);
    }

    private String getDbUrl(Connection connection) {
      return Try.getOrDefault(() -> connection.getMetaData().getURL(), "");
    }

    public String createBeforeSqlMessage(Connection connection, String sql, Object[] parameters) {
      String ret = createBeforeSqlMessage(connection, ParameterizedSql.parse(sql, parameters));
      return ret;
    }

    public String createBeforeSqlMessage(Connection connection, ParameterizedSql psql) {
      String ret = StringUtils.format("[{}] At {}, Execute SQL [{}] to [{}]", getTag(), getCaller(),
          psql.getBindedSql(), getDbUrl(connection));
      this.startTime = System.nanoTime();
      return ret;
    }

    public String createBeforeMultiRowMessage(Connection connection, Class<?> clazz, int length,
        String tableName) {
      String ret = StringUtils.format(
          "[{}] At {}, Execute multirow insert with [{}] objects of [{}] into [{}] on [{}]",
          getTag(), getCaller(), length, clazz, tableName, getDbUrl(connection));
      this.startTime = System.nanoTime();
      return ret;
    }


    public String createAfterUpdateMessage(int ret) {
      return StringUtils.format("{} Affect [{}] rows", getTagAndElapsedTime(), ret);
    }

    public String createAfterQueryMessage(Object ret) {
      return StringUtils.format("{} Read [{}] objects", getTagAndElapsedTime(),
          ret instanceof Collection ? ((Collection<?>) ret).size() : 1);
    }

    public String createAfterMultiRowMessage(int[] result) {
      return StringUtils.format("{} Affect [{}] objects", getTagAndElapsedTime(),
          IntStream.of(result).sum());
    }

    private String getCaller() {
      StackTraceElement[] stackTrace = new Throwable().getStackTrace();

      String caller = Arrays.stream(stackTrace)
          .filter(s -> !s.getClassName().startsWith("org.nkjmlab.sorm4j")
              && !s.getClassName().startsWith("java."))
          .findFirst().map(se -> se.getClassName() + "." + se.getMethodName() + "("
              + se.getFileName() + ":" + se.getLineNumber() + ")")
          .orElseGet(() -> "");
      return caller;
    }

  }

  public static class Builder {
    private static Set<Category> defaultLoggingCategories = Set.of(Category.MAPPING);

    private Supplier<SormLogger> loggerSupplier;
    private final Set<LoggerConfig.Category> onCategories;

    public Builder() {
      this.onCategories = new HashSet<>(defaultLoggingCategories);
    }

    public void setLoggerSupplier(Supplier<SormLogger> loggerSupplier) {
      this.loggerSupplier = loggerSupplier;
    }

    private static Supplier<SormLogger> getDefaultLoggerSupplier() {
      return Log4jSormLogger.enableLogger ? () -> Log4jSormLogger.getLogger()
          : (Slf4jSormLogger.enableLogger ? () -> Slf4jSormLogger.getLogger()
              : () -> JulSormLogger.getLogger());
    }

    public LoggerConfig build() {
      this.loggerSupplier = loggerSupplier != null ? loggerSupplier : getDefaultLoggerSupplier();
      return new LoggerConfig(loggerSupplier, onCategories);
    }

    /**
     * Enables logging for the given parameter of {@link Category}.
     *
     * @param categories are in {@link Category}
     */
    public void on(LoggerConfig.Category... categories) {
      Arrays.stream(categories).forEach(name -> onCategories.add(name));
    }

    /**
     * Disables logging for the given categories.
     *
     * @param categories
     */
    public void off(LoggerConfig.Category... categories) {
      Arrays.stream(categories).forEach(name -> onCategories.remove(name));
    }

    /**
     * Enables logging all categories. See {@link Category}.
     */
    public void onAll() {
      on(LoggerConfig.Category.values());
    }

    /**
     * Disables logging all categories. See {@link Category}.
     */
    public void offAll() {
      off(LoggerConfig.Category.values());
    }


  }

}
