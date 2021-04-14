package org.nkjmlab.sorm4j.internal.mapping;

import java.util.HashMap;
import java.util.Map;
import org.nkjmlab.sorm4j.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.Configurator;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.extension.TableNameMapper;

/**
 * A builder for configuration of or mapper.
 *
 * @author nkjm
 *
 */
public class ConfiguratorImpl implements Configurator {

  private final String configName;
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

  public ConfiguratorImpl(String configName) {
    this.configName = configName;
  }

  public ConfiguratorImpl(String configName, ConfigStore configStore) {
    this(configName);
    this.columnFieldMapper = configStore.getColumnFieldMapper();
    this.tableNameMapper = configStore.getTableNameMapper();
    this.resultSetConverter = configStore.getResultSetConverter();
    this.sqlParametersSetter = configStore.getSqlParametersSetter();
    this.multiRowProcessorType = configStore.getMultiRowProcessorType();
    this.batchSize = configStore.getBatchSize();
    this.multiRowSize = configStore.getMultiRowSize();
    this.batchSizeWithMultiRow = configStore.getBatchSizeWithMultiRow();
    this.transactionIsolationLevel = configStore.getTransactionIsolationLevel();
    this.options = configStore.getOptions();

  }

  public ConfigStore build() {
    return new ConfigStore(configName, options, columnFieldMapper, tableNameMapper,
        resultSetConverter, sqlParametersSetter, multiRowProcessorType, batchSize, multiRowSize,
        batchSizeWithMultiRow, transactionIsolationLevel);
  }


  @Override
  public Configurator setColumnFieldMapper(ColumnFieldMapper fieldNameMapper) {
    this.columnFieldMapper = fieldNameMapper;
    return this;
  }

  @Override
  public Configurator setTableNameMapper(TableNameMapper tableNameMapper) {
    this.tableNameMapper = tableNameMapper;
    return this;
  }

  @Override
  public Configurator setResultSetConverter(ResultSetConverter resultSetConverter) {
    this.resultSetConverter = resultSetConverter;
    return this;
  }

  @Override
  public Configurator setSqlParametersSetter(SqlParametersSetter sqlParametersSetter) {
    this.sqlParametersSetter = sqlParametersSetter;
    return this;
  }

  @Override
  public Configurator setMultiRowProcessorType(MultiRowProcessorType multiRowProcessorType) {
    this.multiRowProcessorType = multiRowProcessorType;
    return this;
  }

  @Override
  public Configurator setBatchSize(int size) {
    this.batchSize = size;
    return this;
  }

  @Override
  public Configurator setMultiRowSize(int size) {
    this.multiRowSize = size;
    return this;
  }

  @Override
  public Configurator setBatchSizeWithMultiRow(int size) {
    this.batchSizeWithMultiRow = size;
    return this;
  }

  @Override
  public Configurator setTransactionIsolationLevel(int level) {
    this.transactionIsolationLevel = level;
    return this;
  }

  @Override
  public Configurator setOption(String name, Object value) {
    this.options.put(name, value);
    return this;
  }

}
