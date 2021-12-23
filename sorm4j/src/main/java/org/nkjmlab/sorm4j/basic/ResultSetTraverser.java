package org.nkjmlab.sorm4j.basic;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * Traverses {@link ResultSet} and maps to T.
 *
 * @author nkjm
 *
 * @param <T>
 */
@Experimental
@FunctionalInterface
public interface ResultSetTraverser<T> {

  /**
   * Traverses {@link ResultSet} and maps to T.
   *
   * @param resultSet
   * @return
   * @throws Exception
   */
  T traverseAndMap(ResultSet resultSet) throws Exception;

  /**
   * Converts the given {@link RowMapper} to the {@link ResultSetTraverser}.
   *
   * @param <T>
   * @param rowMapper
   * @return
   */
  @Experimental
  static <T> ResultSetTraverser<List<T>> from(RowMapper<T> rowMapper) {
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
