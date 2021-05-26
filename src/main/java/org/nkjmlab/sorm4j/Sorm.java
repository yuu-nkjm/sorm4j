package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.extension.SormBuilder;
import org.nkjmlab.sorm4j.extension.SormConfig;
import org.nkjmlab.sorm4j.extension.SormConfigBuilder;
import org.nkjmlab.sorm4j.internal.mapping.DriverManagerDataSource;
import org.nkjmlab.sorm4j.internal.mapping.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.mapping.SormImpl;

/**
 * An interface of executing object-relation mapping.
 *
 * @author nkjm
 *
 */
public interface Sorm extends Orm {

  static final SormConfig DEFAULT_CONFIG = new SormConfigBuilder().build();


  /**
   * Create a {@link Sorm} object which uses {@link DataSource}.
   *
   * @param dataSource
   * @return
   */
  static Sorm create(DataSource dataSource) {
    return Sorm.create(dataSource, DEFAULT_CONFIG);
  }

  static Sorm create(DataSource dataSource, SormConfig config) {
    return new SormImpl(dataSource, config);
  }

  /**
   * Create a {@link Sorm} object which uses {@link DriverManager}.
   *
   * @param jdbcUrl
   * @param user
   * @param password
   * @return
   */
  static Sorm create(String jdbcUrl, String user, String password) {
    return create(jdbcUrl, user, password, DEFAULT_CONFIG);
  }


  static Sorm create(String jdbcUrl, String user, String password, SormConfig config) {
    return create(Sorm.createDriverManagerDataSource(jdbcUrl, user, password), config);
  }

  /**
   * Creates a {@link DataSource} which simply wraps {@link DriverManager}
   *
   * @param jdbcUrl
   * @param username
   * @param password
   * @return
   */
  static DataSource createDriverManagerDataSource(String jdbcUrl, String username,
      String password) {
    return new DriverManagerDataSource(jdbcUrl, username, password);
  }


  /**
   * Create a {@link OrmConnection} wrapping the given JDBC Connection
   *
   * @param connection
   * @return
   */
  static OrmConnection toOrmConnection(Connection connection) {
    return Sorm.toOrmConnection(connection, DEFAULT_CONFIG);
  }

  static OrmConnection toOrmConnection(Connection connection, SormConfig sormConfig) {
    return new OrmConnectionImpl(connection, sormConfig);
  }

  /**
   * Accepts a {@link OrmConnection} handler for a task with object-relation mapping. The connection
   * will be closed after the process of handler.
   *
   * @param handler
   */
  void accept(ConsumerHandler<OrmConnection> handler);

  @Experimental
  void acceptWithLogging(ConsumerHandler<OrmConnection> handler);

  @Experimental
  <R> R applyWithLogging(FunctionHandler<OrmConnection, R> handler);


  /**
   * Accepts a {@link Connection} handler for a task with object-relation mapping. The connection
   * will be closed after the process of handler.
   *
   * @param handler
   */
  void acceptJdbcConnectionHandler(ConsumerHandler<Connection> handler);

  /**
   * Accepts a {@link OrmTransaction} handler for a task with object-relation mapping.
   *
   * The transaction will be committed and the connection will be closed after the process of
   * handler. When the transaction throws a exception, the transaction will be rollback.
   *
   * @param handler
   */
  void acceptTransactionHandler(ConsumerHandler<OrmTransaction> handler);

  /**
   * Applies a {@link OrmConnection} handler for a task with object-relation mapping and gets the
   * result. The connection will be closed after the process of handler.
   *
   * @param <R>
   * @param handler
   * @return
   */
  <R> R apply(FunctionHandler<OrmConnection, R> handler);

  /**
   * Applies a {@link Connection} handler for a task with object-relation mapping and gets the
   * result. The connection will be closed after the process of handler.
   *
   * @param <R>
   * @param handler
   * @return
   */
  <R> R applyJdbcConnectionHandler(FunctionHandler<Connection, R> handler);


  /**
   * Applies a {@link OrmTransaction} handler for a task with object-relation mapping and gets the
   * result.
   *
   * The transaction will be committed and the connection will be closed after the process of
   * handler. When the transaction throws a exception, the transaction will be rollback.
   *
   * @param <R>
   * @param handler
   * @return
   */
  <R> R applyTransactionHandler(FunctionHandler<OrmTransaction, R> handler);

  /**
   * Gets the config of this object.
   *
   * @return
   */
  SormConfig getConfig();

  /**
   * Gets {@link DataSource}.
   *
   * @return
   */
  DataSource getDataSource();

  /**
   * Gets JDBC {@link Connection}.
   *
   * @return
   */
  Connection getJdbcConnection();


  /**
   * Gets map of the table mapping status. The keys are table names in lower case.
   *
   * @return
   */
  Map<String, String> getTableMappingStatusMap();

  /**
   * Open {@link OrmConnection}. You should always use try-with-resources to ensure the database
   * connection is released. We recommend using {@link #accept(ConsumerHandler)} or
   * {@link #apply(FunctionHandler)} .
   *
   * @return
   */
  OrmConnection openConnection();

  /**
   * Open {@link OrmTransaction}. You should always use try-with-resources to ensure the database
   * connection is released. We recommend using {@link #acceptTransactionHandler(ConsumerHandler)}
   * or {@link #applyTransactionHandler(FunctionHandler)}. Default transaction level is
   * {@link Connection#TRANSACTION_READ_COMMITTED}.
   *
   * Note: the transaction is automatically rollback if the transaction is not committed.
   *
   * @return
   */
  OrmTransaction openTransaction();

  static SormBuilder newBuilder() {
    return new SormBuilder();
  }

  static SormBuilder newBuilder(DataSource dataSource) {
    return new SormBuilder(dataSource);
  }



}
