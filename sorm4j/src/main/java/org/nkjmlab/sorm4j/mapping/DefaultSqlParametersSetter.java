package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.internal.util.ArrayUtils.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;

/**
 * Default implementation of {@link SqlParametersSetter}
 *
 * @author nkjm
 *
 */

public final class DefaultSqlParametersSetter implements SqlParametersSetter {

  private final Map<Class<?>, SqlParameterSetter> setters;

  public DefaultSqlParametersSetter() {
    this.setters = Collections.emptyMap();
  }

  public DefaultSqlParametersSetter(Map<Class<?>, SqlParameterSetter> setters) {
    this.setters = Map.copyOf(setters);
  }

  @Override
  public void setParameters(PreparedStatement stmt, Object... parameters) throws SQLException {
    if (parameters == null) {
      return;
    }
    for (int i = 1; i <= parameters.length; i++) {
      setParameter(stmt, i, parameters[i - 1]);
    }
  }

  /**
   * Sets a parameter into the given prepared statement. i.e. Convert from java objects to SQL.
   *
   * <strong>TIME</strong>
   * <p>
   * In JDBC this data type is mapped to java.sql.Time. java.time.LocalTime is also supported and
   * recommended. See <a href=
   * "https://www.h2database.com/html/datatypes.html?highlight=datatype&search=DataType#time_type">Data
   * Types</a>.
   *
   * @param stmt {@link java.sql.PreparedStatement} to have parameters set into
   * @param parameter parameters values
   *
   * @throws SQLException
   *
   */
  private void setParameter(PreparedStatement stmt, int parameterIndex, Object parameter)
      throws SQLException {

    if (parameter == null) {
      stmt.setNull(parameterIndex, java.sql.Types.NULL);
      return;
    }

    final Class<?> type = parameter.getClass();
    final SqlParameterSetter setter = setters.get(type);
    if (setter != null) {
      setter.setParameter(stmt, parameterIndex, parameter);
      return;
    }

    switch (type.getName()) {
      case "java.lang.Boolean":
      case "boolean":
        stmt.setBoolean(parameterIndex, (Boolean) parameter);
        return;
      case "java.lang.Byte":
      case "byte":
        stmt.setByte(parameterIndex, (Byte) parameter);
        return;
      case "java.lang.Short":
      case "short":
        stmt.setShort(parameterIndex, (Short) parameter);
        return;
      case "java.lang.Integer":
      case "int":
        stmt.setInt(parameterIndex, (Integer) parameter);
        return;
      case "java.lang.Long":
      case "long":
        stmt.setLong(parameterIndex, (Long) parameter);
        return;
      case "java.lang.Float":
      case "float":
        stmt.setFloat(parameterIndex, (Float) parameter);
        return;
      case "java.lang.Double":
      case "double":
        stmt.setDouble(parameterIndex, (Double) parameter);
        return;
      case "java.lang.Character":
      case "char":
        stmt.setString(parameterIndex, parameter == null ? null : "" + (Character) parameter);
        return;
      case "java.lang.String":
        stmt.setString(parameterIndex, (String) parameter);
        return;
      case "java.math.BigDecimal":
        stmt.setBigDecimal(parameterIndex, (BigDecimal) parameter);
        return;
      case "java.sql.Date":
        stmt.setDate(parameterIndex, (java.sql.Date) parameter);
        return;
      case "java.sql.Time":
        stmt.setTime(parameterIndex, (java.sql.Time) parameter);
        return;
      case "java.sql.Timestamp":
        stmt.setTimestamp(parameterIndex, (java.sql.Timestamp) parameter);
        return;
      case "java.time.Instant":
        stmt.setTimestamp(parameterIndex,
            parameter == null ? null : Timestamp.from((java.time.Instant) parameter));
        return;
      case "java.time.LocalTime":
        stmt.setTime(parameterIndex,
            parameter == null ? null : java.sql.Time.valueOf((java.time.LocalTime) parameter));
        return;
      case "java.time.LocalDate":
        stmt.setDate(parameterIndex,
            parameter == null ? null : java.sql.Date.valueOf((java.time.LocalDate) parameter));
        return;
      case "java.time.LocalDateTime":
        stmt.setTimestamp(parameterIndex,
            parameter == null ? null : Timestamp.valueOf((java.time.LocalDateTime) parameter));
        return;
      case "java.time.OffsetTime":
        stmt.setObject(parameterIndex, parameter);
        return;
      case "java.time.OffsetDateTime":
        stmt.setObject(parameterIndex, parameter);
        return;
      case "java.util.Date":
        stmt.setTimestamp(parameterIndex,
            parameter == null ? null : new Timestamp(((java.util.Date) parameter).getTime()));
        return;
      case "java.util.UUID":
        stmt.setString(parameterIndex,
            parameter == null ? null : ((java.util.UUID) parameter).toString());
        return;
      default:
        if (type.isArray()) {
          final Class<?> compType = type.getComponentType();
          procArray(compType, stmt, parameterIndex, parameter);
        } else if (parameter instanceof List) {
          List<?> list = (List<?>) parameter;
          if (list.isEmpty()) {
            throw new SormException(
                "Size of parameter which type is List should be at least one. ");
          }
          procArray(list.get(0).getClass(), stmt, parameterIndex, list.toArray());
        } else if (type.isEnum()) {
          stmt.setString(parameterIndex, parameter.toString());
        } else if (parameter instanceof java.sql.Blob) {
          stmt.setBlob(parameterIndex, (java.sql.Blob) parameter);
        } else if (parameter instanceof java.sql.Clob) {
          stmt.setClob(parameterIndex, (java.sql.Clob) parameter);
        } else if (parameter instanceof java.io.InputStream) {
          final java.io.InputStream is = (java.io.InputStream) parameter;
          stmt.setBinaryStream(parameterIndex, is);
        } else if (parameter instanceof java.io.Reader) {
          final java.io.Reader reader = (java.io.Reader) parameter;
          stmt.setCharacterStream(parameterIndex, reader);
        } else {
          stmt.setObject(parameterIndex, parameter);
        }
    }

  }



  /**
   * Treats array.
   *
   * @param stmt
   * @param parameterIndex
   * @param parameter
   * @throws SQLException
   */
  private void procArray(Class<?> compType, PreparedStatement stmt, int parameterIndex,
      Object parameter) throws SQLException {
    String typeName = compType.getName();
    if (typeName.equals("byte")) {
      stmt.setBytes(parameterIndex, (byte[]) parameter);
    } else {
      stmt.setArray(parameterIndex, toSqlArray(typeName, stmt.getConnection(), parameter));
    }
  }

  static java.sql.Array toSqlArray(String typeName, Connection conn, Object parameter)
      throws SQLException {
    switch (typeName) {
      case "boolean":
        return conn.createArrayOf("BOOLEAN", toObjectArray((boolean[]) parameter));
      case "java.lang.Boolean":
        return conn.createArrayOf("BOOLEAN", (Object[]) parameter);
      case "byte":
        return conn.createArrayOf("TINYINT", toObjectArray((byte[]) parameter));
      case "java.lang.Byte":
        return conn.createArrayOf("TINYINT", (Object[]) parameter);
      case "short":
        return conn.createArrayOf("SMALLINT", ArrayUtils.toObjectArray((short[]) parameter));
      case "java.lang.Short":
        return conn.createArrayOf("SMALLINT", (Object[]) parameter);
      case "int":
        return conn.createArrayOf("INTEGER", ArrayUtils.toObjectArray((int[]) parameter));
      case "java.lang.Integer":
        return conn.createArrayOf("INTEGER", (Object[]) parameter);
      case "long":
        return conn.createArrayOf("BINGINT", ArrayUtils.toObjectArray((long[]) parameter));
      case "java.lang.Long":
        return conn.createArrayOf("BIGINT", (Object[]) parameter);
      case "float":
        return conn.createArrayOf("REAL", ArrayUtils.toObjectArray((float[]) parameter));
      case "java.lang.Float":
        return conn.createArrayOf("REAL", (Object[]) parameter);
      case "double":
        return conn.createArrayOf("DOUBLE", ArrayUtils.toObjectArray((double[]) parameter));
      case "java.lang.Double":
        return conn.createArrayOf("DOUBLE", (Object[]) parameter);
      case "char":
        return conn.createArrayOf("CHARACTER", ArrayUtils.toObjectArray((char[]) parameter));
      case "java.lang.Character":
        return conn.createArrayOf("CHARACTER", (Object[]) parameter);
      case "java.lang.String":
        return conn.createArrayOf("VARCHAR", (Object[]) parameter);
      case "java.math.BigDecimal":
        return conn.createArrayOf("NUMERIC", (Object[]) parameter);
      case "java.sql.Date":
        return conn.createArrayOf("DATE", (Object[]) parameter);
      case "java.sql.Time":
        return conn.createArrayOf("TIME", (Object[]) parameter);
      case "java.sql.Timestamp":
        return conn.createArrayOf("TIMESTAMP", (Object[]) parameter);
      default:
        return conn.createArrayOf("JAVA_OBJECT", (Object[]) parameter);
    }
  }


}
