package org.nkjmlab.sorm4j.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.config.ResultSetValueGetter;


final class ResultSetConverter {

  private final ResultSetValueGetter resultSetValueGetter;

  ResultSetConverter(ResultSetValueGetter resultSetValueGetter) {
    this.resultSetValueGetter = resultSetValueGetter;
  }

  Object getValueByClass(ResultSet resultSet, int i, Class<?> classType) throws SQLException {
    return resultSetValueGetter.getValueBySetterType(resultSet, i, classType);
  }

  final List<Object> toObjectsByClasses(ResultSet resultSet, List<Class<?>> setterArgTypes)
      throws SQLException {
    final List<Object> values = new ArrayList<>(setterArgTypes.size());
    for (int i = 1; i <= setterArgTypes.size(); i++) {
      final Class<?> type = setterArgTypes.get(i - 1);
      values.add(resultSetValueGetter.getValueBySetterType(resultSet, i, type));
    }
    return values;
  }

  Map<String, Object> toSingleMap(final ResultSet resultSet, List<String> columns,
      List<Integer> columnTypes) throws SQLException {
    final Map<String, Object> ret = new LinkedHashMap<>();
    for (int i = 1; i <= columns.size(); i++) {
      int type = columnTypes.get(i - 1);
      Object value = resultSetValueGetter.getValueBySqlType(resultSet, i, type);
      ret.put(columns.get(i - 1), value);
    }
    return ret;
  }

  final <T> T toSingleNativeObject(final ResultSet resultSet, final Class<T> objectClass)
      throws SQLException {
    // Don't user type from metadata (metaData.getColumnType(1)) because object class of container
    // is prior.
    Object value = resultSetValueGetter.getValueBySetterType(resultSet, 1, objectClass);
    @SuppressWarnings("unchecked")
    T valueT = (T) value;
    return valueT;
  }


}
