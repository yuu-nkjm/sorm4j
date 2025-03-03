package org.nkjmlab.sorm4j;

import java.sql.Connection;

import javax.sql.DataSource;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.common.ConsumerHandler;
import org.nkjmlab.sorm4j.common.FunctionHandler;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.internal.SormImpl;
import org.nkjmlab.sorm4j.internal.common.DriverManagerDataSource;
import org.nkjmlab.sorm4j.table.Table;

/**
 * An interface of executing object-relation mapping. Object-relation mapping functions with an
 * instant connection. When executing these functions, this object gets a connection and executes
 * the function, after that closes the connection immediately.
 *
 * @author nkjm
 */
public interface Sorm extends Orm {

  /**
   * Create a {@link Sorm} object which uses {@link DataSource}.
   *
   * <p>For example,
   *
   * <pre><code>
   *  DataSource dataSource = org.h2.jdbcx.JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;","sa","");
   * Sorm.create(dataSource);
   * </pre></code>
   *
   * @param dataSource
   * @return
   */
  static Sorm create(DataSource dataSource) {
    return create(dataSource, SormImpl.DEFAULT_CONTEXT);
  }

  /**
   * Creates a {@link Sorm} instance which uses the given {@link DriverManagerDataSource} and the
   * given {@link SormContext}.
   *
   * @param dataSource
   * @param context
   * @return
   */
  static Sorm create(DataSource dataSource, SormContext context) {
    return SormImpl.create(dataSource, context);
  }

  /**
   * Creates a {@link Sorm} instance which uses the given {@link DriverManagerDataSource}.
   *
   * <p>If you want specified more precise configuration of database access, create {@link
   * DataSource} yourself and use {@link #create(DataSource)} method.
   *
   * <p>For example,
   *
   * <pre>
   * <code>
   *    Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
   * </code>
   * </pre>
   *
   * @param jdbcUrl
   * @return
   */
  static Sorm create(String jdbcUrl) {
    return create(createDataSource(jdbcUrl, null, null));
  }

  /**
   * Creates an instance of {@link DataSource}
   *
   * <p>Example:
   *
   * <pre>
   * Sorm.createDataSource("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", null, null);
   * </pre>
   *
   * @param jdbcUrl
   * @param username
   * @param password
   * @return
   */
  static DataSource createDataSource(String jdbcUrl, String username, String password) {
    return DriverManagerDataSource.create(jdbcUrl, username, password);
  }

  /**
   * Gets the default {@link SormContext} instance.
   *
   * @return
   */
  static SormContext getDefaultContext() {
    return SormImpl.DEFAULT_CONTEXT;
  }

  /**
   * Accepts a {@link OrmConnection} handler for a task with object-relation mapping. The connection
   * will be closed after the process of handler.
   *
   * @param handler
   */
  void acceptHandler(ConsumerHandler<OrmConnection> handler);

  /**
   * Accepts a {@link OrmTransaction} handler for a task with object-relation mapping.
   *
   * <p>Note: The transaction will be closed after the process of handler. The transaction will be
   * rolled back if the transaction closes before commit. When an exception throws in the
   * transaction, the transaction will be rollback.
   *
   * @param isolationLevel
   * @param transactionHandler
   */
  void acceptHandler(int isolationLevel, ConsumerHandler<OrmTransaction> transactionHandler);

  /**
   * Applies a {@link OrmConnection} handler for a task with object-relation mapping and gets the
   * result. The connection will be closed after the process of handler.
   *
   * @param <R>
   * @param connectionHandler
   * @return
   */
  <R> R applyHandler(FunctionHandler<OrmConnection, R> connectionHandler);

  /**
   * Applies a {@link OrmTransaction} handler for a task with object-relation mapping and gets the
   * result.
   *
   * <p>Note: The transaction will be closed after the process of handler. The transaction will be
   * rolled back if the transaction closes before commit. When an exception throws in the
   * transaction, the transaction will be rolled back.
   *
   * @param isolationLevel
   * @param transactionHandler
   * @param <R>
   * @return
   */
  <R> R applyHandler(int isolationLevel, FunctionHandler<OrmTransaction, R> transactionHandler);

  /**
   * Gets {@link DataSource}.
   *
   * @return
   */
  DataSource getDataSource();

  /**
   * Opens JDBC {@link Connection}.
   *
   * @return
   */
  Connection openJdbcConnection();

  /**
   * Open {@link OrmConnection}. You should always use <code>try-with-resources</code> block to
   * ensure the database connection is released.
   *
   * <p>You cold also use {@link Sorm#acceptHandler(ConsumerHandler)} or {@link
   * Sorm#applyHandler(FunctionHandler)} .
   *
   * @return
   */
  OrmConnection open();

  /**
   * Open {@link OrmTransaction}. You should always use try-with-resources block to ensure the
   * database connection is released.
   *
   * <p>You could also use {@link Sorm#acceptHandler(int, ConsumerHandler)} or {@link
   * Sorm#applyHandler(int, FunctionHandler)}.
   *
   * <p><strong>Note:</strong> If you do not explicitly commit in a opened transaction, it will be
   * rolled back.
   *
   * @param isolationLevel {@link Connection#TRANSACTION_READ_COMMITTED}, {@link
   *     Connection#TRANSACTION_READ_UNCOMMITTED}, ...,and so on.
   * @return
   */
  OrmTransaction open(int isolationLevel);

  /**
   * Gets a new {@link Table} instance.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  @Experimental
  <T> Table<T> getTable(Class<T> objectClass);

  /**
   * Gets a new {@link Table} instance.
   *
   * @param <T>
   * @param type
   * @param tableName
   * @return
   */
  @Experimental
  <T> Table<T> getTable(Class<T> type, String tableName);
}
