package org.nkjmlab.sorm4j.extension;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * Default implementation of {@link SqlParameterSetter}
 *
 * @author nkjm
 *
 */

public class DefaultSqlParameterSetter implements SqlParameterSetter {
  // private static org.slf4j.Logger log =
  // org.nkjmlab.sorm4j.internal.util.LoggerFactory.getLogger();

  @Override
  public void setParameters(PreparedStatement stmt, Object... parameters) throws SQLException {
    if (parameters == null || parameters.length == 0) {
      return;
    }
    for (int i = 1; i <= parameters.length; i++) {
      final Object parameter = parameters[i - 1];
      if (parameter == null) {
        stmt.setNull(i, java.sql.Types.NULL);
      } else {
        setParameter(stmt, i, parameter);
      }
    }
  }

  /**
   * Sets a parameter into the given prepared statement. i.e. Convert from java objects to SQL.
   *
   * @param stmt {@link java.sql.PreparedStatement} to have parameters set into
   * @param parameter parameters values
   * @throws SQLException
   * @throws IOException
   *
   */
  protected void setParameter(PreparedStatement stmt, int column, Object parameter)
      throws SQLException {
    Class<?> type = parameter.getClass();
    if (type.isEnum()) {
      stmt.setString(column, parameter.toString());
    } else if (type.isArray()) {
      procArray(type, stmt, column, parameter);
    } else {
      procObject(type, stmt, column, parameter);
    }
  }

  /**
   * Treats object.
   *
   * @param type
   * @param stmt
   * @param column
   * @param parameter
   * @throws SQLException
   */
  protected void procObject(Class<?> type, PreparedStatement stmt, int column, Object parameter)
      throws SQLException {
    if (parameter instanceof Clob) {
      stmt.setClob(column, (Clob) parameter);
    } else if (parameter instanceof Blob) {
      stmt.setBlob(column, (Blob) parameter);
    } else if (parameter instanceof java.io.Reader) {
      final java.io.Reader reader = (java.io.Reader) parameter;
      stmt.setCharacterStream(column, reader, getSize(reader));
    } else if (parameter instanceof java.io.InputStream) {
      final java.io.InputStream inputStream = (java.io.InputStream) parameter;
      stmt.setBinaryStream(column, inputStream, getSize(inputStream));
    } else {
      final String typeName = type.getName();
      switch (typeName) {
        case "java.lang.Boolean":
        case "boolean":
          stmt.setBoolean(column, (Boolean) parameter);
          return;
        case "java.lang.Byte":
        case "byte":
          stmt.setByte(column, (Byte) parameter);
          return;
        case "java.lang.Short":
        case "short":
          stmt.setShort(column, (Short) parameter);
          return;
        case "java.lang.Integer":
        case "int":
          stmt.setInt(column, (Integer) parameter);
          return;
        case "java.lang.Long":
        case "long":
          stmt.setLong(column, (Long) parameter);
          return;
        case "java.lang.Float":
        case "float":
          stmt.setFloat(column, (Float) parameter);
          return;
        case "java.lang.Double":
        case "double":
          stmt.setDouble(column, (Double) parameter);
          return;
        case "java.lang.Character":
        case "char":
          stmt.setString(column, parameter == null ? null : "" + (Character) parameter);
          return;
        case "java.lang.String":
          stmt.setString(column, (String) parameter);
          return;
        case "java.math.BigDecimal":
          stmt.setBigDecimal(column, (BigDecimal) parameter);
          return;
        case "java.sql.Date":
          stmt.setDate(column,
              parameter == null ? null : java.sql.Date.valueOf(parameter.toString()));
          return;
        case "java.sql.Time":
          stmt.setTime(column, parameter == null ? null : Time.valueOf(parameter.toString()));
          return;
        case "java.sql.Timestamp":
          stmt.setTimestamp(column, (java.sql.Timestamp) parameter);
          return;
        case "java.time.LocalTime":
          stmt.setTime(column,
              parameter == null ? null : java.sql.Time.valueOf((java.time.LocalTime) parameter));
          return;
        case "java.time.LocalDate":
          stmt.setDate(column,
              parameter == null ? null : java.sql.Date.valueOf((java.time.LocalDate) parameter));
          return;
        case "java.time.LocalDateTime":
          stmt.setTimestamp(column,
              parameter == null ? null : Timestamp.valueOf((java.time.LocalDateTime) parameter));
          return;
        case "java.util.Date":
          stmt.setTimestamp(column,
              parameter == null ? null : new Timestamp(((java.util.Date) parameter).getTime()));
          return;
        default:
          stmt.setObject(column, parameter);
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
   * @param type
   * @param stmt
   * @param column
   * @param parameter
   * @throws SQLException
   */
  protected void procArray(Class<?> type, PreparedStatement stmt, int column, Object parameter)
      throws SQLException {
    final String name = type.getComponentType().getName();
    switch (name) {
      case "char": {
        stmt.setString(column, parameter == null ? null : String.valueOf((char[]) parameter));
        return;
      }
      case "java.lang.Character": {
        final Character[] src = (Character[]) parameter;
        final char[] dst = new char[src.length];
        for (int j = 0; j < src.length; j++) {
          dst[j] = src[j];
        }
        stmt.setString(column, String.valueOf(dst));
        return;
      }
      case "byte": {
        stmt.setBytes(column, (byte[]) parameter);
        return;
      }
      case "java.lang.Byte": {
        final Byte[] src = (Byte[]) parameter;
        final byte[] dst = new byte[src.length];
        for (int j = 0; j < src.length; j++) {
          dst[j] = src[j];
        }
        stmt.setBytes(column, dst);
        return;
      }
      default: {
        stmt.setObject(column, parameter);
        return;
      }
    }

  }



}
