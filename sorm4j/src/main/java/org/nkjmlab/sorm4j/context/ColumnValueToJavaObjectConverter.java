package org.nkjmlab.sorm4j.context;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ColumnValueToJavaObjectConverter {

  boolean test(Class<?> toType) throws SQLException;

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
   * <p>null's will be respected for any non-native types. This means that if a field is of type
   * Integer it will be able to receive a null value from the ResultSet; on the other hand, if a
   * field is of type int it will receive 0 for a null value from the {@link java.sql.ResultSet}.
   *
   * @param resultSet {@link java.sql.ResultSet} (positioned in the row to be processed)
   * @param columnIndex column index in the result set (starting with 1)
   * @param columnType
   * @param toType {@link java.lang.Class} of the object to be returned
   * @throws SQLException
   */
  Object convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<?> toType)
      throws SQLException;
}
