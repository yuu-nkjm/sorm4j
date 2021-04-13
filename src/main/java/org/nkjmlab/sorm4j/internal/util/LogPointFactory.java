package org.nkjmlab.sorm4j.internal.util;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import org.nkjmlab.sorm4j.extension.SormLogger;

public final class LogPointFactory {

  private LogPointFactory() {}

  public static final Map<SormLogger.Category, Boolean> modes =
      new EnumMap<>(SormLogger.Category.class);

  public static Optional<LogPoint> createLogPoint(SormLogger.Category category) {
    Boolean f = modes.get(category);
    if (f == null || !f) {
      return Optional.empty();
    } else {
      LogPoint dp = new LogPoint(category.name());
      return Optional.of(dp);
    }
  }



}
