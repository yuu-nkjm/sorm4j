package org.nkjmlab.sorm4j.extension;

import static org.nkjmlab.sorm4j.internal.util.StringUtils.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractResultSetConverter implements ResultSetConverter {

  @Override
  public final Map<String, Object> toSingleMap(SormOptions options, ResultSet resultSet,
      List<String> columns, List<Integer> columnTypes) throws SQLException {
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
  public final <T> T toSingleNativeObject(SormOptions options, ResultSet resultSet, int sqlType,
      Class<T> objectClass) throws SQLException {
    return (T) convertColumnValueTo(options, resultSet, 1, sqlType, objectClass);
  }

  abstract protected Object getColumnValueBySqlType(ResultSet resultSet, int column, int sqlType)
      throws SQLException;

}
