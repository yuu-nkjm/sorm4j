package org.nkjmlab.sorm4j.sql.helper;

import java.util.List;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.sql.LazyResultSet;

/**
 * A query for reading and mapping a relation to object.
 *
 * @author nkjm
 *
 * @param <T>
 */
@Experimental
public interface Query<T> {

  /**
   * Reads a list of objects from the database by mapping the results of the parameterized SQL query
   * into instances of the given object class. Only the columns returned from the SQL query will be
   * set into the object instance.
   *
   * @return
   */
  T readOne();

  /**
   * Reads an object from the database.
   *
   * @return
   */
  T readFirst();

  /**
   * Returns an {@link LazyResultSet}. It is able to convert to Stream, List, and so on.
   *
   * @return
   */
  LazyResultSet<T> readLazy();

  /**
   * Reads a list of objects from the database by mapping the results of the parameterized SQL query
   * into instances of the given object class. Only the columns returned from the SQL query will be
   * set into the object instance.
   *
   * @return
   */
  List<T> readList();

}
