package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.annotation.Experimental;
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
public interface Sorm {

  static final SormConfig INITIAL_DEFAULT_CONFIG_STORE = new SormConfigBuilder().build();


  /**
   * Create a {@link Sorm} object which uses {@link DataSource}.
   *
   * @param dataSource
   * @return
   */
  static Sorm create(DataSource dataSource) {
    return Sorm.create(dataSource, INITIAL_DEFAULT_CONFIG_STORE);
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
    return Sorm.create(jdbcUrl, user, password, INITIAL_DEFAULT_CONFIG_STORE);
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
    return Sorm.toOrmConnection(connection, INITIAL_DEFAULT_CONFIG_STORE);
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
   * Get the config name of this object.
   *
   * @return
   */
  String getConfigName();

  /**
   * Get the string of the config of this object.
   *
   * @return
   */
  String getConfigString();

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
   * Gets a {@link Orm} object.
   *
   * @return
   */
  @Experimental
  Orm getOrm();

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

}
