package org.nkjmlab.sorm4j;

import java.sql.ResultSet;

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
}
