package org.nkjmlab.sorm4j.internal.sql;

import java.util.List;
import org.nkjmlab.sorm4j.OrmReader;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

public class QueryOrmExecutor<T> implements QueryExecutor<T> {

  protected final OrmReader conn;
  private final Class<T> objectClass;


  public QueryOrmExecutor(OrmReader conn, Class<T> objectClass) {
    this.conn = conn;
    this.objectClass = objectClass;
  }

  @Override
  public T readOne(ParameterizedSql parameterizedSql) {
    return conn.readOne(objectClass, parameterizedSql);
  }

  @Override
  public T readFirst(ParameterizedSql parameterizedSql) {
    return conn.readFirst(objectClass, parameterizedSql);
  }

  @Override
  public LazyResultSet<T> readLazy(ParameterizedSql parameterizedSql) {
    return conn.readLazy(objectClass, parameterizedSql);
  }

  @Override
  public List<T> readList(ParameterizedSql parameterizedSql) {
    return conn.readList(objectClass, parameterizedSql);
  }


}
