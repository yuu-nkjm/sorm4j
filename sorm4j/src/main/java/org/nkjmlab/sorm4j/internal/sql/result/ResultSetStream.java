package org.nkjmlab.sorm4j.internal.sql.result;

import java.util.stream.Stream;
import org.nkjmlab.sorm4j.annotation.Experimental;


/**
 * @param <T>
 */
@Experimental
public interface ResultSetStream<T> extends AutoCloseable {

  /**
   * Closes this object including the ResultSet. The connection generating the ResultSet will be not
   * closed. After call this method, operations to this object is invalid.
   */
  @Override
  void close();


  /**
   * Streams all the rows of the result set. The {@link ResultSetStream} instance will be closed
   * automatically when the {@link Stream} instance is closed.
   *
   * This method is expected that it is used with try-with-resources block.
   *
   */
  Stream<T> stream();


}
