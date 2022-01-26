package org.nkjmlab.sorm4j.context;

import java.util.Map;
import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;

/**
 * A mapper from column to field name.
 *
 * @author nkjm
 *
 */
public interface ColumnToFieldAccessorMapper {

  /**
   * Creates mapping between column name and {@link FieldAccessor}
   *
   * @param objectClass
   * @return
   */
  Map<String, FieldAccessor> createMapping(Class<?> objectClass);

  /**
   * Gets column alias prefix based on {@link OrmColumnAliasPrefix} annotation or the given object
   * class.
   *
   * @param objectClass
   * @return
   */
  String getColumnAliasPrefix(Class<?> objectClass);

}
