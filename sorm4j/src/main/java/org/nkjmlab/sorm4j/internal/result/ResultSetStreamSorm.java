package org.nkjmlab.sorm4j.internal.result;

import java.util.stream.Stream;

import org.nkjmlab.sorm4j.common.handler.FunctionHandler;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.SormImpl;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * Represents a result set from database.
 *
 * @param <T> element type in result
 */
public final class ResultSetStreamSorm<T> extends AbstractResultSetStream<T> {

  private final SormImpl sorm;

  public ResultSetStreamSorm(
      SormImpl sorm, Class<T> objectClass, String sql, Object... parameters) {
    super(objectClass, sql, parameters);
    this.sorm = sorm;
  }

  @Override
  public <R> R apply(FunctionHandler<Stream<T>, R> handler) {
    try (OrmConnectionImpl ormConn = sorm.open()) {
      return apply(ormConn, handler);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }
}
