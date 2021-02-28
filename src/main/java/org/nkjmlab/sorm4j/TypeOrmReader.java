package org.nkjmlab.sorm4j;

import java.util.List;
import org.nkjmlab.sorm4j.helper.SqlStatement;

public interface TypeOrmReader<T> {

  List<T> readAll();

  ReadResultSet<T> readAllLazy();

  T readByPrimaryKey(Object... primaryKeyValues);

  T readFirst(String sql, Object... parameters);

  ReadResultSet<T> readLazy(String sql, Object... parameters);

  List<T> readList(String sql, Object... parameters);

  T readFirst(SqlStatement sql);

  ReadResultSet<T> readLazy(SqlStatement sql);

  List<T> readList(SqlStatement sql);

}
