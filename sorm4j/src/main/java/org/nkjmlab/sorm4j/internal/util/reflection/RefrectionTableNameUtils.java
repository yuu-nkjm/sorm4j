package org.nkjmlab.sorm4j.internal.util.reflection;

import java.util.Optional;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.mapping.annotation.OrmTableName;

public class RefrectionTableNameUtils {
  public static Optional<String> getAnotatedTableName(Class<?> valueType) {
    return Optional.ofNullable(valueType.getAnnotation(OrmTableName.class)).map(a -> a.value());
  }

  public static String toNaiveTableName(Class<?> valueType) {
    return getAnotatedTableName(valueType)
        .orElseGet(
            () ->
                SormContext.getDefaultCanonicalStringCache()
                    .toCanonicalName(valueType.getSimpleName() + "s"));
  }

  private RefrectionTableNameUtils() {}
}
