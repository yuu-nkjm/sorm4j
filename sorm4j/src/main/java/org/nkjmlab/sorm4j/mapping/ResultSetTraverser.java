package org.nkjmlab.sorm4j.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Traverses {@link ResultSet} and maps to T.
 *
 * @author nkjm
 * @param <T>
 */
@FunctionalInterface
public interface ResultSetTraverser<T> {

  /**
   * Traverses {@link ResultSet} and maps to T.
   *
   * @param resultSet
   * @return
   * @throws Exception
   */
  T traverseAndMap(ResultSet resultSet) throws SQLException;

  /**
   * Converts the given {@link RowMapper} to the {@link ResultSetTraverser}.
   *
   * @param <T>
   * @param rowMapper
   * @return
   */
  static <T> ResultSetTraverser<List<T>> of(RowMapper<T> rowMapper) {
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
