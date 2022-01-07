package org.nkjmlab.sorm4j.extension.impl;

import java.lang.reflect.Array;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.extension.ColumnValueToJavaObjectConverter;
import org.nkjmlab.sorm4j.extension.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.internal.util.ClassUtils;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;

/**
 * Default implementation of {@link ColumnValueToJavaObjectConverters}
 *
 * @author nkjm
 *
 */

public final class DefaultColumnValueToJavaObjectConverters
    implements ColumnValueToJavaObjectConverters {


  private final Map<Class<?>, ColumnValueToJavaObjectConverter> converters;

  public DefaultColumnValueToJavaObjectConverters() {
    this(Collections.emptyMap());
  }


  public DefaultColumnValueToJavaObjectConverters(
      Map<Class<?>, ColumnValueToJavaObjectConverter> converters) {
    this.converters = converters;
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> T convertTo(SormOptions options, ResultSet resultSet, int column, int columnType,
      Class<T> toType) throws SQLException {
    return (T) convertToHelper(options, resultSet, column, columnType, toType);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private Object convertToHelper(SormOptions options, ResultSet resultSet, int column,
      int columnType, Class<?> toType) throws SQLException {

    final ColumnValueToJavaObjectConverter converter = converters.get(toType);
    if (converter != null) {
      return converter.convertTo(options, resultSet, column, columnType, toType);
    }

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
      case "java.lang.String":
        return resultSet.getString(column);
      case "java.lang.Character":
      case "char": {
        final String str = resultSet.getString(column);
        return (str == null || str.length() == 0) ? null : str.charAt(0);
      }
      case "java.lang.Object":
        return resultSet.getObject(column);
      case "java.io.InputStream":
        return resultSet.getBinaryStream(column);
      case "java.io.Reader":
        return resultSet.getCharacterStream(column);
      case "java.math.BigDecimal":
        return resultSet.getBigDecimal(column);
      case "java.sql.Date":
        return resultSet.getDate(column);
      case "java.sql.Time":
        return resultSet.getTime(column);
      case "java.sql.Timestamp":
        return resultSet.getTimestamp(column);
      case "java.sql.Blob":
        return resultSet.getBlob(column);
      case "java.sql.Clob":
        return resultSet.getClob(column);
      case "java.time.Instant":
        return Optional.ofNullable(resultSet.getTimestamp(column)).map(t -> t.toInstant())
            .orElse(null);
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
        return Optional.ofNullable(resultSet.getObject(column)).orElse(null);
      case "java.time.OffsetDateTime":
        return Optional.ofNullable(resultSet.getObject(column)).orElse(null);
      case "java.util.ArrayList":
      case "java.util.List": {
        java.sql.Array arry = resultSet.getArray(column);
        Object srcArry = arry.getArray();
        final int length = Array.getLength(srcArry);
        List<Object> ret = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
          ret.add(Array.get(srcArry, i));
        }
        return ret;
      }
      default:
        if (toType.isEnum()) {
          try {
            return Enum.valueOf((Class<? extends Enum>) toType, resultSet.getString(column));
          } catch (Exception e) {
            return null;
          }
        } else if (toType.isArray()) {
          final String compName = toType.getComponentType().getName();
          switch (compName) {
            case "byte":
              return resultSet.getBytes(column);
            default: {
              java.sql.Array arry = resultSet.getArray(column);
              Object srcArry = arry.getArray();
              final int length = Array.getLength(srcArry);
              Object destArray = Array.newInstance(ClassUtils.convertToClass(compName), length);
              try {
                for (int i = 0; i < length; i++) {
                  Object v = Array.get(srcArry, i);
                  Array.set(destArray, i, v);
                }
              } catch (Exception e) {
                throw new SormException(ParameterizedStringUtils.newString(
                    "Could not convert column ({}) to  array ({}[])",
                    JDBCType.valueOf(columnType).getName(), compName));
              }
              return destArray;
            }
          }
        } else {
          return resultSet.getObject(column);
          // throw new SormException(ParameterizedStringUtils.newString(
          // "Could not find corresponding converter columnType={}, toType={}. ",
          // JDBCType.valueOf(columnType).getName(), toType));
        }
    }
  }


  @Override
  public boolean isSupportedType(SormOptions options, Class<?> objectClass) {
    return supportedTypes.contains(objectClass) || objectClass.isArray();
  }

  private static final Set<Class<?>> supportedTypes = Set.of(boolean.class, byte.class, short.class,
      int.class, long.class, float.class, double.class, char.class, java.io.InputStream.class,
      java.io.Reader.class, java.lang.Boolean.class, java.lang.Byte.class, java.lang.Short.class,
      java.lang.Integer.class, java.lang.Long.class, java.lang.Float.class, java.lang.Double.class,
      java.lang.Character.class, java.lang.String.class, java.lang.Object.class,
      java.math.BigDecimal.class, java.sql.Clob.class, java.sql.Blob.class, java.sql.Date.class,
      java.sql.Time.class, java.sql.Timestamp.class, java.time.Instant.class,
      java.time.LocalDate.class, java.time.LocalTime.class, java.time.LocalDateTime.class,
      java.time.OffsetTime.class, java.time.OffsetDateTime.class, java.util.Date.class,
      java.util.UUID.class, java.util.List.class);



}
