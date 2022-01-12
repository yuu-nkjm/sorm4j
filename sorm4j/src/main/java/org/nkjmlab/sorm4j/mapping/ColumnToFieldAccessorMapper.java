package org.nkjmlab.sorm4j.mapping;

/**
 * A mapper from column to field name.
 *
 * @author nkjm
 *
 */
public interface ColumnToFieldAccessorMapper {

  ColumnToAccessorMapping createMapping(Class<?> objectClass, String columnAliasPrefix);


}
