package org.nkjmlab.sorm4j.context;

import java.util.Map;

/**
 * A mapper from column to field name.
 *
 * @author nkjm
 *
 */
public interface ColumnToFieldAccessorMapper {

  Map<String, FieldAccessor> createMapping(Class<?> objectClass);

  String getColumnAliasPrefix(Class<?> objectClass);

}
