package org.nkjmlab.sorm4j.internal.sql;

import java.util.List;
import org.nkjmlab.sorm4j.TypedOrmReader;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

public class QueryTypedOrmExecutor<T> implements QueryExecutor<T> {

  protected final TypedOrmReader<T> conn;

  public QueryTypedOrmExecutor(TypedOrmReader<T> conn) {
    this.conn = conn;
  }

  @Override
  public T readOne(ParameterizedSql parameterizedSql) {
    return conn.readOne(parameterizedSql);
  }

  @Override
  public T readFirst(ParameterizedSql parameterizedSql) {
    return conn.readFirst(parameterizedSql);
  }

  @Override
  public LazyResultSet<T> readLazy(ParameterizedSql parameterizedSql) {
    return conn.readLazy(parameterizedSql);
  }

  @Override
  public List<T> readList(ParameterizedSql parameterizedSql) {
    return conn.readList(parameterizedSql);
  }


}
