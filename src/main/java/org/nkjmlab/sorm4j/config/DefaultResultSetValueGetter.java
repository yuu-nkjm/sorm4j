package org.nkjmlab.sorm4j.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class DefaultResultSetValueGetter implements ResultSetValueGetter {

  private static org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  @Override
  public Object getValueByClass(final ResultSet resultSet, final int column, final Class<?> type)
      throws SQLException {
    if (type.isEnum()) {
      final String v = resultSet.getString(column);
      return Arrays.stream(type.getEnumConstants()).filter(o -> o.toString().equals(v)).findAny()
          .orElse(null);
    } else if (type.isArray()) {
      final String name = type.getComponentType().getName();
      switch (name) {
        case "byte":
        case "java.lang.Byte":
          return resultSet.getBytes(column);
        case "char":
        case "java.lang.Character":
          final String str = resultSet.getString(column);
          return (str == null) ? null : str.toCharArray();
      }
    }

    final String name = type.getName();
    switch (name) {
      case "boolean":
        return resultSet.getBoolean(column);
      case "java.lang.Boolean": {
        final boolean ret = resultSet.getBoolean(column);
        return (!ret && resultSet.wasNull()) ? null : ret;
      }
      case "byte":
        return resultSet.getByte(column);
      case "java.lang.Byte": {
        final byte ret = resultSet.getByte(column);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case "short":
        return resultSet.getShort(column);
      case "java.lang.Short": {
        final short ret = resultSet.getShort(column);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case "int":
        return resultSet.getInt(column);
      case "java.lang.Integer": {
        final int ret = resultSet.getInt(column);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case "long":
        return resultSet.getLong(column);
      case "java.lang.Long": {
        final long ret = resultSet.getLong(column);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case "float":
        return resultSet.getFloat(column);
      case "java.lang.Float": {
        final float ret = resultSet.getFloat(column);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case "double":
        return resultSet.getDouble(column);
      case "java.lang.Double": {
        final double ret = resultSet.getDouble(column);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case "java.math.BigDecimal":
        return resultSet.getBigDecimal(column);
      case "java.lang.String":
        return resultSet.getString(column);
      case "java.lang.Character":
      case "char": {
        final String str = resultSet.getString(column);
        return (str == null || str.length() == 0) ? null : str.charAt(0);
      }
      case "java.sql.Date":
        return resultSet.getDate(column);
      case "java.sql.Time":
        return resultSet.getTime(column);
      case "java.sql.Timestamp":
        return resultSet.getTimestamp(column);
      case "java.io.InputStream":
        return resultSet.getBinaryStream(column);
      case "java.io.Reader":
        return resultSet.getCharacterStream(column);
      case "java.sql.Clob":
        return resultSet.getClob(column);
      case "java.sql.Blob":
        return resultSet.getBlob(column);
      case "java.time.LocalTime":
        return resultSet.getTime(column).toLocalTime();
      case "java.time.LocalDate":
        return resultSet.getDate(column).toLocalDate();
      case "java.time.LocalDateTime":
        return resultSet.getTimestamp(column).toLocalDateTime();
      case "java.lang.Object":
        return resultSet.getObject(column);
      default:
        log.warn(
            "Could not find coresponding converter for type [{}] on column [{}]. ResultSet.getObject method will be used.",
            name, column);
        return resultSet.getObject(column);
    }

  }

  @Override
  public Object getValueBySqlType(final ResultSet resultSet, final int column, final int type)
      throws SQLException {

    switch (type) {
      case java.sql.Types.ARRAY:
        return resultSet.getArray(column);
      case java.sql.Types.BIGINT: {
        final long ret = resultSet.getLong(column);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case java.sql.Types.BINARY:
        return resultSet.getBytes(column);
      case java.sql.Types.BIT: {
        final boolean ret = resultSet.getBoolean(column);
        return (!ret && resultSet.wasNull()) ? null : ret;
      }
      case java.sql.Types.BLOB:
        return resultSet.getBytes(column);
      case java.sql.Types.BOOLEAN: {
        final boolean ret = resultSet.getBoolean(column);
        return (!ret && resultSet.wasNull()) ? null : ret;
      }
      case java.sql.Types.CHAR:
        return resultSet.getString(column);
      case java.sql.Types.CLOB:
        return resultSet.getString(column);
      case java.sql.Types.DATALINK:
        return resultSet.getBinaryStream(column);
      case java.sql.Types.DATE:
        return resultSet.getDate(column);
      case java.sql.Types.DECIMAL:
        return resultSet.getBigDecimal(column);
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
      case java.sql.Types.JAVA_OBJECT:
        return resultSet.getObject(column);
      case java.sql.Types.LONGVARBINARY:
        return resultSet.getBytes(column);
      case java.sql.Types.LONGVARCHAR:
        return resultSet.getString(column);
      case java.sql.Types.NULL:
        return null;
      case java.sql.Types.NUMERIC:
        return resultSet.getBigDecimal(column);
      case java.sql.Types.OTHER:
        return resultSet.getObject(column);
      case java.sql.Types.REAL: {
        final double ret = resultSet.getDouble(column);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
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
      case java.sql.Types.TINYINT: {
        final byte ret = resultSet.getByte(column);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case java.sql.Types.VARBINARY:
        return resultSet.getBytes(column);
      case java.sql.Types.VARCHAR:
        return resultSet.getString(column);
      default:
        log.warn(
            "Could not get value for result set using type [{}] on column [{}]. ResultSet.getObject method will be used.",
            sqlTypeToString(type), column);
        return resultSet.getObject(column);
    }
  }

  public static final Map<Integer, String> typeStringMap = initalizeTypeStringMap();

  public static String sqlTypeToString(final int type) {
    return typeStringMap.getOrDefault(type, "");
  }

  private static Map<Integer, String> initalizeTypeStringMap() {
    Map<Integer, String> typeStringMap = new HashMap<>();
    typeStringMap.put(java.sql.Types.ARRAY, "ARRAY");
    typeStringMap.put(java.sql.Types.BIGINT, "BIGINT");
    typeStringMap.put(java.sql.Types.BINARY, "BINARY");
    typeStringMap.put(java.sql.Types.BIT, "BIT");
    typeStringMap.put(java.sql.Types.BLOB, "BLOB");
    typeStringMap.put(java.sql.Types.BOOLEAN, "BOOLEAN");
    typeStringMap.put(java.sql.Types.CHAR, "CHAR");
    typeStringMap.put(java.sql.Types.CLOB, "CLOB");
    typeStringMap.put(java.sql.Types.DATALINK, "DATALINK");
    typeStringMap.put(java.sql.Types.DATE, "DATE");
    typeStringMap.put(java.sql.Types.DECIMAL, "DECIMAL");
    typeStringMap.put(java.sql.Types.DISTINCT, "DISTINCT");
    typeStringMap.put(java.sql.Types.DOUBLE, "DOUBLE");
    typeStringMap.put(java.sql.Types.FLOAT, "FLOAT");
    typeStringMap.put(java.sql.Types.INTEGER, "INTEGER");
    typeStringMap.put(java.sql.Types.JAVA_OBJECT, "JAVA_OBJECT");
    typeStringMap.put(java.sql.Types.LONGVARBINARY, "LONGVARBINARY");
    typeStringMap.put(java.sql.Types.LONGVARCHAR, "LONGVARCHAR");
    typeStringMap.put(java.sql.Types.NULL, "NULL");
    typeStringMap.put(java.sql.Types.NUMERIC, "NUMERIC");
    typeStringMap.put(java.sql.Types.OTHER, "OTHER");
    typeStringMap.put(java.sql.Types.REAL, "REAL");
    typeStringMap.put(java.sql.Types.REF, "REF");
    typeStringMap.put(java.sql.Types.ROWID, "ROWID");
    typeStringMap.put(java.sql.Types.SMALLINT, "SMALLINT");
    typeStringMap.put(java.sql.Types.STRUCT, "STRUCT");
    typeStringMap.put(java.sql.Types.TIME, "TIME");
    typeStringMap.put(java.sql.Types.TIMESTAMP, "TIMESTAMP");
    typeStringMap.put(java.sql.Types.TINYINT, "TINYINT");
    typeStringMap.put(java.sql.Types.VARBINARY, "VARBINARY");
    typeStringMap.put(java.sql.Types.VARCHAR, "VARCHAR");
    return typeStringMap;
  }


}
