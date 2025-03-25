package org.nkjmlab.sorm4j.internal.sql.result;

import java.util.stream.Stream;

import org.nkjmlab.sorm4j.common.handler.FunctionHandler;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;

/**
 * Represents a result set from database.
 *
 * @param <T> element type in result
 */
public final class ResultSetStreamOrmConnection<T> extends AbstractResultSetStream<T> {

  private final OrmConnectionImpl ormConn;

  public ResultSetStreamOrmConnection(
      OrmConnectionImpl ormConn, Class<T> objectClass, String sql, Object... parameters) {
    super(objectClass, sql, parameters);
    this.ormConn = ormConn;
  }

  @Override
  public <R> R apply(FunctionHandler<Stream<T>, R> handler) {
    return apply(ormConn, handler);
  }
}
