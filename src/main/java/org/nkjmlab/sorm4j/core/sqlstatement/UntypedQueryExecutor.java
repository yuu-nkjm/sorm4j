package org.nkjmlab.sorm4j.core.sqlstatement;

import java.util.List;
import org.nkjmlab.sorm4j.OrmReader;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.SqlStatement;

public class UntypedQueryExecutor<T> implements QueryExecutor<T> {

  protected final OrmReader conn;
  private final Class<T> objectClass;


  public UntypedQueryExecutor(OrmReader conn, Class<T> objectClass) {
    this.conn = conn;
    this.objectClass = objectClass;
  }

  @Override
  public T readOne(SqlStatement sqlStatement) {
    return conn.readOne(objectClass, sqlStatement);
  }

  @Override
  public T readFirst(SqlStatement sqlStatement) {
    return conn.readFirst(objectClass, sqlStatement);
  }

  @Override
  public LazyResultSet<T> readLazy(SqlStatement sqlStatement) {
    return conn.readLazy(objectClass, sqlStatement);
  }

  @Override
  public List<T> readList(SqlStatement sqlStatement) {
    return conn.readList(objectClass, sqlStatement);
  }


}
