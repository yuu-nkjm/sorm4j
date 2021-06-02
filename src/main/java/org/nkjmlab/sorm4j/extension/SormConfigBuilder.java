package org.nkjmlab.sorm4j.extension;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.extension.logger.SormLogger;

/**
 * A builder for configuration of or mapper.
 *
 * @author nkjm
 *
 */
@Experimental
public class SormConfigBuilder {

  public static final MultiRowProcessorType DEFAULT_MULTI_ROW_PROCESSOR =
      MultiRowProcessorType.MULTI_ROW;

  public static final SqlParametersSetter DEFAULT_SQL_PARAMETER_SETTER =
      new DefaultSqlParametersSetter();

  public static final ResultSetConverter DEFAULT_RESULT_SET_CONVERTER =
      new DefaultResultSetConverter();

  public static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();


  public static final int DEFAULT_TRANSACTION_ISOLATION_LEVEL =
      Connection.TRANSACTION_READ_COMMITTED;
  public static final String DEFAULT_CACHE_NAME = "DEFAULT_CACHE";

  private TableNameMapper tableNameMapper = DEFAULT_TABLE_NAME_MAPPER;
  private ResultSetConverter resultSetConverter = DEFAULT_RESULT_SET_CONVERTER;
  private SqlParametersSetter sqlParametersSetter = DEFAULT_SQL_PARAMETER_SETTER;
  private MultiRowProcessorType multiRowProcessorType = DEFAULT_MULTI_ROW_PROCESSOR;
  private ColumnFieldMapper columnFieldMapper;
  private int batchSize = 32;
  private int multiRowSize = 32;
  private int batchSizeWithMultiRow = 5;
  private int transactionIsolationLevel = DEFAULT_TRANSACTION_ISOLATION_LEVEL;
  private Map<String, Object> options = new HashMap<>();
  private LoggerConfig.Builder loggerConfigBuilder = new LoggerConfig.Builder();



  public SormConfigBuilder() {}

  public SormConfig build() {
    LoggerConfig loggerConfig = loggerConfigBuilder.build();
    columnFieldMapper =
        columnFieldMapper != null ? columnFieldMapper : new DefaultColumnFieldMapper(loggerConfig);
    return new SormConfig(loggerConfig, options, columnFieldMapper, tableNameMapper,
        resultSetConverter, sqlParametersSetter, multiRowProcessorType, batchSize, multiRowSize,
        batchSizeWithMultiRow, transactionIsolationLevel);
  }


  public SormConfigBuilder setColumnFieldMapper(ColumnFieldMapper fieldNameMapper) {
    this.columnFieldMapper = fieldNameMapper;
    return this;
  }


  public SormConfigBuilder setTableNameMapper(TableNameMapper tableNameMapper) {
    this.tableNameMapper = tableNameMapper;
    return this;
  }


  public SormConfigBuilder setResultSetConverter(ResultSetConverter resultSetConverter) {
    this.resultSetConverter = resultSetConverter;
    return this;
  }


  public SormConfigBuilder setSqlParametersSetter(SqlParametersSetter sqlParametersSetter) {
    this.sqlParametersSetter = sqlParametersSetter;
    return this;
  }


  public SormConfigBuilder setMultiRowProcessorType(MultiRowProcessorType multiRowProcessorType) {
    this.multiRowProcessorType = multiRowProcessorType;
    return this;
  }


  public SormConfigBuilder setBatchSize(int size) {
    this.batchSize = size;
    return this;
  }


  public SormConfigBuilder setMultiRowSize(int size) {
    this.multiRowSize = size;
    return this;
  }


  public SormConfigBuilder setBatchSizeWithMultiRow(int size) {
    this.batchSizeWithMultiRow = size;
    return this;
  }


  public SormConfigBuilder setTransactionIsolationLevel(int level) {
    this.transactionIsolationLevel = level;
    return this;
  }


  public SormConfigBuilder setOption(String name, Object value) {
    this.options.put(name, value);
    return this;
  }


  public SormConfigBuilder setLoggerOnAll() {
    this.loggerConfigBuilder.onAll();
    return this;
  }

  public SormConfigBuilder setLoggerOffAll() {
    this.loggerConfigBuilder.offAll();
    return this;
  }

  public SormConfigBuilder setLoggerOn(LoggerConfig.Category... categories) {
    this.loggerConfigBuilder.on(categories);
    return this;
  }

  public SormConfigBuilder setLoggerOff(LoggerConfig.Category... categories) {
    this.loggerConfigBuilder.off(categories);
    return this;
  }

  public SormConfigBuilder setLoggerSupplier(Supplier<SormLogger> loggerSupplier) {
    this.loggerConfigBuilder.setLoggerSupplier(loggerSupplier);
    return this;
  }

}
