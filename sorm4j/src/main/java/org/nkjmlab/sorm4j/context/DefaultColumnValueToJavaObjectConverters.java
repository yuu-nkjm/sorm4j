package org.nkjmlab.sorm4j.context;

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
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
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


  private final Map<Class<?>, ColumnValueToJavaObjectConverter<?>> converters;

  public DefaultColumnValueToJavaObjectConverters() {
    this(Collections.emptyMap());
  }


  /**
   *
   * @param converters the converter which corresponding to the key class is applied to the column
   *        value.
   */
  public DefaultColumnValueToJavaObjectConverters(
      Map<Class<?>, ColumnValueToJavaObjectConverter<?>> converters) {
    this.converters = Map.copyOf(converters);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<T> toType)
      throws SQLException {
    return (T) convertToHelper(resultSet, columnIndex, columnType, toType);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private Object convertToHelper(ResultSet resultSet, int columnIndex, int columnType,
      Class<?> toType) throws SQLException {

    final ColumnValueToJavaObjectConverter converter = converters.get(toType);
    if (converter != null) {
      return converter.convertTo(resultSet, columnIndex, columnType, toType);
    }

    final String name = toType.getName();
    switch (name) {
      case "boolean":
        return resultSet.getBoolean(columnIndex);
      case "java.lang.Boolean": {
        final boolean ret = resultSet.getBoolean(columnIndex);
        return (!ret && resultSet.wasNull()) ? null : ret;
      }
      case "byte":
        return resultSet.getByte(columnIndex);
      case "java.lang.Byte": {
        final byte ret = resultSet.getByte(columnIndex);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case "short":
        return resultSet.getShort(columnIndex);
      case "java.lang.Short": {
        final short ret = resultSet.getShort(columnIndex);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case "int":
        return resultSet.getInt(columnIndex);
      case "java.lang.Integer": {
        final int ret = resultSet.getInt(columnIndex);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case "long":
        return resultSet.getLong(columnIndex);
      case "java.lang.Long": {
        final long ret = resultSet.getLong(columnIndex);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case "float":
        return resultSet.getFloat(columnIndex);
      case "java.lang.Float": {
        final float ret = resultSet.getFloat(columnIndex);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case "double":
        return resultSet.getDouble(columnIndex);
      case "java.lang.Double": {
        final double ret = resultSet.getDouble(columnIndex);
        return (ret == 0 && resultSet.wasNull()) ? null : ret;
      }
      case "java.lang.String":
        return resultSet.getString(columnIndex);
      case "java.lang.Character":
      case "char": {
        final String str = resultSet.getString(columnIndex);
        return (str == null || str.length() == 0) ? null : str.charAt(0);
      }
      case "java.lang.Object":
        return resultSet.getObject(columnIndex);
      case "java.io.InputStream":
        return resultSet.getBinaryStream(columnIndex);
      case "java.io.Reader":
        return resultSet.getCharacterStream(columnIndex);
      case "java.math.BigDecimal":
        return resultSet.getBigDecimal(columnIndex);
      case "java.sql.Date":
        return resultSet.getDate(columnIndex);
      case "java.sql.Time":
        return resultSet.getTime(columnIndex);
      case "java.sql.Timestamp":
        return resultSet.getTimestamp(columnIndex);
      case "java.sql.Blob":
        return resultSet.getBlob(columnIndex);
      case "java.sql.Clob":
        return resultSet.getClob(columnIndex);
      case "java.time.Instant":
        return Optional.ofNullable(resultSet.getObject(columnIndex)).orElse(null);
      case "java.time.LocalTime":
        return Optional.ofNullable(resultSet.getTime(columnIndex)).map(t -> t.toLocalTime())
            .orElse(null);
      case "java.time.LocalDate":
        return Optional.ofNullable(resultSet.getDate(columnIndex)).map(t -> t.toLocalDate())
            .orElse(null);
      case "java.time.LocalDateTime":
        return Optional.ofNullable(resultSet.getTimestamp(columnIndex))
            .map(t -> t.toLocalDateTime()).orElse(null);
      case "java.util.Date":
        return Optional.ofNullable(resultSet.getTimestamp(columnIndex))
            .map(t -> new java.util.Date(t.getTime())).orElse(null);
      case "java.util.UUID":
        return Optional.ofNullable(resultSet.getString(columnIndex))
            .map(s -> java.util.UUID.fromString(s)).orElse(null);
      case "java.time.OffsetTime":
        return Optional.ofNullable(resultSet.getObject(columnIndex)).orElse(null);
      case "java.time.OffsetDateTime":
        return Optional.ofNullable(resultSet.getObject(columnIndex)).orElse(null);
      case "java.util.ArrayList":
      case "java.util.List": {
        java.sql.Array arry = resultSet.getArray(columnIndex);
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
            return Enum.valueOf((Class<? extends Enum>) toType, resultSet.getString(columnIndex));
          } catch (Exception e) {
            return null;
          }
        } else if (toType.isArray()) {
          final String compName = toType.getComponentType().getName();
          switch (compName) {
            case "byte":
              return resultSet.getBytes(columnIndex);
            default: {
              try {
                return ArrayUtils.convertToArray(ClassUtils.convertToClass(compName),
                    resultSet.getArray(columnIndex).getArray());
              } catch (Exception e) {
                throw new SormException(ParameterizedStringUtils.newString(
                    "Could not convert column ({}) to  array ({}[])",
                    JDBCType.valueOf(columnType).getName(), compName), e);
              }
            }
          }
        } else {
          return resultSet.getObject(columnIndex);
          // throw new SormException(ParameterizedStringUtils.newString(
          // "Could not find corresponding converter columnType={}, toType={}. ",
          // JDBCType.valueOf(columnType).getName(), toType));
        }
    }
  }


  @Override
  public boolean isSupportedReturnedType(Class<?> objectClass) {
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
