package org.nkjmlab.sorm4j.internal.sql;

import java.util.List;
import org.nkjmlab.sorm4j.TypedOrmReader;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

public interface QueryExecutor<T> {
  /**
   * see {@link TypedOrmReader#readOne(ParameterizedSql)}
   *
   * @return
   */
  T readOne(ParameterizedSql parameterizedSql);

  /**
   * see {@link TypedOrmReader#readFirst(ParameterizedSql)}
   *
   * @return
   */
  T readFirst(ParameterizedSql parameterizedSql);

  /**
   * see {@link TypedOrmReader#readLazy(ParameterizedSql)}
   *
   * @return
   */
  LazyResultSet<T> readLazy(ParameterizedSql parameterizedSql);

  /**
   * see {@link TypedOrmReader#readList(ParameterizedSql)}
   *
   * @return
   */
  List<T> readList(ParameterizedSql parameterizedSql);
}
