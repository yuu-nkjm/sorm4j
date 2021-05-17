package org.nkjmlab.sorm4j.extension;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import org.nkjmlab.sorm4j.internal.util.LogPoint;

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

  private final Map<LoggerConfig.Category, Boolean> modes = new EnumMap<>(LoggerConfig.Category.class);

  public LoggerConfig() {
    on(Category.MAPPING);
  }


  /**
   * Enables logging for the given parameter of {@link Category}.
   *
   * @param categories are in {@link Category}
   */
  public void on(LoggerConfig.Category... categories) {
    Arrays.stream(categories).forEach(name -> modes.put(name, true));
  }

  /**
   * Disables logging for the given categories.
   *
   * @param categories
   */
  public void off(LoggerConfig.Category... categories) {
    Arrays.stream(categories).forEach(name -> modes.put(name, false));
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

  public Optional<LogPoint> createLogPoint(LoggerConfig.Category category) {
    Boolean f = modes.get(category);
    if (f == null || !f) {
      return Optional.empty();
    } else {
      LogPoint dp = new LogPoint(category.name());
      return Optional.of(dp);
    }
  }


}
