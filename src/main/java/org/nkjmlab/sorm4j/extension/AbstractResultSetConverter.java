package org.nkjmlab.sorm4j.extension;

import static org.nkjmlab.sorm4j.internal.util.StringUtils.*;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractResultSetConverter implements ResultSetConverter {

  private static final Set<Class<?>> nativeSqlTypes = Set.of(boolean.class, Boolean.class,
      byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class,
      Long.class, float.class, Float.class, double.class, Double.class, char.class, Character.class,
      byte[].class, Byte[].class, char[].class, Character[].class, String.class, BigDecimal.class,
      java.util.Date.class, java.sql.Date.class, java.sql.Time.class, java.sql.Timestamp.class,
      java.io.InputStream.class, java.io.Reader.class, java.sql.Clob.class, java.sql.Blob.class,
      Object.class);

  /**
   * Returns the given type is enable to convert native object.
   *
   * Following classes are regarded as native class: boolean.class, Boolean.class, byte.class,
   * Byte.class, short.class, Short.class, int.class, Integer.class, long.class, Long.class,
   * float.class, Float.class, double.class, Double.class, char.class, Character.class,
   * byte[].class, Byte[].class, char[].class, Character[].class, String.class, BigDecimal.class,
   * java.util.Date.class, java.sql.Date.class, java.sql.Time.class, java.sql.Timestamp.class,
   * java.io.InputStream.class, java.io.Reader.class, java.sql.Clob.class, java.sql.Blob.class,
   * Object.class
   */
  @Override
  public boolean isEnableToConvertNativeObject(Class<?> objectClass) {
    return nativeSqlTypes.contains(objectClass);
  }

  @Override
  public final Map<String, Object> toSingleMap(ResultSet resultSet, List<String> columns,
      List<Integer> columnTypes) throws SQLException {
    final int cSize = columns.size();
    final Map<String, Object> ret = new LinkedHashMap<>(cSize);
    for (int i = 1; i <= cSize; i++) {
      ret.put(toLowerCase(columns.get(i - 1)),
          getColumnValueBySqlType(resultSet, i, columnTypes.get(i - 1)));
    }
    return ret;
  }

  @Override
  @SuppressWarnings("unchecked")
  public final <T> T toSingleNativeObject(ResultSet resultSet, int sqlType, Class<T> objectClass)
      throws SQLException {
    return (T) getColumnValue(resultSet, 1, sqlType, objectClass);
  }

  abstract protected Object getColumnValueBySqlType(ResultSet resultSet, int column, int sqlType)
      throws SQLException;

}
