package org.nkjmlab.sorm4j.context;

import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.internal.util.JdbcTypeUtils;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.util.json.JsonByte;

/**
 * Default implementation of {@link ColumnValueToJavaObjectConverters}
 *
 * @author nkjm
 *
 */

public final class DefaultColumnValueToJavaObjectConverters
    implements ColumnValueToJavaObjectConverters {


  private final Map<Class<?>, ColumnValueToJavaObjectConverter> convertersHitCache;
  private final List<ColumnValueToJavaObjectConverter> converters;

  private static final DummyConverter DUMMY_CONVERTER = new DummyConverter();

  private static final class DummyConverter implements ColumnValueToJavaObjectConverter {

    @Override
    public boolean test(ResultSet resultSet, int columnIndex, int columnType, Class<?> toType) {
      return false;
    }

    @Override
    public Object convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<?> toType)
        throws SQLException {
      return null;
    }
  }


  /**
   *
   * @param converters the converter which corresponding to the key class is applied to the column
   *        value.
   */
  public DefaultColumnValueToJavaObjectConverters(ColumnValueToJavaObjectConverter... converters) {
    this.converters = Arrays.asList(converters);
    this.convertersHitCache =
        this.converters.isEmpty() ? Collections.emptyMap() : new ConcurrentHashMap<>();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<T> toType) {
    try {
      return (T) convertToHelper(resultSet, columnIndex, columnType, toType);
    } catch (Exception e) {
      throw new SormException(
          ParameterizedStringUtils.newString("ColumnIndex={},ColumnType={},toType={}", columnIndex,
              JdbcTypeUtils.convert(columnType), toType),
          e);
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private Object convertToHelper(ResultSet resultSet, int columnIndex, int columnType,
      Class<?> toType) throws SQLException {

    if (!converters.isEmpty()) {
      final ColumnValueToJavaObjectConverter converter =
          convertersHitCache.computeIfAbsent(toType, key -> converters.stream().filter(conv -> {
            try {
              return conv.test(resultSet, columnIndex, columnType, toType);
            } catch (SQLException e) {
              throw Try.rethrow(e);
            }
          }).findFirst().orElse(DUMMY_CONVERTER));

      if (!converter.equals(DUMMY_CONVERTER)) {
        return converter.convertTo(resultSet, columnIndex, columnType, toType);
      }
    }

    switch (toType.getName()) {
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
      case "java.util.Date":
      case "java.time.LocalTime":
      case "java.time.LocalDate":
      case "java.time.LocalDateTime":
      case "java.time.Instant":
      case "java.util.UUID":
      case "java.time.OffsetTime":
      case "java.time.OffsetDateTime":
        return resultSet.getObject(columnIndex, toType);
      case "org.nkjmlab.sorm4j.util.json.JsonByte":
        return new JsonByte(resultSet.getBytes(columnIndex));
      default:
        if (toType.isEnum()) {
          try {
            return Enum.valueOf((Class<? extends Enum>) toType, resultSet.getString(columnIndex));
          } catch (Exception e) {
            return null;
          }
        } else if (toType.isArray()) {
          if (toType.getComponentType().getName().equals("byte")) {
            return resultSet.getBytes(columnIndex);
          }
          try {
            return ArrayUtils.convertSqlArrayToArray(toType.getComponentType(),
                resultSet.getArray(columnIndex));
          } catch (Exception e) {
            throw new SormException(
                ParameterizedStringUtils.newString("Could not convert column ({}) to  array ({}[])",
                    JDBCType.valueOf(columnType).getName(), toType.getComponentType().getName()),
                e);
          }
        } else {
          return resultSet.getObject(columnIndex, toType);
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
      java.util.UUID.class);



}
