package org.nkjmlab.sorm4j.extension;

import org.nkjmlab.sorm4j.internal.mapping.ColumnToAccessorMapping;


/**
 * A mapper from column to field name.
 *
 * @author nkjm
 *
 */
public interface ColumnToFieldAccessorMapper {

  ColumnToAccessorMapping createMapping(Class<?> objectClass);


}
