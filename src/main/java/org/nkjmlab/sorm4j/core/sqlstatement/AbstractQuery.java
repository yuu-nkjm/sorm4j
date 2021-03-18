package org.nkjmlab.sorm4j.core.sqlstatement;

import java.util.List;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.Query;
import org.nkjmlab.sorm4j.sql.SqlStatement;

public abstract class AbstractQuery<T> implements Query<T> {
  protected final QueryExecutor<T> executor;

  abstract SqlStatement toSqlStatement();

  public AbstractQuery(QueryExecutor<T> executor) {
    this.executor = executor;
  }

  @Override
  public T readOne() {
    return executor.readOne(toSqlStatement());
  }

  @Override
  public T readFirst() {
    return executor.readFirst(toSqlStatement());
  }

  @Override
  public LazyResultSet<T> readLazy() {
    return executor.readLazy(toSqlStatement());
  }

  @Override
  public List<T> readList() {
    return executor.readList(toSqlStatement());
  }

}
