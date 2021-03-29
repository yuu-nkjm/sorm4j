package org.nkjmlab.sorm4j.sql;

import java.io.Closeable;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.nkjmlab.sorm4j.RowMapper;


/**
 * A object wraps {@link ResultSet}.
 *
 * @author nkjm
 *
 * @param <T>
 */
public interface LazyResultSet<T> extends Iterable<T>, Closeable, AutoCloseable {

  /**
   * Closes this result set. After call this method, operations to this object is invalid.
   */
  @Override
  void close();

  /**
   * Returns the first row in the result set and close.
   *
   * @return
   */
  T first();

  /**
   * Returns the first row in the result set and close.
   *
   * @return
   */
  Map<String, Object> firstMap();

  /**
   * Iterates all the rows of the result set. The Iterator must be closed to release database
   * resources. The iterator is closed automatically if hasNext is false.
   */
  @Override
  Iterator<T> iterator();

  /**
   * Returns the object of one row from query and close. If the row is not unique, Exception is
   * thrown.
   *
   * @return
   */
  T one();

  /**
   * Returns the map of one row from query and close. If the row is not unique, Exception is
   *
   * @return
   */
  Map<String, Object> oneMap();

  /**
   * Streams all the rows of the result set. The stream must be closed to release database
   * resources.
   */
  Stream<T> stream();

  /**
   * Returns results in a List.
   *
   * @return
   */
  List<T> toList();

  /**
   * Returns the results converted by the given row mapper in a List.
   *
   * @return
   */
  List<T> toList(RowMapper<T> rowMapper);

  /**
   * Returns results in a List of {@code Map<String, Object>}.
   *
   * @return
   */
  List<Map<String, Object>> toMapList();

}
