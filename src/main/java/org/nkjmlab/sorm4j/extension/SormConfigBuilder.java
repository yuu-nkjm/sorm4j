package org.nkjmlab.sorm4j.extension;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * A builder for configuration of or mapper.
 *
 * @author nkjm
 *
 */
@Experimental
public class SormConfigBuilder {

  /**
   * Type of how to execute multi-row update SQL statements.
   */
  public enum MultiRowProcessorType {
    SIMPLE_BATCH, MULTI_ROW, MULTI_ROW_AND_BATCH
  }

  public static final MultiRowProcessorType DEFAULT_MULTI_ROW_PROCESSOR =
      MultiRowProcessorType.MULTI_ROW;

  public static final SqlParametersSetter DEFAULT_SQL_PARAMETER_SETTER =
      new DefaultSqlParametersSetter();

  public static final ResultSetConverter DEFAULT_RESULT_SET_CONVERTER =
      new DefaultResultSetConverter();

  public static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();

  public static final ColumnFieldMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnFieldMapper();

  public static final int DEFAULT_TRANSACTION_ISOLATION_LEVEL =
      Connection.TRANSACTION_READ_COMMITTED;
  public static final String DEFAULT_CACHE_NAME = "DEFAULT_CACHE";

  private String cacheName = DEFAULT_CACHE_NAME;
  private ColumnFieldMapper columnFieldMapper = DEFAULT_COLUMN_FIELD_MAPPER;
  private TableNameMapper tableNameMapper = DEFAULT_TABLE_NAME_MAPPER;
  private ResultSetConverter resultSetConverter = DEFAULT_RESULT_SET_CONVERTER;
  private SqlParametersSetter sqlParametersSetter = DEFAULT_SQL_PARAMETER_SETTER;
  private MultiRowProcessorType multiRowProcessorType = DEFAULT_MULTI_ROW_PROCESSOR;
  private int batchSize = 32;
  private int multiRowSize = 32;
  private int batchSizeWithMultiRow = 5;
  private int transactionIsolationLevel = DEFAULT_TRANSACTION_ISOLATION_LEVEL;
  private Map<String, Object> options = new HashMap<>();


  public SormConfigBuilder() {}

  public SormConfig build() {
    return new SormConfig(cacheName, options, columnFieldMapper, tableNameMapper,
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


  public SormConfigBuilder setCacheName(String cacheName) {
    this.cacheName = cacheName;
    return this;
  }


}
