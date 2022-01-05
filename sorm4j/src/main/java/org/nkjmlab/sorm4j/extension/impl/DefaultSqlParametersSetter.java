package org.nkjmlab.sorm4j.extension.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.extension.ParameterSetter;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;

/**
 * Default implementation of {@link SqlParametersSetter}
 *
 * @author nkjm
 *
 */

public class DefaultSqlParametersSetter implements SqlParametersSetter {

  private final List<ParameterSetter> setters;

  public DefaultSqlParametersSetter() {
    this.setters = Collections.emptyList();
  }

  public DefaultSqlParametersSetter(List<ParameterSetter> setters) {
    this.setters = List.copyOf(setters);
  }

  public DefaultSqlParametersSetter(ParameterSetter... setters) {
    this(Arrays.asList(setters));
  }


  @Override
  public void setParameters(SormOptions options, PreparedStatement stmt, Object... parameters)
      throws SQLException {
    if (parameters == null || parameters.length == 0) {
      return;
    }
    for (int i = 1; i <= parameters.length; i++) {
      setParameter(options, stmt, i, parameters[i - 1]);
    }
  }

  /**
   * Sets a parameter into the given prepared statement. i.e. Convert from java objects to SQL.
   *
   * @param options
   * @param stmt {@link java.sql.PreparedStatement} to have parameters set into
   * @param parameter parameters values
   *
   * @throws SQLException
   * @throws IOException
   *
   */
  private void setParameter(SormOptions options, PreparedStatement stmt, int parameterIndex,
      Object parameter) throws SQLException {
    if (parameter == null) {
      stmt.setNull(parameterIndex, java.sql.Types.NULL);
      return;
    }
    final Class<?> type = parameter.getClass();
    if (!setters.isEmpty()) {
      Optional<ParameterSetter> setter = setters.stream()
          .filter(_setter -> _setter.isApplicable(options, stmt, parameterIndex, type, parameter))
          .findFirst();
      if (setter.isPresent()) {
        setter.get().setParameter(options, stmt, parameterIndex, type, parameter);
        return;
      }
    }

    if (setIfStandardObject(type, stmt, parameterIndex, parameter)) {
    } else if (type.isEnum()) {
      stmt.setString(parameterIndex, parameter.toString());
    } else if (type.isArray()) {
      procArray(type, stmt, parameterIndex, parameter);
    } else if (List.class.isAssignableFrom(type)) {
      procList(stmt, parameterIndex, (List<?>) parameter);
    } else if (parameter instanceof Clob) {
      stmt.setClob(parameterIndex, (Clob) parameter);
    } else if (parameter instanceof Blob) {
      stmt.setBlob(parameterIndex, (Blob) parameter);
    } else {
      stmt.setObject(parameterIndex, parameter);
    }
  }

  private boolean setIfStandardObject(Class<?> type, PreparedStatement stmt, int parameterIndex,
      Object parameter) throws SQLException {
    switch (type.getName()) {
      case "java.lang.Boolean":
      case "boolean":
        stmt.setBoolean(parameterIndex, (Boolean) parameter);
        return true;
      case "java.lang.Byte":
      case "byte":
        stmt.setByte(parameterIndex, (Byte) parameter);
        return true;
      case "java.lang.Short":
      case "short":
        stmt.setShort(parameterIndex, (Short) parameter);
        return true;
      case "java.lang.Integer":
      case "int":
        stmt.setInt(parameterIndex, (Integer) parameter);
        return true;
      case "java.lang.Long":
      case "long":
        stmt.setLong(parameterIndex, (Long) parameter);
        return true;
      case "java.lang.Float":
      case "float":
        stmt.setFloat(parameterIndex, (Float) parameter);
        return true;
      case "java.lang.Double":
      case "double":
        stmt.setDouble(parameterIndex, (Double) parameter);
        return true;
      case "java.lang.Character":
      case "char":
        stmt.setString(parameterIndex, parameter == null ? null : "" + (Character) parameter);
        return true;
      case "java.lang.String":
        stmt.setString(parameterIndex, (String) parameter);
        return true;
      case "java.math.BigDecimal":
        stmt.setBigDecimal(parameterIndex, (BigDecimal) parameter);
        return true;
      case "java.sql.Date":
        stmt.setDate(parameterIndex, (java.sql.Date) parameter);
        return true;
      case "java.sql.Time":
        stmt.setTime(parameterIndex, (java.sql.Time) parameter);
        return true;
      case "java.sql.Timestamp":
        stmt.setTimestamp(parameterIndex, (java.sql.Timestamp) parameter);
        return true;
      case "java.time.LocalTime":
        stmt.setTime(parameterIndex,
            parameter == null ? null : java.sql.Time.valueOf((java.time.LocalTime) parameter));
        return true;
      case "java.time.LocalDate":
        stmt.setDate(parameterIndex,
            parameter == null ? null : java.sql.Date.valueOf((java.time.LocalDate) parameter));
        return true;
      case "java.time.LocalDateTime":
        stmt.setTimestamp(parameterIndex,
            parameter == null ? null : Timestamp.valueOf((java.time.LocalDateTime) parameter));
        return true;
      case "java.time.OffsetTime":
        stmt.setTime(parameterIndex, parameter == null ? null
            : java.sql.Time.valueOf(((java.time.OffsetTime) parameter).toLocalTime()));
        return true;
      case "java.time.OffsetDateTime":
        stmt.setTimestamp(parameterIndex, parameter == null ? null
            : Timestamp.valueOf(((java.time.OffsetDateTime) parameter).toLocalDateTime()));
        return true;
      case "java.time.ZonedDateTime":
        stmt.setTimestamp(parameterIndex, parameter == null ? null
            : Timestamp.valueOf(((java.time.ZonedDateTime) parameter).toLocalDateTime()));
        return true;
      case "java.util.Date":
        stmt.setTimestamp(parameterIndex,
            parameter == null ? null : new Timestamp(((java.util.Date) parameter).getTime()));
        return true;
      case "java.util.UUID":
        stmt.setString(parameterIndex,
            parameter == null ? null : ((java.util.UUID) parameter).toString());
        return true;
      default:
        return false;
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
  private void procArray(Class<?> type, PreparedStatement stmt, int parameterIndex,
      Object parameter) throws SQLException {
    final String compType = type.getComponentType().getName();

    switch (compType) {
      case "byte": {
        stmt.setBytes(parameterIndex, (byte[]) parameter);
        return;
      }
      case "java.lang.Boolean": {
        stmt.setArray(parameterIndex,
            stmt.getConnection().createArrayOf("boolean", (Object[]) parameter));
        return;
      }
      case "java.lang.Short": {
        stmt.setArray(parameterIndex,
            stmt.getConnection().createArrayOf("smallint", (Object[]) parameter));
        return;
      }
      case "java.lang.Integer": {
        stmt.setArray(parameterIndex,
            stmt.getConnection().createArrayOf("integer", (Object[]) parameter));
        return;
      }
      case "java.lang.Long": {
        stmt.setArray(parameterIndex,
            stmt.getConnection().createArrayOf("bigint", (Object[]) parameter));
        return;
      }
      case "java.math.BigDecimal": {
        stmt.setArray(parameterIndex,
            stmt.getConnection().createArrayOf("numeric", (Object[]) parameter));
        return;
      }
      case "java.lang.Float": {
        stmt.setArray(parameterIndex,
            stmt.getConnection().createArrayOf("real", (Object[]) parameter));
        return;
      }
      case "java.lang.Double": {
        stmt.setArray(parameterIndex,
            stmt.getConnection().createArrayOf("double precision", (Object[]) parameter));
        return;
      }
      case "java.lang.Character": {
        stmt.setArray(parameterIndex,
            stmt.getConnection().createArrayOf("character", (Object[]) parameter));
        return;
      }
      case "java.lang.String": {
        stmt.setArray(parameterIndex,
            stmt.getConnection().createArrayOf("varchar", (Object[]) parameter));
        return;
      }
      case "java.sql.Date": {
        stmt.setArray(parameterIndex,
            stmt.getConnection().createArrayOf("date", (Object[]) parameter));
        return;
      }
      case "java.sql.Time": {
        stmt.setArray(parameterIndex,
            stmt.getConnection().createArrayOf("time", (Object[]) parameter));
        return;
      }
      case "java.sql.Timestamp": {
        stmt.setArray(parameterIndex,
            stmt.getConnection().createArrayOf("timestamp", (Object[]) parameter));
        return;
      }
      default: {
        stmt.setObject(parameterIndex, parameter);
        return;
      }
    }
  }

  private void procList(PreparedStatement stmt, int parameterIndex, List<?> parameter)
      throws SQLException {
    if (parameter.size() == 0) {
      throw new SormException("Size of parameter which type is List should be at least one. ");
    }
    procArray(parameter.get(0).getClass(), stmt, parameterIndex, parameter.toArray());
  }



}
