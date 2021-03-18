package org.nkjmlab.sorm4j.core.sqlstatement;

import java.util.List;
import org.nkjmlab.sorm4j.TypedOrmReader;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.SqlStatement;

public class TypedQueryExecutor<T> implements QueryExecutor<T> {

  protected final TypedOrmReader<T> conn;

  public TypedQueryExecutor(TypedOrmReader<T> conn) {
    this.conn = conn;
  }

  @Override
  public T readOne(SqlStatement sqlStatement) {
    return conn.readOne(sqlStatement);
  }

  @Override
  public T readFirst(SqlStatement sqlStatement) {
    return conn.readFirst(sqlStatement);
  }

  @Override
  public LazyResultSet<T> readLazy(SqlStatement sqlStatement) {
    return conn.readLazy(sqlStatement);
  }

  @Override
  public List<T> readList(SqlStatement sqlStatement) {
    return conn.readList(sqlStatement);
  }


}
