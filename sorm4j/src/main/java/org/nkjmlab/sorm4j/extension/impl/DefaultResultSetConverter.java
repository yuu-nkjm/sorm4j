package org.nkjmlab.sorm4j.extension.impl;

import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.extension.ColumnValueConverter;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * Default implementation of {@link ResultSetConverter}
 *
 * @author nkjm
 *
 */

public class DefaultResultSetConverter implements ResultSetConverter {


  private static final Set<Class<?>> standardObjectClasses = Set.of(boolean.class, Boolean.class,
      byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class,
      Long.class, float.class, Float.class, double.class, Double.class, char.class, Character.class,
      String.class, BigDecimal.class, java.sql.Clob.class, java.sql.Blob.class, java.sql.Date.class,
      java.sql.Time.class, java.sql.Timestamp.class, java.time.LocalDate.class,
      java.time.LocalTime.class, java.time.LocalDateTime.class, java.time.OffsetTime.class,
      java.time.OffsetDateTime.class, java.util.Date.class, java.util.UUID.class,
      java.io.InputStream.class, java.io.Reader.class, java.net.URL.class,
      java.net.Inet4Address.class, java.net.Inet6Address.class, Object.class);

  /**
   * Returns the given type is enable to convert element object.
   *
   * Following classes and Array are regarded as native class.
   *
   * boolean.class, Boolean.class, byte.class, Byte.class, short.class, Short.class, int.class,
   * Integer.class, long.class, Long.class, float.class, Float.class, double.class, Double.class,
   * char.class, Character.class, byte[].class, Byte[].class, char[].class, Character[].class,
   * String.class, BigDecimal.class, java.sql.Clob.class, java.sql.Blob.class, java.sql.Date.class,
   * java.sql.Time.class, java.sql.Timestamp.class, java.time.LocalDate.class,
   * java.time.LocalTime.class, java.time.LocalDateTime.class, java.time.OffsetTime.class,
   * java.time.OffsetDateTime.class, java.util.Date.class, java.util.UUID.class,
   * java.io.InputStream.class, java.io.Reader.class, java.net.URL.class,
   * java.net.Inet4Address.class, java.net.Inet6Address.class, Object.class
   */
  @Override
  public boolean isStandardClass(SormOptions options, Class<?> objectClass) {
    return standardObjectClasses.contains(objectClass) || objectClass.isArray();
  }

  private final List<ColumnValueConverter> converters;

  public DefaultResultSetConverter() {
    this(LetterCaseOfKeyInMap.LOWER_CASE, Collections.emptyList());
  }

  public DefaultResultSetConverter(LetterCaseOfKeyInMap letterCaseOfKeyInMap) {
    this(letterCaseOfKeyInMap, Collections.emptyList());
  }

  public DefaultResultSetConverter(LetterCaseOfKeyInMap letterCaseOfKeyInMap,
      List<ColumnValueConverter> converters) {
    this(letterCaseOfKeyInMap, converters.toArray(ColumnValueConverter[]::new));
  }

  public DefaultResultSetConverter(LetterCaseOfKeyInMap letterCaseOfKeyInMap,
      ColumnValueConverter... converters) {
    this.converters = converters.length == 0 ? Collections.emptyList() : Arrays.asList(converters);
    this.letterCaseOfKeyInMap = letterCaseOfKeyInMap;
  }

  @Experimental
  private final LetterCaseOfKeyInMap letterCaseOfKeyInMap;


  public enum LetterCaseOfKeyInMap {
    LOWER_CASE, UPPER_CASE, CANONICAL_CASE, NO_CONVERSION;

  }

  private String convertKey(String key) {
    switch (letterCaseOfKeyInMap) {
      case LOWER_CASE:
        return toLowerCase(key);
      case UPPER_CASE:
        return toUpperCase(key);
      case CANONICAL_CASE:
        return toCanonicalCase(key);
      case NO_CONVERSION:
      default:
        return key;
    }
  }

  @Override
  public Map<String, Object> toSingleMap(SormOptions options, ResultSet resultSet,
      List<String> columns, List<Integer> columnTypes) throws SQLException {
    final int cSize = columns.size();
    final Map<String, Object> ret = new LinkedHashMap<>(cSize);
    for (int i = 1; i <= cSize; i++) {
      ret.put(convertKey(columns.get(i - 1)),
          getColumnValueBySqlType(resultSet, i, columnTypes.get(i - 1)));
    }
    return ret;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T toSingleStandardObject(SormOptions options, ResultSet resultSet, int sqlType,
      Class<T> objectClass) throws SQLException {
    return (T) convertColumnValueTo(options, resultSet, 1, sqlType, objectClass);
  }


  private static Class<?> componentType(String name) {
    switch (name) {
      case "boolean":
        return boolean.class;
      case "char":
        return char.class;
      case "byte":
        return byte.class;
      case "short":
        return short.class;
      case "int":
        return int.class;
      case "long":
        return long.class;
      case "float":
        return float.class;
      case "double":
        return double.class;
      default:
        return Try.getOrElseThrow(() -> Class.forName(name), Try::rethrow);
    }
  }


  // 2021-03-26 An approach to create converter at once and apply the converter to get result is
  // slower than the current code. https://github.com/yuu-nkjm/sorm4j/issues/25

  @Override
  public Object convertColumnValueTo(SormOptions options, ResultSet resultSet, int column,
      int columnType, Class<?> toType) throws SQLException {

    if (converters.size() != 0) {
      Optional<ColumnValueConverter> conv = converters.stream()
          .filter(co -> co.isApplicable(options, resultSet, column, columnType, toType))
          .findFirst();
      if (conv.isPresent()) {
        return conv.get().convertTo(options, resultSet, column, columnType, toType);
      }
    }

    if (toType.isEnum()) {
      final String v = resultSet.getString(column);
      return Arrays.stream(toType.getEnumConstants()).filter(o -> o.toString().equals(v)).findAny()
          .orElse(null);
    } else if (toType.isArray()) {
      final String name = toType.getComponentType().getName();
      switch (name) {
        case "byte":
        case "java.lang.Byte":
          return resultSet.getBytes(column);
        case "char":
        case "java.lang.Character": {
          final String str = resultSet.getString(column);
          return (str == null) ? null : str.toCharArray();
        }
        default: {
          java.sql.Array arry = resultSet.getArray(column);
          Object srcArry = arry.getArray();
          final int length = Array.getLength(srcArry);
          Object destArray = Array.newInstance(componentType(name), length);
          for (int i = 0; i < length; i++) {
            Object v = Array.get(srcArry, i);
            Array.set(destArray, i, v);
          }
          return destArray;
        }
      }
    } else {
      final String name = toType.getName();
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
          return Optional.ofNullable(resultSet.getTime(column)).map(t -> t.toLocalTime())
              .orElse(null);
        case "java.time.LocalDate":
          return Optional.ofNullable(resultSet.getDate(column)).map(t -> t.toLocalDate())
              .orElse(null);
        case "java.time.LocalDateTime":
          return Optional.ofNullable(resultSet.getTimestamp(column)).map(t -> t.toLocalDateTime())
              .orElse(null);
        case "java.util.Date":
          return Optional.ofNullable(resultSet.getTimestamp(column))
              .map(t -> new java.util.Date(t.getTime())).orElse(null);
        case "java.util.UUID":
          return Optional.ofNullable(resultSet.getString(column))
              .map(s -> java.util.UUID.fromString(s)).orElse(null);
        case "java.time.OffsetTime":
          return Optional.ofNullable(resultSet.getTimestamp(column)).map(t -> t.toLocalDateTime()
              .atZone(ZoneId.systemDefault()).toOffsetDateTime().toOffsetTime()).orElse(null);
        case "java.time.OffsetDateTime":
          return Optional.ofNullable(resultSet.getTimestamp(column))
              .map(t -> t.toLocalDateTime().atZone(ZoneId.systemDefault()).toOffsetDateTime())
              .orElse(null);
        case "java.net.URL":
          return Optional.ofNullable(resultSet.getString(column))
              .map(s -> Try.getOrElseNull(() -> new java.net.URL(s))).orElse(null);
        case "java.net.Inet4Address":
          return Optional.ofNullable(resultSet.getString(column))
              .map(s -> Try.getOrElseNull(() -> java.net.Inet4Address.getByName(s))).orElse(null);
        case "java.net.Inet6Address":
          return Optional.ofNullable(resultSet.getString(column))
              .map(s -> Try.getOrElseNull(() -> java.net.Inet6Address.getByName(s))).orElse(null);
        case "java.lang.Object":
          return resultSet.getObject(column);
        default:
          // Could not find corresponding converter. ResultSet#getObject method will be used.
          return resultSet.getObject(column);
      }
    }
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
  protected Object getColumnValueBySqlType(ResultSet resultSet, int column, int sqlType)
      throws SQLException {

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
        return resultSet.getBinaryStream(column);
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
