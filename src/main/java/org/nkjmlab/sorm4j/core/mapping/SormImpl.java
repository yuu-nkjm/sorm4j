package org.nkjmlab.sorm4j.core.mapping;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.ConsumerHandler;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.OrmTransaction;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormFactory;
import org.nkjmlab.sorm4j.TypedOrmConnection;
import org.nkjmlab.sorm4j.TypedOrmTransaction;
import org.nkjmlab.sorm4j.core.util.Try;

/**
 * An entry point of object-relation mapping.
 *
 * @author nkjm
 *
 */
public final class SormImpl implements Sorm {
  // private static final org.slf4j.Logger log =
  // org.nkjmlab.sorm4j.core.util.LoggerFactory.getLogger();

  private final DataSource dataSource;

  private final ConfigStore configStore;


  public SormImpl(DataSource connectionSource, ConfigStore configs) {
    this.configStore = configs;
    this.dataSource = connectionSource;
  }

  @Override
  public OrmTransaction openTransaction() {
    return new OrmTransactionImpl(getJdbcConnection(), configStore);
  }

  @Override
  public <T> TypedOrmTransaction<T> openTransaction(Class<T> objectClass) {
    return new TypedOrmTransactionImpl<>(objectClass,
        new OrmTransactionImpl(getJdbcConnection(), configStore));
  }


  @Override
  public <T, R> R apply(Class<T> objectClass, FunctionHandler<TypedOrmConnection<T>, R> handler) {
    try (TypedOrmConnection<T> conn = openConnection(objectClass)) {
      return handler.apply(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
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
  public <T, R> R applyTransactionHandler(Class<T> objectClass,
      FunctionHandler<TypedOrmTransaction<T>, R> handler) {
    try (TypedOrmTransaction<T> transaction = openTransaction(objectClass)) {
      return handler.apply(transaction);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }



  @Override
  public <R> R applyTransactionHandler(FunctionHandler<OrmTransaction, R> handler) {
    try (OrmTransaction transaction = openTransaction()) {
      return handler.apply(transaction);
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
  public String getConfigName() {
    return configStore.getConfigName();
  }

  @Override
  public String getConfigString() {
    return configStore.toString();
  }


  @Override
  public OrmConnection openConnection() {
    return new OrmConnectionImpl(getJdbcConnection(), configStore);
  }



  @Override
  public <T> TypedOrmConnection<T> openConnection(Class<T> objectClass) {
    return new TypedOrmConnectionImpl<T>(objectClass,
        new OrmConnectionImpl(getJdbcConnection(), configStore));
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
  public <T> void accept(Class<T> objectClass, ConsumerHandler<TypedOrmConnection<T>> handler) {
    try (TypedOrmConnection<T> conn = openConnection(objectClass)) {
      try {
        handler.accept(conn);
      } catch (Exception e) {
        throw Try.rethrow(e);
      }
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
  public <T> void acceptTransactionHandler(Class<T> objectClass,
      ConsumerHandler<TypedOrmTransaction<T>> handler) {
    try (TypedOrmTransaction<T> transaction = openTransaction(objectClass)) {
      handler.accept(transaction);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }



  @Override
  public void acceptTransactionHandler(ConsumerHandler<OrmTransaction> handler) {
    try (OrmTransaction conn = openTransaction()) {
      handler.accept(conn);
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
    return "Sorm [dataSource=" + dataSource + ", configStore=" + configStore + "]";
  }


  public static final class OrmTransactionImpl extends OrmConnectionImpl implements OrmTransaction {

    public OrmTransactionImpl(Connection connection, ConfigStore options) {
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
      super(objectClass, (OrmConnectionImpl) ormTransaction);
      begin(ormTransaction.getTransactionIsolationLevel());
    }

    @Override
    public void close() {
      rollback();
      super.close();
    }

    @Override
    public <S> TypedOrmTransaction<S> type(Class<S> objectClass) {
      return new TypedOrmTransactionImpl<>(objectClass, (OrmTransactionImpl) ormConnection);
    }

    @Override
    public OrmTransaction untype() {
      return (OrmTransactionImpl) ormConnection;
    }

  }

  @Override
  public Sorm createWith(String configName) {
    return SormFactory.create(dataSource, configName);
  }

  @Override
  public Map<String, String> getTableMappingStatusMap() {
    return configStore.getTableMappings().entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getFormattedString()));
  }


}
