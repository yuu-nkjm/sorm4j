package org.nkjmlab.sorm4j.sqlstatement;

import java.util.List;
import org.nkjmlab.sorm4j.TypedOrmReader;
import org.nkjmlab.sorm4j.result.LazyResultSet;

abstract class AbstQuery<T> implements Query<T> {

  protected final TypedOrmReader<T> conn;

  protected abstract SqlStatement toSqlStatement();

  public AbstQuery(TypedOrmReader<T> conn) {
    this.conn = conn;
  }

  @Override
  public T readOne() {
    return conn.readOne(toSqlStatement());
  }

  @Override
  public T readFirst() {
    return conn.readFirst(toSqlStatement());
  }

  @Override
  public LazyResultSet<T> readLazy() {
    return conn.readLazy(toSqlStatement());
  }

  @Override
  public List<T> readList() {
    return conn.readList(toSqlStatement());
  }


}
