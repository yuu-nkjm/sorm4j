package org.nkjmlab.sorm4j.core.mapping;

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

  public static final SqlParameterSetter DEFAULT_SQL_PARAMETER_SETTER =
      new DefaultSqlParameterSetter();

  public static final ResultSetConverter DEFAULT_RESULT_SET_CONVERTER =
      new DefaultResultSetConverter();

  public static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();

  public static final ColumnFieldMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnFieldMapper();

  private final String configName;
  private ColumnFieldMapper columnFieldMapper = DEFAULT_COLUMN_FIELD_MAPPER;
  private TableNameMapper tableNameMapper = DEFAULT_TABLE_NAME_MAPPER;
  private ResultSetConverter resultSetConverter = DEFAULT_RESULT_SET_CONVERTER;
  private SqlParameterSetter sqlParameterSetter = DEFAULT_SQL_PARAMETER_SETTER;
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
    this.columnFieldMapper = configStore.getColumnFieldMapper();
    this.tableNameMapper = configStore.getTableNameMapper();
    this.resultSetConverter = configStore.getResultSetConverter();
    this.sqlParameterSetter = configStore.getSqlParameterSetter();
    this.multiRowProcessorType = configStore.getMultiRowProcessorType();
    this.batchSize = configStore.getBatchSize();
    this.multiRowSize = configStore.getMultiRowSize();
    this.batchSizeWithMultiRow = configStore.getBatchSizeWithMultiRow();
    this.transactionIsolationLevel = configStore.getTransactionIsolationLevel();

  }

  public ConfigStore build() {
    return new ConfigStore(configName, columnFieldMapper, tableNameMapper, resultSetConverter,
        sqlParameterSetter, multiRowProcessorType, batchSize, multiRowSize, batchSizeWithMultiRow,
        transactionIsolationLevel);
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
