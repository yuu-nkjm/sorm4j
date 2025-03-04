package org.nkjmlab.sorm4j.context;

import org.nkjmlab.sorm4j.mapping.annotation.OrmColumnAliasPrefix;

/**
 * A mapper from column to field name.
 *
 * @author nkjm
 */
public interface ColumnToFieldAccessorMapper extends NameToFieldAccessorMapper {

  /**
   * Gets column alias prefix based on {@link OrmColumnAliasPrefix} annotation or the given object
   * class.
   *
   * @param objectClass
   * @return
   */
  String getColumnAliasPrefix(Class<?> objectClass);
}
