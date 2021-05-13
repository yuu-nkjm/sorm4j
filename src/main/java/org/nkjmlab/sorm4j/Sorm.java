package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.util.Map;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * An interface of executing object-relation mapping.
 *
 * @author nkjm
 *
 */
public interface Sorm {


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
   * Creates a Sorm object with the given config name. The config name should be registered by
   * {@link SormFactory#registerConfig(String, java.util.function.Consumer)} or
   * {@link SormFactory#registerModifiedConfig(String, String, java.util.function.Consumer)}
   *
   * @param configName
   * @return
   */
  Sorm createWith(String configName);

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

  /**
   * Gets a {@link Orm} object.
   *
   * @return
   */
  @Experimental
  Orm getOrm();

}
