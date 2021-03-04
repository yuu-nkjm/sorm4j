package org.nkjmlab.sorm4j.result;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


/**
 * A object wraps {@link ResultSet}.
 *
 * @author nkjm
 *
 * @param <T>
 */
public interface LazyResultSet<T> {

  /**
   * Returns the row in the result set and close. If the row is not unique, Exception is thrown.
   *
   * @return
   */
  T one();

  /**
   * Returns the first row in the result set and close.
   *
   * @return
   */
  T first();

  /**
   * Returns results in a List.
   *
   * @return
   */
  List<T> toList();

  Map<String, Object> oneMap();

  /**
   * Returns the first row in the result set and close.
   *
   * @return
   */
  Map<String, Object> firstMap();

  /**
   * Returns results in a List of {@code Map<String, Object>}.
   *
   * @return
   */
  List<Map<String, Object>> toMapList();

  /**
   * Iterates all the rows of the result set. The Iterator must be closed to release database
   * resources. The iterator is closed automatically if hasNext is false.
   */
  Iterator<T> iterator();

  /**
   * Streams all the rows of the result set. The stream must be closed to release database
   * resources.
   */
  Stream<T> stream();

  void close();

}
