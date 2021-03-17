package org.nkjmlab.sorm4j;

import java.util.Arrays;
import org.nkjmlab.sorm4j.core.util.LogPointFactory;

public final class OrmLogger {

  private OrmLogger() {}

  static {
    on(Category.MAPPING);
  }

  /**
   * Categories of {@link OrmLogger}.
   *
   */
  public enum Category {
    MAPPING, EXECUTE_QUERY, MULTI_ROW, EXECUTE_UPDATE;
  }

  /**
   * Enables logging for the given categories.
   *
   * @param categories
   */
  public static void on(OrmLogger.Category... categories) {
    Arrays.stream(categories).forEach(name -> LogPointFactory.modes.put(name, true));
  }

  /**
   * Disables logging for the given categories.
   *
   * @param categories
   */
  public static void off(OrmLogger.Category... categories) {
    Arrays.stream(categories).forEach(name -> LogPointFactory.modes.put(name, false));
  }

  /**
   * Enables logging all.
   */
  public static void on() {
    on(OrmLogger.Category.values());
  }

  /**
   * Disables logging all.
   */
  public static void off() {
    off(OrmLogger.Category.values());
  }

}
