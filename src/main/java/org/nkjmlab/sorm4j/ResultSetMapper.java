package org.nkjmlab.sorm4j;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mapping {@link ResultSet} to object.
 *
 * @author nkjm
 *
 */
public interface ResultSetMapper {

  /**
   * Maps the current row in the given resultSet to an object.
   *
   * @param <T>
   * @param objectClass
   * @param resultSet
   * @return
   */
  <T> T mapRow(Class<T> objectClass, ResultSet resultSet);

  /**
   * Maps the current row in the given resultSet to an Map object.
   *
   * @param resultSet
   * @return
   */
  Map<String, Object> mapRow(ResultSet resultSet);

  /**
   * Maps the all rows in the given resultSet to an object list.
   *
   * @param <T>
   * @param objectClass
   * @param resultSet
   * @return
   */
  <T> List<T> mapRows(Class<T> objectClass, ResultSet resultSet);

  /**
   * Maps the all rows in the given resultSet to a map list.
   *
   * @param resultSet
   * @return
   */
  List<Map<String, Object>> mapRows(ResultSet resultSet);

  /**
   * Converts the given rowMapper to function mapping rows to object list.
   *
   * @param <T>
   * @param rowMapper
   * @return
   */
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
