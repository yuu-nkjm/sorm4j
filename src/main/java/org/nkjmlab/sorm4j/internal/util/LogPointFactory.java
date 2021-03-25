package org.nkjmlab.sorm4j.internal.util;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import org.nkjmlab.sorm4j.OrmLogger;

public final class LogPointFactory {

  private LogPointFactory() {}

  public static final Map<OrmLogger.Category, Boolean> modes =
      new EnumMap<>(OrmLogger.Category.class);

  public static Optional<LogPoint> createLogPoint(OrmLogger.Category category) {
    Boolean f = modes.get(category);
    if (f == null || !f) {
      return Optional.empty();
    } else {
      LogPoint dp = new LogPoint(category.name());
      return Optional.of(dp);
    }
  }



}
