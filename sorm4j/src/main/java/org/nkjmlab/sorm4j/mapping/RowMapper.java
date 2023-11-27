package org.nkjmlab.sorm4j.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps a row in a {@link ResultSet} to a object.
 *
 * @author nkjm
 * @param <T>
 * @see {@link ResultSetTraverser}
 */
@FunctionalInterface
public interface RowMapper<T> {

  /**
   * Maps a row in {@link RowMapper} to a object.
   *
   * @param resultSet the ResultSet to map (pre-initialized for the current row)
   * @param rowNum the number of the current row. it starts 1 ( e.g. 1, 2, ...).
   * @return
   */
  // rowNum is added because it is ambiguous when it used by Orm#executeQuery method.
  T mapRow(ResultSet resultSet, int rowNum) throws SQLException;
}
