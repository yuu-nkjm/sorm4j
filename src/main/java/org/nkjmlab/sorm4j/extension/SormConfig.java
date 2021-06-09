package org.nkjmlab.sorm4j.extension;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.extension.impl.DefaultColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.impl.DefaultResultSetConverter;
import org.nkjmlab.sorm4j.extension.impl.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.extension.impl.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.extension.impl.DefaultTableSqlFactory;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext;
import org.nkjmlab.sorm4j.extension.logger.SormLogger;
import org.nkjmlab.sorm4j.internal.SormOptionsImpl;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessorFactory;

/**
 * A configuration store of sorm4j.
 *
 * @author nkjm
 *
 */
@Experimental
public final class SormConfig {

  private final TableNameMapper tableNameMapper;
  private final ColumnFieldMapper columnFieldMapper;
  private final MultiRowProcessorFactory multiRowProcessorFactory;
  private final ResultSetConverter resultSetConverter;
  private final SqlParametersSetter sqlParametersSetter;
  private final SormOptions options;
  private final int transactionIsolationLevel;
  private final LoggerContext loggerContext;
  private final TableSqlFactory tableSqlFactory;

  public SormConfig(LoggerContext loggerContext, Map<String, Object> options,
      ColumnFieldMapper columnFieldMapper, TableNameMapper tableNameMapper,
      ResultSetConverter resultSetConverter, SqlParametersSetter sqlParametersSetter,
      TableSqlFactory tableSqlFactory, MultiRowProcessorType multiRowProcessorType, int batchSize,
      int multiRowSize, int batchSizeWithMultiRow, int transactionIsolationLevel) {
    this.loggerContext = loggerContext;
    this.options = new SormOptionsImpl(options);
    this.transactionIsolationLevel = transactionIsolationLevel;
    this.tableNameMapper = tableNameMapper;
    this.columnFieldMapper = columnFieldMapper;
    this.multiRowProcessorFactory = MultiRowProcessorFactory.createMultiRowProcessorFactory(
        loggerContext, this.options, sqlParametersSetter, multiRowProcessorType, batchSize,
        multiRowSize, batchSizeWithMultiRow);
    this.resultSetConverter = resultSetConverter;
    this.sqlParametersSetter = sqlParametersSetter;
    this.tableSqlFactory = tableSqlFactory;
  }


  public int getTransactionIsolationLevel() {
    return transactionIsolationLevel;
  }


  public ResultSetConverter getResultSetConverter() {
    return resultSetConverter;
  }

  public SqlParametersSetter getSqlParametersSetter() {
    return sqlParametersSetter;
  }

  public SormOptions getOptions() {
    return options;
  }

  public LoggerContext getLoggerContext() {
    return loggerContext;
  }


  @Override
  public String toString() {
    return "SormConfig [tableNameMapper=" + tableNameMapper + ", columnFieldMapper="
        + columnFieldMapper + ", multiRowProcessorFactory=" + multiRowProcessorFactory
        + ", resultSetConverter=" + resultSetConverter + ", sqlParametersSetter="
        + sqlParametersSetter + ", options=" + options + ", transactionIsolationLevel="
        + transactionIsolationLevel + ", loggerContext=" + loggerContext + "]";
  }


  public ColumnFieldMapper getColumnFieldMapper() {
    return columnFieldMapper;
  }


  public TableNameMapper getTableNameMapper() {
    return tableNameMapper;
  }

  public TableSqlFactory getTableSqlFactory() {
    return tableSqlFactory;
  }


  public MultiRowProcessorFactory getMultiRowProcessorFactory() {
    return multiRowProcessorFactory;
  }

  public static Builder newBuilder() {
    return new Builder();
  }


  @Experimental
  public static class Builder {

    private static final MultiRowProcessorType DEFAULT_MULTI_ROW_PROCESSOR =
        MultiRowProcessorType.MULTI_ROW;

    private static final SqlParametersSetter DEFAULT_SQL_PARAMETER_SETTER =
        new DefaultSqlParametersSetter();

    private static final ResultSetConverter DEFAULT_RESULT_SET_CONVERTER =
        new DefaultResultSetConverter();

    private static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();

    private static final TableSqlFactory DEFAULT_TABLE_SQL_FACTORY = new DefaultTableSqlFactory();

    private static final int DEFAULT_TRANSACTION_ISOLATION_LEVEL =
        Connection.TRANSACTION_READ_COMMITTED;

    private TableNameMapper tableNameMapper = DEFAULT_TABLE_NAME_MAPPER;
    private ResultSetConverter resultSetConverter = DEFAULT_RESULT_SET_CONVERTER;
    private SqlParametersSetter sqlParametersSetter = DEFAULT_SQL_PARAMETER_SETTER;
    private MultiRowProcessorType multiRowProcessorType = DEFAULT_MULTI_ROW_PROCESSOR;
    private TableSqlFactory tableSqlFactory = DEFAULT_TABLE_SQL_FACTORY;

    private ColumnFieldMapper columnFieldMapper;
    private int batchSize = 32;
    private int multiRowSize = 32;
    private int batchSizeWithMultiRow = 5;
    private int transactionIsolationLevel = DEFAULT_TRANSACTION_ISOLATION_LEVEL;
    private Map<String, Object> options = new HashMap<>();
    private LoggerContext.Builder loggerConfigBuilder = LoggerContext.newBuilder();



    public Builder() {}

    public SormConfig build() {
      LoggerContext loggerContext = loggerConfigBuilder.build();
      columnFieldMapper = columnFieldMapper != null ? columnFieldMapper
          : new DefaultColumnFieldMapper(loggerContext);
      return new SormConfig(loggerContext, options, columnFieldMapper, tableNameMapper,
          resultSetConverter, sqlParametersSetter, tableSqlFactory, multiRowProcessorType,
          batchSize, multiRowSize, batchSizeWithMultiRow, transactionIsolationLevel);
    }


    public Builder setColumnFieldMapper(ColumnFieldMapper fieldNameMapper) {
      this.columnFieldMapper = fieldNameMapper;
      return this;
    }


    public Builder setTableNameMapper(TableNameMapper tableNameMapper) {
      this.tableNameMapper = tableNameMapper;
      return this;
    }


    public Builder setResultSetConverter(ResultSetConverter resultSetConverter) {
      this.resultSetConverter = resultSetConverter;
      return this;
    }


    public Builder setSqlParametersSetter(SqlParametersSetter sqlParametersSetter) {
      this.sqlParametersSetter = sqlParametersSetter;
      return this;
    }

    public Builder setTableSqlFactory(TableSqlFactory tableSqlFactory) {
      this.tableSqlFactory = tableSqlFactory;
      return this;
    }


    public Builder setMultiRowProcessorType(MultiRowProcessorType multiRowProcessorType) {
      this.multiRowProcessorType = multiRowProcessorType;
      return this;
    }


    public Builder setBatchSize(int size) {
      this.batchSize = size;
      return this;
    }


    public Builder setMultiRowSize(int size) {
      this.multiRowSize = size;
      return this;
    }


    public Builder setBatchSizeWithMultiRow(int size) {
      this.batchSizeWithMultiRow = size;
      return this;
    }


    public Builder setTransactionIsolationLevel(int level) {
      this.transactionIsolationLevel = level;
      return this;
    }


    public Builder setOption(String name, Object value) {
      this.options.put(name, value);
      return this;
    }


    public Builder setLoggerOnAll() {
      this.loggerConfigBuilder.onAll();
      return this;
    }

    public Builder setLoggerOffAll() {
      this.loggerConfigBuilder.offAll();
      return this;
    }

    public Builder setLoggerOn(LoggerContext.Category... categories) {
      this.loggerConfigBuilder.on(categories);
      return this;
    }

    public Builder setLoggerOff(LoggerContext.Category... categories) {
      this.loggerConfigBuilder.off(categories);
      return this;
    }

    public Builder setLoggerSupplier(Supplier<SormLogger> loggerSupplier) {
      this.loggerConfigBuilder.setLoggerSupplier(loggerSupplier);
      return this;
    }
  }



}
