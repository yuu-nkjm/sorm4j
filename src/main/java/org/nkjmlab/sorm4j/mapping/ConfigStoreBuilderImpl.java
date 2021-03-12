package org.nkjmlab.sorm4j.mapping;

import org.nkjmlab.sorm4j.ConfigStoreBuilder;
import org.nkjmlab.sorm4j.mapping.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.mapping.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.mapping.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.mapping.extension.TableNameMapper;

/**
 * A builder for configuration of or mapper.
 *
 * @author nkjm
 *
 */
public class ConfigStoreBuilderImpl implements ConfigStoreBuilder {

  private final String configName;
  private ColumnFieldMapper columnFieldMapper = DEFAULT_COLUMN_FIELD_MAPPER;
  private TableNameMapper tableNameMapper = DEFAULT_TABLE_NAME_MAPPER;
  private ResultSetConverter resultSetConverter = DEFAULT_SQL_TO_JAVA_DATA_CONVERTER;
  private SqlParameterSetter sqlParameterSetter = DEFAULT_JAVA_TO_SQL_DATA_CONVERTER;
  private MultiRowProcessorType multiRowProcessorType = DEFAULT_MULTI_ROW_PROCESSOR;
  private int batchSize = 32;
  private int multiRowSize = 32;
  private int batchSizeWithMultiRow = 5;
  private int transactionIsolationLevel = DEFAULT_TRANSACTION_ISOLATION_LEVEL;

  public ConfigStoreBuilderImpl(String configName) {
    this.configName = configName;
  }

  public ConfigStoreBuilderImpl(String configName, ConfigStore configStore) {
    this(configName);
    this.columnFieldMapper = DEFAULT_COLUMN_FIELD_MAPPER;
    this.tableNameMapper = DEFAULT_TABLE_NAME_MAPPER;
    this.resultSetConverter = DEFAULT_SQL_TO_JAVA_DATA_CONVERTER;
    this.sqlParameterSetter = DEFAULT_JAVA_TO_SQL_DATA_CONVERTER;
    this.multiRowProcessorType = DEFAULT_MULTI_ROW_PROCESSOR;
    this.batchSize = 32;
    this.multiRowSize = 32;
    this.batchSizeWithMultiRow = 5;
    this.transactionIsolationLevel = DEFAULT_TRANSACTION_ISOLATION_LEVEL;

  }

  @Override
  public ConfigStore build() {
    return new ConfigStore(configName, columnFieldMapper, tableNameMapper, resultSetConverter,
        sqlParameterSetter, createMultiRowProcessorFactory(), transactionIsolationLevel);
  }

  private MultiRowProcessorGeneratorFactory createMultiRowProcessorFactory() {
    switch (multiRowProcessorType) {
      case SIMPLE_BATCH:
        return MultiRowProcessorGeneratorFactory.of(t -> new SimpleBatchProcessor<>(t, batchSize));
      case MULTI_ROW:
        return MultiRowProcessorGeneratorFactory
            .of(t -> new MultiRowInOneStatementProcessor<>(t, batchSize, multiRowSize));
      case MULTI_ROW_AND_BATCH:
        return MultiRowProcessorGeneratorFactory
            .of(t -> new BatchOfMultiRowInOneStatementProcessor<>(t, batchSize, multiRowSize,
                batchSizeWithMultiRow));
      default:
        return null;
    }
  }

  @Override
  public ConfigStoreBuilder setColumnFieldMapper(ColumnFieldMapper fieldNameMapper) {
    this.columnFieldMapper = fieldNameMapper;
    return this;
  }

  @Override
  public ConfigStoreBuilder setTableNameMapper(TableNameMapper tableNameMapper) {
    this.tableNameMapper = tableNameMapper;
    return this;
  }

  @Override
  public ConfigStoreBuilder setResultSetConverter(ResultSetConverter resultSetConverter) {
    this.resultSetConverter = resultSetConverter;
    return this;
  }

  @Override
  public ConfigStoreBuilder setSqlParameterSetter(SqlParameterSetter sqlParameterSetter) {
    this.sqlParameterSetter = sqlParameterSetter;
    return this;
  }

  @Override
  public ConfigStoreBuilder setMultiRowProcessorType(MultiRowProcessorType multiRowProcessorType) {
    this.multiRowProcessorType = multiRowProcessorType;
    return this;
  }

  @Override
  public ConfigStoreBuilder setBatchSize(int size) {
    this.batchSize = size;
    return this;
  }

  @Override
  public ConfigStoreBuilder setMultiRowSize(int size) {
    this.multiRowSize = size;
    return this;
  }

  @Override
  public ConfigStoreBuilder setBatchSizeWithMultiRow(int size) {
    this.batchSizeWithMultiRow = size;
    return this;
  }

  @Override
  public ConfigStoreBuilder setTransactionIsolationLevel(int level) {
    this.transactionIsolationLevel = level;
    return this;
  }

}
