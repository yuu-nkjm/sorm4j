package org.nkjmlab.sorm4j.context;

import java.util.Map;

public interface NameToFieldAccessorMapper {
  /**
   * Creates mapping between name and {@link FieldAccessor}
   *
   * @param objectClass
   * @return Keys in map is written in the canonical case.
   */
  Map<String, FieldAccessor> createMapping(Class<?> objectClass);
}
