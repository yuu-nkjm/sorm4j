package org.nkjmlab.sorm4j.basic;

import java.sql.ResultSet;

/**
 *
 * Maps a row in a {@link ResultSet} to a object.
 *
 * @author nkjm
 *
 * @param <T>
 * @see <a href="https://scrapbox.io/sorm4j/RowMapper">RowMapper - Sorm4j</a>
 * @see {@link ResultSetTraverser}
 */
@FunctionalInterface
public interface RowMapper<T> {

  /**
   * Maps a row in {@link RowMapper} to a object.
   *
   * @param resultSet
   * @param rowNum Starts 1. e.g. 1, 2, ...
   * @return
   */
  T mapRow(ResultSet resultSet, int rowNum) throws Exception;
}
