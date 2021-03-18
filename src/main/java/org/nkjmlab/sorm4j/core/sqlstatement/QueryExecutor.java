package org.nkjmlab.sorm4j.core.sqlstatement;

import java.util.List;
import org.nkjmlab.sorm4j.TypedOrmReader;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.SqlStatement;

public interface QueryExecutor<T> {
  /**
   * see {@link TypedOrmReader#readOne(SqlStatement)}
   *
   * @return
   */
  T readOne(SqlStatement sqlStatement);

  /**
   * see {@link TypedOrmReader#readFirst(SqlStatement)}
   *
   * @return
   */
  T readFirst(SqlStatement sqlStatement);

  /**
   * see {@link TypedOrmReader#readLazy(SqlStatement)}
   *
   * @return
   */
  LazyResultSet<T> readLazy(SqlStatement sqlStatement);

  /**
   * see {@link TypedOrmReader#readList(SqlStatement)}
   *
   * @return
   */
  List<T> readList(SqlStatement sqlStatement);
}
