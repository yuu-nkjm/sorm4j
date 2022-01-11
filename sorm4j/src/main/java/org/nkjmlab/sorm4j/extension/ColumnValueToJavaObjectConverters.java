package org.nkjmlab.sorm4j.extension;

import java.sql.ResultSet;


/**
 * Convert {@link ResultSet} from database to specified objects.
 *
 * @author nkjm
 *
 */
public interface ColumnValueToJavaObjectConverters extends ColumnValueToJavaObjectConverter {

  /**
   * Returns the given type could be converted to Java object or not.
   *
   * @param options
   * @param objectClass
   *
   * @return
   */

  boolean isSupportedType(Class<?> objectClass);

}
