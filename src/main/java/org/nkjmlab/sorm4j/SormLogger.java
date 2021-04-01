package org.nkjmlab.sorm4j;

import java.util.Arrays;
import org.nkjmlab.sorm4j.internal.util.LogPointFactory;

/**
 * Logger for Sorm4j.
 *
 * @author nkjm
 *
 */
public final class SormLogger {

  private SormLogger() {}

  static {
    on(Category.MAPPING);
  }

  /**
   * Categories of {@link SormLogger}.
   *
   */
  public enum Category {
    MAPPING, EXECUTE_QUERY, MULTI_ROW, EXECUTE_UPDATE;
  }

  /**
   * Enables logging for the given parameter of {@link Category}.
   *
   * @param categories are in {@link Category}
   */
  public static void on(SormLogger.Category... categories) {
    Arrays.stream(categories).forEach(name -> LogPointFactory.modes.put(name, true));
  }

  /**
   * Disables logging for the given categories.
   *
   * @param categories
   */
  public static void off(SormLogger.Category... categories) {
    Arrays.stream(categories).forEach(name -> LogPointFactory.modes.put(name, false));
  }

  /**
   * Enables logging all categories. See {@link Category}.
   */
  public static void on() {
    on(SormLogger.Category.values());
  }

  /**
   * Disables logging all categories. See {@link Category}.
   */
  public static void off() {
    off(SormLogger.Category.values());
  }

}
