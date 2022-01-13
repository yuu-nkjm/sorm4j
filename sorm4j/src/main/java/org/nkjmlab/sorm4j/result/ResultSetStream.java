package org.nkjmlab.sorm4j.result;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.nkjmlab.sorm4j.mapping.RowMapper;


/**
 * A object wraps {@link ResultSet}.
 *
 * @author nkjm
 *
 * @param <T>
 */
public interface ResultSetStream<T> extends Iterable<T>, AutoCloseable {

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


}
