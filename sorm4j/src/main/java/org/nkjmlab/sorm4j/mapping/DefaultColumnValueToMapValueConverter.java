package org.nkjmlab.sorm4j.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.Map;

public final class DefaultColumnValueToMapValueConverter implements ColumnValueToMapValueConverter {

  private final Map<Integer, ColumnValueToMapValueConverter> converters;

  public DefaultColumnValueToMapValueConverter(
      Map<Integer, ColumnValueToMapValueConverter> converters) {
    this.converters = Map.copyOf(converters);
  }

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
  @Override
  public Object convertToValue(ResultSet resultSet, int column, int sqlType) throws SQLException {

    final ColumnValueToMapValueConverter converter = converters.get(sqlType);
    if (converter != null) {
      return converter.convertToValue(resultSet, column, sqlType);
    }
    switch (sqlType) {
      case java.sql.Types.ARRAY:
        return resultSet.getArray(column);
      case java.sql.Types.BIGINT: {
        final long ret = resultSet.getLong(column);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case java.sql.Types.BINARY:
      case java.sql.Types.BLOB:
      case java.sql.Types.VARBINARY:
      case java.sql.Types.LONGVARBINARY:
        return resultSet.getBytes(column);
      case java.sql.Types.BIT:
      case java.sql.Types.BOOLEAN: {
        final boolean ret = resultSet.getBoolean(column);
        return (!ret && resultSet.wasNull()) ? null : ret;
      }
      case java.sql.Types.CHAR:
      case java.sql.Types.CLOB:
      case java.sql.Types.LONGVARCHAR:
      case java.sql.Types.VARCHAR:
        return resultSet.getString(column);
      case java.sql.Types.DATALINK:
        return resultSet.getURL(column);
      case java.sql.Types.DATE:
        return resultSet.getDate(column);
      case java.sql.Types.DECIMAL:
      case java.sql.Types.NUMERIC:
        return resultSet.getBigDecimal(column);
      case java.sql.Types.REAL:
      case java.sql.Types.DOUBLE: {
        final double ret = resultSet.getDouble(column);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case java.sql.Types.FLOAT: {
        final float ret = resultSet.getFloat(column);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case java.sql.Types.INTEGER: {
        final int ret = resultSet.getInt(column);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case java.sql.Types.NULL:
        return null;
      case java.sql.Types.REF:
        return resultSet.getRef(column);
      case java.sql.Types.ROWID:
        return resultSet.getRowId(column);
      case java.sql.Types.SMALLINT: {
        final short ret = (short) resultSet.getInt(column);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case java.sql.Types.TIME:
        return resultSet.getTime(column);
      case java.sql.Types.TIMESTAMP:
        return resultSet.getTimestamp(column);
      case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
        return resultSet.getObject(column);
      case java.sql.Types.TINYINT: {
        final byte ret = resultSet.getByte(column);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case java.sql.Types.JAVA_OBJECT:
      case java.sql.Types.OTHER:
        return resultSet.getObject(column);
      default:
        // Could not find corresponding converter. ResultSet#getObject method will be used.
        return resultSet.getObject(column);
    }
  }
}
