package org.nkjmlab.sorm4j;

import java.util.List;

public interface TypeOrmReader<T> {

  List<T> readAll();

  ReadResultSet<T> readAllLazy();

  T readByPrimaryKey(Object... primaryKeyValues);

  T readFirst(String sql, Object... parameters);

  ReadResultSet<T> readLazy(String sql, Object... parameters);

  List<T> readList(String sql, Object... parameters);


}
