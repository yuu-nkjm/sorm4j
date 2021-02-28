package org.nkjmlab.sorm4j.config;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetValueGetter extends OrmConfig {

  /**
   * Reads a column from the current row in the provided {@link java.sql.ResultSet} and returns an
   * instance of the specified Java {@link java.lang.Class} containing the values read.
   *
   * This method is mainly used for "SEARCH SQL AND READ TO POJO". i.e. Convert from Sql to Java by
   * the specified class.
   *
   * This method is used while converting {@link java.sql.ResultSet} rows to objects. The class type
   * is the field type in the target bean.
   *
   * null's will be respected for any non-native types. This means that if a field is of type
   * Integer it will be able to receive a null value from the ResultSet; on the other hand, if a
   * field is of type int it will receive 0 for a null value from the {@link java.sql.ResultSet}.
   *
   * @param resultSet {@link java.sql.ResultSet} (positioned in the row to be processed)
   * @param column column index in the result set (starting with 1)
   * @param type {@link java.lang.Class} of the object to be returned
   * @throws SQLException
   * @since 1.0
   */
  Object getValueByClass(ResultSet resultSet, int column, Class<?> type) throws SQLException;

  /**
   * Reads a column from the current row in the provided {@link java.sql.ResultSet} and return a
   * value correspondent to the SQL type provided (as defined in {@link java.sql.Types
   * java.sql.Types}). null's are respected for all types. This means that if a column is of type
   * LONG and its value comes from the database as null, this method will return null for it.
   *
   * This method is used for "SEARCH AND READ TO MAP". i.e. Convert from Sql to Java by the
   * specified Sql.Types.
   *
   * @param resultSet {@link java.sql.ResultSet} (positioned in the row to be processed)
   * @param column Column index in the result set (starting with 1)
   * @param type type of the column (as defined in {@link java.sql.Types java.sql.Types})
   * @throws SQLException
   * @since 1.0
   */
  Object getValueBySqlType(ResultSet resultSet, int column, int type) throws SQLException;

}
