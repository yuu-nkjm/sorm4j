package org.nkjmlab.sorm4j.internal.context;

import java.util.Map;

import org.nkjmlab.sorm4j.internal.context.impl.ContainerAccessor;
import org.nkjmlab.sorm4j.mapping.annotation.OrmColumnAliasPrefix;

/**
 * A mapper from column to field name.
 *
 * @author nkjm
 */
public interface ColumnToFieldAccessorMapper {

  /**
   * Creates mapping between name and {@link ContainerAccessor}
   *
   * @param objectClass
   * @return Keys in map is written in the canonical case.
   */
  Map<String, ContainerAccessor> createMapping(Class<?> objectClass);

  /**
   * Gets column alias prefix based on {@link OrmColumnAliasPrefix} annotation or the given object
   * class.
   *
   * @param objectClass
   * @return
   */
  String getColumnAliasPrefix(Class<?> objectClass);
}
