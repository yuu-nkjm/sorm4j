package org.nkjmlab.sorm4j.internal.result;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.nkjmlab.sorm4j.common.handler.ConsumerHandler;
import org.nkjmlab.sorm4j.common.handler.FunctionHandler;
import org.nkjmlab.sorm4j.container.sql.result.ResultSetStream;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.context.logging.LogContext;
import org.nkjmlab.sorm4j.context.logging.LogContext.Category;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.internal.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.internal.context.logging.LogPoint;
import org.nkjmlab.sorm4j.internal.util.Try;

abstract class AbstractResultSetStream<T> implements ResultSetStream<T> {
  private final Class<T> objectClass;
  private final String sql;
  private final Object[] parameters;

  public AbstractResultSetStream(Class<T> objectClass, String sql, Object[] parameters) {
    this.objectClass = objectClass;
    this.sql = sql;
    this.parameters = parameters;
  }

  @Override
  public void accept(ConsumerHandler<Stream<T>> handler) {
    apply(
        stream -> {
          handler.accept(stream);
          return null;
        });
  }

  public <R> R apply(OrmConnectionImpl ormConn, FunctionHandler<Stream<T>, R> handler) {
    SormContext context = ormConn.getContext();
    LogContext loggerContext = context.getLogContext();
    PreparedStatementSupplier statementSupplier = context.getPreparedStatementSupplier();
    SqlParametersSetter parametersSetter = context.getSqlParametersSetter();

    try (PreparedStatement stmt =
        statementSupplier.prepareStatement(ormConn.getJdbcConnection(), sql)) {

      parametersSetter.setParameters(stmt, parameters);

      Optional<LogPoint> lp = loggerContext.createLogPoint(Category.EXECUTE_QUERY, objectClass);
      lp.ifPresent(_lp -> _lp.logBeforeSql(ormConn.getJdbcConnection(), sql, parameters));

      try (ResultSet resultSet = stmt.executeQuery()) {
        Stream<T> stream =
            StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                    new ResultSetIterator<T>(ormConn, objectClass, resultSet), Spliterator.ORDERED),
                false);
        R ret = handler.apply(stream);
        lp.ifPresent(_lp -> _lp.logAfterQuery(ret));
        return ret;
      }
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }
}
