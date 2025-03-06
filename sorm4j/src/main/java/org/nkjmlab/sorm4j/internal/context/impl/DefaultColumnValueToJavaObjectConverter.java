package org.nkjmlab.sorm4j.internal.context.impl;

import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

import org.nkjmlab.sorm4j.common.exception.SormException;
import org.nkjmlab.sorm4j.container.datatype.GeometryString;
import org.nkjmlab.sorm4j.container.datatype.JsonByte;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverter;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.internal.util.JdbcTypeUtils;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;
import org.nkjmlab.sorm4j.internal.util.Try;

public final class DefaultColumnValueToJavaObjectConverter
    implements ColumnValueToJavaObjectConverter {

  private final Set<Class<?>> supportedComponentTypes =
      Set.of(
          boolean.class,
          byte.class,
          short.class,
          int.class,
          long.class,
          float.class,
          double.class,
          char.class,
          java.io.InputStream.class,
          java.io.Reader.class,
          java.lang.Boolean.class,
          java.lang.Byte.class,
          java.lang.Short.class,
          java.lang.Integer.class,
          java.lang.Long.class,
          java.lang.Float.class,
          java.lang.Double.class,
          java.lang.Character.class,
          java.lang.String.class,
          java.lang.Object.class,
          java.math.BigDecimal.class,
          java.sql.Clob.class,
          java.sql.Blob.class,
          java.sql.Date.class,
          java.sql.Time.class,
          java.sql.Timestamp.class,
          java.time.Instant.class,
          java.time.LocalDate.class,
          java.time.LocalTime.class,
          java.time.LocalDateTime.class,
          java.time.OffsetTime.class,
          java.time.OffsetDateTime.class,
          java.util.Date.class,
          java.util.UUID.class,
          org.nkjmlab.sorm4j.container.datatype.JsonByte.class,
          org.nkjmlab.sorm4j.container.datatype.GeometryString.class);

  @Override
  public boolean test(Class<?> objectClass) {
    return supportedComponentTypes.contains(objectClass)
        || (objectClass.isArray()
            && supportedComponentTypes.contains(
                ArrayUtils.getInternalComponentType(objectClass.getComponentType())));
  }

  @Override
  public <T> T convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<T> toType)
      throws SQLException {

    Object value = convertToAux(resultSet, columnIndex, columnType, toType);
    try {
      if (toType.isPrimitive()) {
        @SuppressWarnings("unchecked")
        T val = (T) value;
        return val;
      }

      return toType.cast(value);
    } catch (Exception e) {
      String tableName =
          Try.getOrElse(() -> resultSet.getMetaData().getTableName(columnIndex), "UNKNOWN_TABLE");
      String columnLabel =
          Try.getOrElse(
              () -> resultSet.getMetaData().getColumnLabel(columnIndex), "UNKNOWN_COLUMN");
      Object[] params = {JDBCType.valueOf(columnType), toType, tableName, columnLabel};
      throw new SormException(
          ParameterizedStringFormatter.LENGTH_256.format(
              "Could not cast column [{}] to java object type [{}], tableName=[{}], columnLabel=[{}]",
              params),
          e);
    }
  }

  private Object convertToAux(ResultSet resultSet, int columnIndex, int columnType, Class<?> toType)
      throws SQLException {
    if (toType.isEnum()) {
      return procEnum(resultSet, columnIndex, columnType, toType);
    } else if (toType.isArray()) {
      return procArray(resultSet, columnIndex, columnType, toType);
    } else {
      return procCommonTypeValue(resultSet, columnIndex, columnType, toType);
    }
  }

  private Object procCommonTypeValue(
      ResultSet resultSet, int columnIndex, int columnType, Class<?> toType) throws SQLException {
    final String typeName = toType.getName();
    switch (typeName) {
      case "boolean":
        return resultSet.getBoolean(columnIndex);
      case "byte":
        return resultSet.getByte(columnIndex);
      case "short":
        return resultSet.getShort(columnIndex);
      case "int":
        return resultSet.getInt(columnIndex);
      case "long":
        return resultSet.getLong(columnIndex);
      case "float":
        return resultSet.getFloat(columnIndex);
      case "double":
        return resultSet.getDouble(columnIndex);
      case "java.lang.String":
        return resultSet.getString(columnIndex);
      case "java.lang.Character":
      case "char":
        return Optional.ofNullable(resultSet.getString(columnIndex))
            .filter(str -> str.length() != 0)
            .map(str -> str.charAt(0))
            .orElse(null);
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
      case "org.nkjmlab.sorm4j.container.datatype.JsonByte":
        return new JsonByte(resultSet.getBytes(columnIndex));
      case "org.nkjmlab.sorm4j.container.datatype.GeometryString":
        return new GeometryString(resultSet.getString(columnIndex));
      //      case "java.util.Date":
      //      case "java.time.LocalTime":
      //      case "java.time.LocalDate":
      //      case "java.time.LocalDateTime":
      //      case "java.time.Instant":
      //      case "java.util.UUID":
      //      case "java.time.OffsetTime":
      //      case "java.time.OffsetDateTime":
      //        return resultSet.getObject(columnIndex, toType);
      default:
        return resultSet.getObject(columnIndex, toType);
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private Enum procEnum(ResultSet resultSet, int columnIndex, int columnType, Class<?> toType)
      throws SQLException {
    String str = resultSet.getString(columnIndex);
    try {
      return Enum.valueOf((Class<? extends Enum>) toType, str);
    } catch (Exception e) {
      String tableName =
          Try.getOrElse(() -> resultSet.getMetaData().getTableName(columnIndex), "UNKNOWN_TABLE");
      String columnLabel =
          Try.getOrElse(
              () -> resultSet.getMetaData().getColumnLabel(columnIndex), "UNKNOWN_COLUMN");
      Object[] params = {str, JDBCType.valueOf(columnType), toType, tableName, columnLabel};
      throw new SormException(
          ParameterizedStringFormatter.LENGTH_256.format(
              "Could not convert [{}] in column [{}] to  Enum [{}], tableName=[{}], columnLabel=[{}]",
              params),
          e);
    }
  }

  private Object procArray(ResultSet resultSet, int columnIndex, int columnType, Class<?> toType)
      throws SQLException {
    if (toType.getComponentType().equals(byte.class)) {
      return resultSet.getBytes(columnIndex);
    }
    try {
      return ArrayUtils.convertSqlArrayToArray(
          toType.getComponentType(), resultSet.getArray(columnIndex));
    } catch (Exception e) {
      String tableName =
          Try.getOrElse(() -> resultSet.getMetaData().getTableName(columnIndex), "UNKNOWN_TABLE");
      String columnLabel =
          Try.getOrElse(
              () -> resultSet.getMetaData().getColumnLabel(columnIndex), "UNKNOWN_COLUMN");
      Object[] params = {JdbcTypeUtils.convert(columnType), toType, tableName, columnLabel};
      throw new SormException(
          ParameterizedStringFormatter.LENGTH_256.format(
              "Could not convert column [{}] to  array [{}], tableName=[{}], columnLabel=[{}]",
              params),
          e);
    }
  }
}
