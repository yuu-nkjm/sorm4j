package org.nkjmlab.sorm4j.internal.context.impl;

import static org.nkjmlab.sorm4j.internal.util.ArrayUtils.convertToObjectArray;
import static org.nkjmlab.sorm4j.internal.util.ArrayUtils.toObjectArray;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nkjmlab.sorm4j.context.SqlParameterSetter;
import org.nkjmlab.sorm4j.internal.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * Default implementation of {@link SqlParametersSetter}
 *
 * @author nkjm
 */
public final class DefaultSqlParametersSetter implements SqlParametersSetter {

  private final List<SqlParameterSetter> setters;
  private final Map<Class<?>, SqlParameterSetter> settersHitCache;

  private static final DummySetter DUMMY_SETTER = new DummySetter();

  private static final class DummySetter implements SqlParameterSetter {

    @Override
    public boolean test(PreparedStatement stmt, int parameterIndex, Object parameter)
        throws SQLException {
      return false;
    }

    @Override
    public void setParameter(PreparedStatement stmt, int parameterIndex, Object parameter)
        throws SQLException {}
  }

  public DefaultSqlParametersSetter(SqlParameterSetter... setters) {
    this.setters = Arrays.asList(setters);
    this.settersHitCache =
        this.setters.isEmpty() ? Collections.emptyMap() : new ConcurrentHashMap<>();
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
   * <p><strong>TIME</strong>
   *
   * <p>In JDBC this data type is mapped to java.sql.Time. java.time.LocalTime is also supported and
   * recommended. See <a href=
   * "https://www.h2database.com/html/datatypes.html?highlight=datatype&search=DataType#time_type">Data
   * Types</a>.
   *
   * @param stmt {@link java.sql.PreparedStatement} to have parameters set into
   * @param parameter parameters values
   * @throws SQLException
   */
  private void setParameter(PreparedStatement stmt, int parameterIndex, Object parameter)
      throws SQLException {

    if (parameter == null) {
      stmt.setNull(parameterIndex, java.sql.Types.NULL);
      return;
    }

    final Class<?> type = parameter.getClass();
    if (!setters.isEmpty()) {
      final SqlParameterSetter setter =
          settersHitCache.computeIfAbsent(
              type,
              key ->
                  setters.stream()
                      .filter(
                          _setter -> {
                            try {
                              return _setter.test(stmt, parameterIndex, parameter);
                            } catch (SQLException e) {
                              throw Try.rethrow(e);
                            }
                          })
                      .findFirst()
                      .orElse(DUMMY_SETTER));

      if (!setter.equals(DUMMY_SETTER)) {
        setter.setParameter(stmt, parameterIndex, parameter);
        return;
      }
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
      case "java.util.Date":
      case "java.time.LocalTime":
      case "java.time.LocalDate":
      case "java.time.LocalDateTime":
      case "java.time.OffsetTime":
      case "java.time.OffsetDateTime":
      case "java.time.Instant":
      case "java.util.UUID":
        stmt.setObject(parameterIndex, parameter);
        return;
      case "org.nkjmlab.sorm4j.util.datatype.JsonByte":
        stmt.setObject(
            parameterIndex, ((org.nkjmlab.sorm4j.container.datatype.JsonByte) parameter).getBytes());
        return;
      case "org.nkjmlab.sorm4j.util.datatype.GeometryString":
        stmt.setString(
            parameterIndex,
            ((org.nkjmlab.sorm4j.container.datatype.GeometryString) parameter).getText());
        return;
      default:
        if (type.isArray()) {
          final Class<?> compType = type.getComponentType();
          procArray(compType, stmt, parameterIndex, parameter);
        } else if (type.isEnum()) {
          stmt.setString(parameterIndex, ((Enum<?>) parameter).name());
        } else if (parameter instanceof java.sql.Blob) {
          stmt.setBlob(parameterIndex, (java.sql.Blob) parameter);
        } else if (parameter instanceof java.sql.Clob) {
          stmt.setClob(parameterIndex, (java.sql.Clob) parameter);
        } else if (parameter instanceof java.io.InputStream) {
          stmt.setBinaryStream(parameterIndex, (java.io.InputStream) parameter);
        } else if (parameter instanceof java.io.Reader) {
          stmt.setCharacterStream(parameterIndex, (java.io.Reader) parameter);
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
  private void procArray(
      Class<?> compType, PreparedStatement stmt, int parameterIndex, Object parameter)
      throws SQLException {
    String typeName = compType.getName();
    if (typeName.equals("byte")) {
      stmt.setBytes(parameterIndex, (byte[]) parameter);
    } else if (typeName.contains("[")) {
      stmt.setArray(
          parameterIndex,
          stmt.getConnection()
              .createArrayOf("java_object", convertToObjectArray((Object[]) parameter)));
    } else {
      stmt.setArray(parameterIndex, toSqlArray(typeName, stmt.getConnection(), parameter));
    }
  }

  public static java.sql.Array toSqlArray(String typeName, Connection conn, Object parameter)
      throws SQLException {
    switch (typeName) {
      case "boolean":
        return conn.createArrayOf("boolean", toObjectArray((boolean[]) parameter));
      case "byte":
        return conn.createArrayOf("tinyint", toObjectArray((byte[]) parameter));
      case "char":
        return conn.createArrayOf("character", ArrayUtils.toObjectArray((char[]) parameter));
      case "short":
        return conn.createArrayOf("smallint", ArrayUtils.toObjectArray((short[]) parameter));
      case "int":
        return conn.createArrayOf("integer", ArrayUtils.toObjectArray((int[]) parameter));
      case "long":
        return conn.createArrayOf("bigint", ArrayUtils.toObjectArray((long[]) parameter));
      case "float":
        return conn.createArrayOf("real", ArrayUtils.toObjectArray((float[]) parameter));
      case "double":
        return conn.createArrayOf("double", ArrayUtils.toObjectArray((double[]) parameter));
      default:
        // The first argument is "JAVA_OBJECT", however the type of elements depends on JDBC driver.
        // The JDBC driver is responsible for mapping the elements Object array to the default JDBC
        // SQL type defined injava.sql.Types for the given class of Object.
        return conn.createArrayOf("java_object", (Object[]) parameter);
    }
  }
}
