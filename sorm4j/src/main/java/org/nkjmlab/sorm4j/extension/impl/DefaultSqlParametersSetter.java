package org.nkjmlab.sorm4j.extension.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.extension.ParameterSetter;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.internal.util.Try;

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
    this.setters = new ArrayList<>(setters);
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
      final Object parameter = parameters[i - 1];
      if (parameter == null) {
        stmt.setNull(i, java.sql.Types.NULL);
      } else {
        setParameter(options, stmt, i, parameter);
      }
    }
  }

  /**
   * Sets a parameter into the given prepared statement. i.e. Convert from java objects to SQL.
   *
   * @param options TODO
   * @param stmt {@link java.sql.PreparedStatement} to have parameters set into
   * @param parameter parameters values
   *
   * @throws SQLException
   * @throws IOException
   *
   */
  protected void setParameter(SormOptions options, PreparedStatement stmt, int parameterIndex,
      Object parameter) throws SQLException {
    Class<?> type = parameter.getClass();
    if (setters.size() != 0) {
      Optional<ParameterSetter> setter = setters.stream()
          .filter(co -> co.isApplicable(options, stmt, parameterIndex, type, parameter))
          .findFirst();
      if (setter.isPresent()) {
        setter.get().setParameter(options, stmt, parameterIndex, type, parameter);
        return;
      }
    }

    if (type.isEnum()) {
      stmt.setString(parameterIndex, parameter.toString());
    } else if (type.isArray()) {
      final String name = type.getComponentType().getName();
      procArray(name, stmt, parameterIndex, parameter);
    } else if (List.class.isAssignableFrom(type)) {
      procList(stmt, parameterIndex, (List<?>) parameter);
    } else {
      procObject(type, stmt, parameterIndex, parameter);
    }
  }



  /**
   * Treats object.
   *
   * @param type
   * @param stmt
   * @param parameterIndex
   * @param parameter
   * @throws SQLException
   */
  protected void procObject(Class<?> type, PreparedStatement stmt, int parameterIndex,
      Object parameter) throws SQLException {
    if (parameter instanceof Clob) {
      stmt.setClob(parameterIndex, (Clob) parameter);
    } else if (parameter instanceof Blob) {
      stmt.setBlob(parameterIndex, (Blob) parameter);
    } else if (parameter instanceof java.io.Reader) {
      final java.io.Reader reader = (java.io.Reader) parameter;
      stmt.setCharacterStream(parameterIndex, reader, getSize(reader));
    } else if (parameter instanceof java.io.InputStream) {
      final java.io.InputStream inputStream = (java.io.InputStream) parameter;
      stmt.setBinaryStream(parameterIndex, inputStream, getSize(inputStream));
    } else {
      final String typeName = type.getName();
      switch (typeName) {
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
          stmt.setDate(parameterIndex,
              parameter == null ? null : java.sql.Date.valueOf(parameter.toString()));
          return;
        case "java.sql.Time":
          stmt.setTime(parameterIndex,
              parameter == null ? null : Time.valueOf(parameter.toString()));
          return;
        case "java.sql.Timestamp":
          stmt.setTimestamp(parameterIndex, (java.sql.Timestamp) parameter);
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
        case "java.util.Date":
          stmt.setTimestamp(parameterIndex,
              parameter == null ? null : new Timestamp(((java.util.Date) parameter).getTime()));
          return;
        case "java.time.OffsetTime":
          stmt.setTime(parameterIndex, parameter == null ? null
              : java.sql.Time.valueOf(((java.time.OffsetTime) parameter).toLocalTime()));
          return;
        case "java.time.OffsetDateTime":
          stmt.setTimestamp(parameterIndex, parameter == null ? null
              : Timestamp.valueOf(((java.time.OffsetDateTime) parameter).toLocalDateTime()));
          return;
        case "java.net.URL":
          stmt.setString(parameterIndex, parameter == null ? null : ((URL) parameter).toString());
          return;
        default:
          stmt.setObject(parameterIndex, parameter);
          return;
      }
    }
  }



  private static final int getSize(InputStream inputStream) {
    try {
      int size = 0;
      inputStream.reset();
      while (inputStream.read() != -1) {
        size++;
      }
      inputStream.reset();
      return size;
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }

  private static final int getSize(Reader reader) {
    try {
      int size = 0;
      reader.reset();
      while (reader.read() != -1) {
        size++;
      }
      reader.reset();
      return size;
    } catch (IOException e) {
      throw Try.rethrow(e);
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
  protected void procArray(String className, PreparedStatement stmt, int parameterIndex,
      Object parameter) throws SQLException {
    switch (className) {
      case "char": {
        stmt.setString(parameterIndex,
            parameter == null ? null : String.valueOf((char[]) parameter));
        return;
      }
      case "java.lang.Character": {
        final Character[] src = (Character[]) parameter;
        final char[] dst = new char[src.length];
        for (int j = 0; j < src.length; j++) {
          dst[j] = src[j];
        }
        stmt.setString(parameterIndex, String.valueOf(dst));
        return;
      }
      case "byte": {
        stmt.setBytes(parameterIndex, (byte[]) parameter);
        return;
      }
      case "java.lang.Byte": {
        final Byte[] src = (Byte[]) parameter;
        final byte[] dst = new byte[src.length];
        for (int j = 0; j < src.length; j++) {
          dst[j] = src[j];
        }
        stmt.setBytes(parameterIndex, dst);
        return;
      }
      case "java.lang.String": {
        stmt.setArray(parameterIndex,
            stmt.getConnection().createArrayOf("varchar", (Object[]) parameter));
        return;
      }
      case "java.lang.Integer": {
        stmt.setArray(parameterIndex,
            stmt.getConnection().createArrayOf("int", (Object[]) parameter));
        return;
      }
      case "java.lang.Long": {
        stmt.setArray(parameterIndex,
            stmt.getConnection().createArrayOf("bigint", (Object[]) parameter));
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
    procArray(parameter.get(0).getClass().getName(), stmt, parameterIndex, parameter.toArray());
  }



}
