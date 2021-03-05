package org.nkjmlab.sorm4j;

import java.util.List;
import org.nkjmlab.sorm4j.result.LazyResultSet;
import org.nkjmlab.sorm4j.sqlstatement.SqlStatement;

/**
 * The typed interface of reading functions of object-relation mapping.
 *
 * @author nkjm
 *
 */

public interface TypedOrmReader<T> {

  List<T> readAll();

  LazyResultSet<T> readAllLazy();

  T readByPrimaryKey(Object... primaryKeyValues);

  T readOne(String sql, Object... parameters);

  T readFirst(String sql, Object... parameters);

  LazyResultSet<T> readLazy(String sql, Object... parameters);

  List<T> readList(String sql, Object... parameters);

  T readOne(SqlStatement sql);

  T readFirst(SqlStatement sql);

  LazyResultSet<T> readLazy(SqlStatement sql);

  List<T> readList(SqlStatement sql);

}
