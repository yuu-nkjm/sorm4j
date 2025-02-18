package org.nkjmlab.sorm4j.context;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Convert {@link ResultSet} from database to specified objects.
 *
 * @author nkjm
 */
public interface ColumnValueToJavaObjectConverters {

  /**
   * Reads a column from the current row in the provided {@link java.sql.ResultSet} and returns an
   * instance of the specified Java {@link java.lang.Class} containing the values read.
   *
   * <p>This method is mainly used for converting the result of query. i.e. Convert from Sql to Java
   * by the specified class.
   *
   * <p>This method is used while converting {@link java.sql.ResultSet} rows to objects. The class
   * type is the field type in the target bean.
   *
   * @param resultSet {@link java.sql.ResultSet} (positioned in the row to be processed)
   * @param columnIndex column index in the result set (starting with 1)
   * @param columnType
   * @param toType {@link java.lang.Class} of the object to be returned
   * @throws SQLException
   */
  <T> T convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<T> toType);

  /**
   * Returns the given type could be converted to Java object or not.
   *
   * @param objectClass
   * @return
   */
  boolean isSupportedComponentType(Class<?> objectClass);
}
