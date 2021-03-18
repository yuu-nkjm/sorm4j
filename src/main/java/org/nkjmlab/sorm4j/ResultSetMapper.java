package org.nkjmlab.sorm4j;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.sql.RowMapper;

public interface ResultSetMapper {

  <T> T mapRow(Class<T> objectClass, ResultSet resultSet);

  Map<String, Object> mapRow(ResultSet resultSet);

  <T> List<T> mapRows(Class<T> objectClass, ResultSet resultSet);

  List<Map<String, Object>> mapRows(ResultSet resultSet);

  static <T> FunctionHandler<ResultSet, List<T>> convertToRowsMapper(RowMapper<T> rowMapper) {
    return resultSet -> {
      final List<T> ret = new ArrayList<>();
      int rowNum = 0;
      while (resultSet.next()) {
        rowNum++;
        ret.add(rowMapper.mapRow(resultSet, rowNum));
      }
      return ret;
    };
  }

}
