package org.nkjmlab.sorm4j.core.sqlstatement;

import java.util.List;
import org.nkjmlab.sorm4j.TypedOrmReader;
import org.nkjmlab.sorm4j.result.LazyResultSet;
import org.nkjmlab.sorm4j.sqlstatement.TypedQuery;
import org.nkjmlab.sorm4j.sqlstatement.SqlStatement;

abstract class AbstTypedQuery<T> implements TypedQuery<T> {

  protected final TypedOrmReader<T> conn;

  protected abstract SqlStatement toSqlStatement();

  public AbstTypedQuery(TypedOrmReader<T> conn) {
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
