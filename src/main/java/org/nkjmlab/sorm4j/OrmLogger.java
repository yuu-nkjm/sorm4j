package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.OrmLogger.Category.*;
import java.util.Arrays;
import java.util.Map;
import org.nkjmlab.sorm4j.core.util.LogPointFactory;

public final class OrmLogger {

  private OrmLogger() {}

  static {
    on(MAPPING);
  }

  /**
   * Categories of {@link OrmLogger}.
   *
   */
  public enum Category {
    MAPPING, READ, EXECUTE_BATCH, EXECUTE_UPDATE;
  }

  /**
   * Sets log mode.
   *
   * @param mode
   */
  public static void setLogMode(Map<OrmLogger.Category, Boolean> mode) {
    LogPointFactory.modes.putAll(mode);
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
  public static void onAll() {
    on(OrmLogger.Category.values());
  }

  /**
   * Disables logging all.
   */
  public static void offAll() {
    off(OrmLogger.Category.values());
  }

}
