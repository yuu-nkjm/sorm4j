package org.nkjmlab.sorm4j.extension;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.extension.LoggerFactory;
import org.nkjmlab.sorm4j.internal.extension.SormLogger;
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

  private final Set<LoggerConfig.Category> onCategories = ConcurrentHashMap.newKeySet();

  @Experimental
  private static List<Category> defaultLoggingCategories =
      new CopyOnWriteArrayList<>(List.of(Category.MAPPING));

  public LoggerConfig() {
    defaultLoggingCategories.forEach(c -> on(c));
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

  @Experimental
  public volatile boolean forceLogging = false;

  @Experimental
  public Optional<LogPoint> createLogPoint(LoggerConfig.Category category, Class<?> clazz) {
    return isLogging(category) ? Optional.of(new LogPoint(category.name(), clazz))
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

    private LogPoint(String name, Class<?> clazz) {
      this.name = name;
      this.logger = LoggerFactory.getLogger(clazz);
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
      String ret = StringUtils.format("[{}] Execute SQL [{}] to [{}]", getTag(),
          psql.getBindedSql(), getDbUrl(connection));
      this.startTime = System.nanoTime();
      return ret;
    }

    public String createBeforeMultiRowMessage(Connection connection, Class<?> clazz, int length,
        String tableName) {
      String ret = StringUtils.format(
          "[{}] Execute multirow insert with [{}] objects of [{}] into [{}] on [{}]", getTag(),
          length, clazz, tableName, getDbUrl(connection));
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

  }

}
