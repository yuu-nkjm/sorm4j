package org.nkjmlab.sorm4j.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.config.ResultSetValueGetter;


public class ResultSetConverter {

  private final ResultSetValueGetter resultSetValueGetter;

  public ResultSetConverter(ResultSetValueGetter resultSetValueGetter) {
    this.resultSetValueGetter = resultSetValueGetter;
  }

  public Map<String, Object> toSingleMap(final ResultSet resultSet, List<String> columns,
      List<Integer> columnTypes) {
    try {
      final Map<String, Object> ret = new LinkedHashMap<>();
      for (int i = 1; i <= columns.size(); i++) {
        int type = columnTypes.get(i - 1);
        Object value = resultSetValueGetter.getValueBySqlType(resultSet, i, type);
        ret.put(columns.get(i - 1), value);
      }
      return ret;
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  public final <T> T toSingleNativeObject(final ResultSet resultSet, final Class<T> objectClass) {
    try {
      // Don't user type from metadata (metaData.getColumnType(1)) because object class of container
      // is prior.
      Object value = resultSetValueGetter.getValueByClass(resultSet, 1, objectClass);
      @SuppressWarnings("unchecked")
      T valueT = (T) value;
      return valueT;
    } catch (SQLException e) {
      throw new OrmException("Fail to get value as [" + objectClass.getSimpleName() + "]", e);
    }
  }

  public final List<Object> toObjectsByClasses(ResultSet resultSet,
      List<Class<?>> setterParamTypes) {
    try {
      final List<Object> values = new ArrayList<>(setterParamTypes.size());
      for (int i = 1; i <= setterParamTypes.size(); i++) {
        final Class<?> type = setterParamTypes.get(i - 1);
        values.add(resultSetValueGetter.getValueByClass(resultSet, i, type));
      }
      return values;
    } catch (IllegalArgumentException | SecurityException | SQLException e) {
      throw new OrmException(e);
    }
  }

  public Object getValueByClass(ResultSet resultSet, int i, Class<?> classType) throws SQLException {
    return resultSetValueGetter.getValueByClass(resultSet, i, classType);
  }


}
