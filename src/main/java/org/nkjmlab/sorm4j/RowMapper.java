package org.nkjmlab.sorm4j;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Maps a row in resultSet to a object.
 *
 * @author nkjm
 *
 * @param <T>
 */
@FunctionalInterface
public interface RowMapper<T> {

  /**
   * Maps a row in resultSet to a object.
   *
   * @param resultSet
   * @param rowNum Starts 1. e.g. 1, 2, ...
   * @return
   */
  T mapRow(ResultSet resultSet, int rowNum);

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
