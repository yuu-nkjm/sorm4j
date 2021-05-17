package org.nkjmlab.sorm4j.internal.mapping;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.ConsumerHandler;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.OrmTransaction;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.extension.SormConfig;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.typed.TypedOrmTransaction;

/**
 * An entry point of object-relation mapping.
 *
 * @author nkjm
 *
 */
public final class SormImpl implements Sorm {

  private final DataSource dataSource;
  private final SormConfig sormConfig;
  private final Orm orm;


  public SormImpl(DataSource connectionSource, SormConfig configs) {
    this.sormConfig = configs;
    this.dataSource = connectionSource;
    this.orm = new OrmImpl(this);
  }

  @Override
  public OrmTransaction openTransaction() {
    return new OrmTransactionImpl(getJdbcConnection(), sormConfig);
  }


  @Override
  public <R> R apply(FunctionHandler<OrmConnection, R> handler) {
    try (OrmConnection conn = openConnection()) {
      return handler.apply(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }



  @Override
  public <R> R applyTransactionHandler(FunctionHandler<OrmTransaction, R> handler) {
    try (OrmTransaction transaction = openTransaction()) {
      R ret = handler.apply(transaction);
      transaction.commit();
      return ret;
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public <R> R applyJdbcConnectionHandler(FunctionHandler<Connection, R> handler) {
    try (Connection conn = getJdbcConnection()) {
      return handler.apply(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }


  @Override
  public SormConfig getConfig() {
    return sormConfig;
  }


  @Override
  public OrmConnection openConnection() {
    return new OrmConnectionImpl(getJdbcConnection(), sormConfig);
  }



  @Override
  public DataSource getDataSource() {
    return this.dataSource;
  }

  @Override
  public Connection getJdbcConnection() {
    try {
      return dataSource.getConnection();
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }



  @Override
  public void accept(ConsumerHandler<OrmConnection> handler) {
    try (OrmConnection conn = openConnection()) {
      handler.accept(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }



  @Override
  public void acceptTransactionHandler(ConsumerHandler<OrmTransaction> handler) {
    try (OrmTransaction transaction = openTransaction()) {
      handler.accept(transaction);
      transaction.commit();
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public void acceptJdbcConnectionHandler(ConsumerHandler<Connection> handler) {
    try (Connection conn = getJdbcConnection()) {
      handler.accept(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }


  @Override
  public String toString() {
    return "Sorm [dataSource=" + dataSource + ", sormConfig=" + sormConfig + "]";
  }


  public static final class OrmTransactionImpl extends OrmConnectionImpl implements OrmTransaction {

    public OrmTransactionImpl(Connection connection, SormConfig options) {
      super(connection, options);
      begin(options.getTransactionIsolationLevel());
    }

    @Override
    public void close() {
      rollback();
      super.close();
    }

    @Override
    public <T> TypedOrmTransaction<T> type(Class<T> objectClass) {
      return new TypedOrmTransactionImpl<>(objectClass, this);
    }

  }

  public static class TypedOrmTransactionImpl<T> extends TypedOrmConnectionImpl<T>
      implements TypedOrmTransaction<T> {

    public TypedOrmTransactionImpl(Class<T> objectClass, OrmTransactionImpl ormTransaction) {
      super(objectClass, ormTransaction);
      ormTransaction.begin();
    }

    @Override
    public void close() {
      rollback();
      super.close();
    }

    @Override
    public <S> TypedOrmTransaction<S> type(Class<S> objectClass) {
      return new TypedOrmTransactionImpl<>(objectClass, (OrmTransactionImpl) conn);
    }

    @Override
    public OrmTransaction untype() {
      return (OrmTransactionImpl) conn;
    }

  }

  @Override
  public Map<String, String> getTableMappingStatusMap() {
    return sormConfig.getTableMappingStatusMap();
  }

  @Override
  public Orm getOrm() {
    return orm;
  }

}
