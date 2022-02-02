package org.nkjmlab.sorm4j.internal.result;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.nkjmlab.sorm4j.common.ConsumerHandler;
import org.nkjmlab.sorm4j.common.FunctionHandler;
import org.nkjmlab.sorm4j.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.result.ResultSetStream;
import org.nkjmlab.sorm4j.util.logger.LogPoint;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;
import org.nkjmlab.sorm4j.util.logger.LoggerContext.Category;

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
    apply(stream -> {
      handler.accept(stream);
      return null;
    });
  }

  public <R> R apply(OrmConnectionImpl ormConn, FunctionHandler<Stream<T>, R> handler) {
    SormContext context = ormConn.getContext();
    LoggerContext loggerContext = context.getLoggerContext();
    PreparedStatementSupplier statementSupplier = context.getPreparedStatementSupplier();
    SqlParametersSetter parametersSetter = context.getSqlParametersSetter();

    try (PreparedStatement stmt =
        statementSupplier.prepareStatement(ormConn.getJdbcConnection(), sql)) {

      parametersSetter.setParameters(stmt, parameters);

      Optional<LogPoint> lp = loggerContext.createLogPoint(Category.EXECUTE_QUERY, objectClass);
      lp.ifPresent(_lp -> _lp.logBeforeSql(ormConn.getJdbcConnection(), sql, parameters));

      try (ResultSet resultSet = stmt.executeQuery()) {
        Stream<T> stream = StreamSupport.stream(
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
