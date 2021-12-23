package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.basic.ConsumerHandler;
import org.nkjmlab.sorm4j.basic.FunctionHandler;
import org.nkjmlab.sorm4j.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.MultiRowProcessorType;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SormContext;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.extension.TableNameMapper;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext;
import org.nkjmlab.sorm4j.extension.logger.SormLogger;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.SormImpl;
import org.nkjmlab.sorm4j.internal.util.DriverManagerDataSource;

/**
 * An interface of executing object-relation mapping.
 *
 * @author nkjm
 *
 */
public interface Sorm extends Orm {

  static class DefaultContext {
    static volatile SormContext CONTEXT = SormContext.builder().build();
  }


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
    return SormImpl.create(dataSource, DefaultContext.CONTEXT);
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
   * Create a {@link Sorm} object which uses {@link DriverManager}.
   *
   * <p>
   * For example,
   *
   * <pre>
   * <code>
   * Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
   *</pre></code>
   *
   * @param jdbcUrl
   * @return
   */
  static Sorm create(String jdbcUrl) {
    return create(createDataSource(jdbcUrl, null, null));
  }

  static void setDefaultContext(Function<SormContext.Builder, SormContext> contextGenerator) {
    DefaultContext.CONTEXT = contextGenerator.apply(SormContext.builder());
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
    return Sorm.toOrmConnection(connection, DefaultContext.CONTEXT);
  }

  static OrmConnection toOrmConnection(Connection connection, SormContext sormContext) {
    return new OrmConnectionImpl(connection, sormContext);
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

  static Builder builder() {
    return new Builder();
  }

  static Builder builder(DataSource dataSource) {
    return new Builder(dataSource);
  }

  static Builder builder(String jdbcUrl, String user, String password) {
    return new Builder(createDataSource(jdbcUrl, user, password));
  }

  @Experimental
  public static class Builder {

    private DataSource dataSource;
    private SormContext.Builder contextBuilder = SormContext.builder();

    private Builder() {}

    public Builder(DataSource dataSource) {
      this.dataSource = dataSource;
    }

    public Sorm build() {
      return new SormImpl(dataSource, contextBuilder.build());
    }

    public Builder setDataSource(DataSource dataSource) {
      this.dataSource = dataSource;
      return this;
    }


    public Builder setDataSource(String jdbcUrl, String username, String password) {
      this.dataSource = Sorm.createDataSource(jdbcUrl, username, password);
      return this;
    }

    public Builder setColumnFieldMapper(ColumnFieldMapper fieldNameMapper) {
      contextBuilder.setColumnFieldMapper(fieldNameMapper);
      return this;
    }


    public Builder setTableNameMapper(TableNameMapper tableNameMapper) {
      contextBuilder.setTableNameMapper(tableNameMapper);
      return this;
    }


    public Builder setResultSetConverter(ResultSetConverter resultSetConverter) {
      contextBuilder.setResultSetConverter(resultSetConverter);
      return this;
    }


    public Builder setSqlParametersSetter(SqlParametersSetter sqlParametersSetter) {
      contextBuilder.setSqlParametersSetter(sqlParametersSetter);
      return this;
    }


    public Builder setMultiRowProcessorType(MultiRowProcessorType multiRowProcessorType) {
      contextBuilder.setMultiRowProcessorType(multiRowProcessorType);
      return this;
    }


    public Builder setBatchSize(int size) {
      contextBuilder.setBatchSize(size);
      return this;
    }


    public Builder setMultiRowSize(int size) {
      contextBuilder.setMultiRowSize(size);
      return this;
    }


    public Builder setBatchSizeWithMultiRow(int size) {
      contextBuilder.setBatchSizeWithMultiRow(size);
      return this;
    }


    public Builder setTransactionIsolationLevel(int level) {
      contextBuilder.setTransactionIsolationLevel(level);
      return this;
    }


    public Builder setOption(String name, Object value) {
      contextBuilder.setOption(name, value);
      return this;
    }


    public Builder setLoggerOnAll() {
      contextBuilder.setLoggerOnAll();
      return this;
    }

    public Builder setLoggerOffAll() {
      contextBuilder.setLoggerOffAll();
      return this;
    }

    public Builder setLoggerOn(LoggerContext.Category... categories) {
      contextBuilder.setLoggerOn(categories);
      return this;
    }

    public Builder setLoggerOff(LoggerContext.Category... categories) {
      contextBuilder.setLoggerOff(categories);
      return this;
    }

    public Builder setLoggerSupplier(Supplier<SormLogger> loggerSupplier) {
      contextBuilder.setLoggerSupplier(loggerSupplier);
      return this;
    }


  }


}
