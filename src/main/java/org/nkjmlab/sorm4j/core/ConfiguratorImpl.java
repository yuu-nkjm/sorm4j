package org.nkjmlab.sorm4j.core;

import static org.nkjmlab.sorm4j.Configurator.MultiRowProcessorType.*;
import java.sql.Connection;
import org.nkjmlab.sorm4j.Configurator;
import org.nkjmlab.sorm4j.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.DefaultColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.DefaultResultSetConverter;
import org.nkjmlab.sorm4j.extension.DefaultSqlParameterSetter;
import org.nkjmlab.sorm4j.extension.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.extension.TableNameMapper;

/**
 * A builder for configuration of or mapper.
 *
 * @author nkjm
 *
 */
public class ConfiguratorImpl implements Configurator {

  public static final int DEFAULT_TRANSACTION_ISOLATION_LEVEL =
      Connection.TRANSACTION_READ_COMMITTED;

  public static final MultiRowProcessorType DEFAULT_MULTI_ROW_PROCESSOR = MULTI_ROW;

  public static final SqlParameterSetter DEFAULT_JAVA_TO_SQL_DATA_CONVERTER =
      new DefaultSqlParameterSetter();

  public static final ResultSetConverter DEFAULT_SQL_TO_JAVA_DATA_CONVERTER =
      new DefaultResultSetConverter();

  public static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();

  public static final ColumnFieldMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnFieldMapper();

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

  public ConfiguratorImpl(String configName) {
    this.configName = configName;
  }

  public ConfiguratorImpl(String configName, ConfigStore configStore) {
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
  public Configurator setSqlParameterSetter(SqlParameterSetter sqlParameterSetter) {
    this.sqlParameterSetter = sqlParameterSetter;
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

}
