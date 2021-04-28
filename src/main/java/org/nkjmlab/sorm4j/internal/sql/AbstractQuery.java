package org.nkjmlab.sorm4j.internal.sql;

import java.util.List;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.ParameterizedSqlParser;
import org.nkjmlab.sorm4j.sql.helper.Query;

public abstract class AbstractQuery<T> implements Query<T>, ParameterizedSqlParser {
  protected final QueryExecutor<T> executor;


  public AbstractQuery(QueryExecutor<T> executor) {
    this.executor = executor;
  }

  @Override
  public T readOne() {
    return executor.readOne(parse());
  }

  @Override
  public T readFirst() {
    return executor.readFirst(parse());
  }

  @Override
  public LazyResultSet<T> readLazy() {
    return executor.readLazy(parse());
  }

  @Override
  public List<T> readList() {
    return executor.readList(parse());
  }

}
