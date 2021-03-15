package org.nkjmlab.sorm4j.core.mapping;

import java.sql.Connection;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.TypedOrmConnection;
import org.nkjmlab.sorm4j.core.util.Try;
import org.nkjmlab.sorm4j.sqlstatement.NamedParameterSql;
import org.nkjmlab.sorm4j.sqlstatement.OrderedParameterSql;
import org.nkjmlab.sorm4j.sqlstatement.SelectBuilder;

/**
 * A database connection with object-relation mapping function. The main class for the ORMapper
 * engine.
 *
 * This instance wraps a {@link java.sql.Connection} object. OrmMapper instances are not thread
 * safe, in particular because {@link java.sql.Connection} objects are not thread safe.
 *
 * @author nkjm
 *
 */
public class OrmConnectionImpl extends OrmMapperImpl implements OrmConnection {

  public OrmConnectionImpl(Connection connection, ConfigStore options) {
    super(connection, options);
  }

  @Override
  public void close() {
    Try.runOrThrow(() -> {
      if (getJdbcConnection().isClosed()) {
        return;
      }
      getJdbcConnection().close();
    }, Try::rethrow);
  }

  @Override
  public void commit() {
    Try.runOrThrow(() -> getJdbcConnection().commit(), Try::rethrow);
  }

  @Override
  public void rollback() {
    Try.runOrThrow(() -> getJdbcConnection().rollback(), Try::rethrow);
  }


  @Override
  public void setAutoCommit(final boolean autoCommit) {
    Try.runOrThrow(() -> getJdbcConnection().setAutoCommit(autoCommit), Try::rethrow);
  }

  @Override
  public void begin(int isolationLevel) {
    setAutoCommit(false);
    setTransactionIsolation(isolationLevel);
  }

  @Override
  public void begin() {
    begin(getTransactionIsolationLevel());
  }

  private void setTransactionIsolation(int isolationLevel) {
    Try.runOrThrow(() -> getJdbcConnection().setTransactionIsolation(isolationLevel), Try::rethrow);
  }

  @Override
  public NamedParameterSql createNamedParameterSql(String sql) {
    return NamedParameterSql.from(sql);
  }

  @Override
  public OrderedParameterSql createOrderedParameterSql(String sql) {
    return OrderedParameterSql.from(sql);
  }

  @Override
  public SelectBuilder createSelectBuilder() {
    return SelectBuilder.create();
  }

  @Override
  public <S> TypedOrmConnection<S> type(Class<S> objectClass) {
    return new TypedOrmConnectionImpl<>(objectClass, getJdbcConnection(), getConfigStore());
  }

}
