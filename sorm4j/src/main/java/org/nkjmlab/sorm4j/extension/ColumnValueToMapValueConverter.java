package org.nkjmlab.sorm4j.extension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;

public interface ColumnValueToMapValueConverter {

  /**
   * Reads a column from the current row in the provided {@link java.sql.ResultSet} and returns an
   * instance of the specified Java {@link SQLType} containing the values read.
   *
   * This method is mainly used for "SEARCH SQL AND READ TO MAP". i.e. Convert from SQL to Java by
   * the SQL type.
   *
   * This method is used while converting {@link java.sql.ResultSet} rows to Map.
   *
   * @param resultSet
   * @param column
   * @param sqlType
   * @return
   * @throws SQLException
   */
  Object convertToValue(ResultSet resultSet, int column, int sqlType) throws SQLException;

}
