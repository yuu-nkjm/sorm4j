package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.common.ConsumerHandler;
import org.nkjmlab.sorm4j.common.FunctionHandler;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.SormContextImpl;
import org.nkjmlab.sorm4j.internal.SormImpl;
import org.nkjmlab.sorm4j.internal.util.DriverManagerDataSource;

/**
 * An interface of executing object-relation mapping. Object-relation mapping functions with an
 * instant connection. When executing these functions, this object gets a connection and executes
 * the function, after that closes the connection immediately.
 *
 * @author nkjm
 *
 */
public interface Sorm extends Orm {


  /**
   * Create a {@link Sorm} object which uses {@link DataSource}.
   *
   * <p>
   * For example,
   *
   * <pre>
   * <code>
   * DataSource dataSource = org.h2.jdbcx.JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;","sa","");
   * Sorm.create(dataSource);
   *</pre></code>
   *
   * @param dataSource
   * @return
   */
  static Sorm create(DataSource dataSource) {
    return create(dataSource, SormContext.DEFAULT_CONTEXT);
  }

  static Sorm create(DataSource dataSource, SormContext context) {
    return SormImpl.create(dataSource, context);
  }


  /**
   * Create a {@link Sorm} object which uses {@link DriverManager}.
   *
   * <p>
   * For example,
   *
   * <pre>
   * <code>
   * Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;","sa","");
   *</pre></code>
   *
   * @param jdbcUrl
   * @param user
   * @param password
   * @return
   */
  static Sorm create(String jdbcUrl, String user, String password) {
    return create(createDataSource(jdbcUrl, user, password));
  }

  /**
   * Creates a {@link DataSource} which simply wraps {@link DriverManager}
   *
   * @param jdbcUrl
   * @param username
   * @param password
   * @return
   */
  static DataSource createDataSource(String jdbcUrl, String username, String password) {
    return new DriverManagerDataSource(jdbcUrl, username, password);
  }


  /**
   * Create a {@link OrmConnection} wrapping the given JDBC Connection
   *
   * @param connection
   * @return
   */
  static OrmConnection toOrmConnection(Connection connection) {
    return Sorm.toOrmConnection(connection, SormContext.DEFAULT_CONTEXT);
  }

  static OrmConnection toOrmConnection(Connection connection, SormContext sormContext) {
    return new OrmConnectionImpl(connection, SormContextImpl.class.cast(sormContext));
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
   * Gets the context string of this object.
   *
   * @return
   */
  @Experimental
  String getContextString();

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
