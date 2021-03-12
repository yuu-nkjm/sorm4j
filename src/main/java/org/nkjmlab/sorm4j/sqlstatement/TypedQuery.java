package org.nkjmlab.sorm4j.sqlstatement;

import java.util.List;
import org.nkjmlab.sorm4j.TypedOrmReader;
import org.nkjmlab.sorm4j.result.LazyResultSet;

/**
 * A query for reading and mapping a relation to object.
 *
 * @author nkjm
 *
 * @param <T>
 */

public interface TypedQuery<T> {

  /**
   * see {@link TypedOrmReader#readOne(SqlStatement)}
   *
   * @return
   */
  T readOne();

  /**
   * see {@link TypedOrmReader#readFirst(SqlStatement)}
   *
   * @return
   */
  T readFirst();

  /**
   * see {@link TypedOrmReader#readLazy(SqlStatement)}
   *
   * @return
   */
  LazyResultSet<T> readLazy();

  /**
   * see {@link TypedOrmReader#readList(SqlStatement)}
   *
   * @return
   */
  List<T> readList();

}
