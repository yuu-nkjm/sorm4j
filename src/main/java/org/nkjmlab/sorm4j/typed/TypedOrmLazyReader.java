package org.nkjmlab.sorm4j.typed;

import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.result.LazyResultSet;

/**
 * The typed interface of reading functions of object-relation mapping.
 *
 * @author nkjm
 *
 */

public interface TypedOrmLazyReader<T> {

  /**
   * Returns {@link LazyResultSet} represents all rows from the table indicated by object class.
   *
   * @return
   */
  LazyResultSet<T> readAllLazy();

  /**
   * Returns an {@link LazyResultSet}. It is able to convert to Stream, List, and so on.
   *
   * @param sql
   * @return
   */
  LazyResultSet<T> readLazy(ParameterizedSql sql);

  /**
   * Returns an {@link LazyResultSet}. It is able to convert to Stream, List, and so on.
   *
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   * @return
   */
  LazyResultSet<T> readLazy(String sql, Object... parameters);

}
