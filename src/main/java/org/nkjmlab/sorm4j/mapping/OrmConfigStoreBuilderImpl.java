package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.OrmConfigStoreBuilder.MultiRowProcessorType.*;
import org.nkjmlab.sorm4j.OrmConfigStoreBuilder;
import org.nkjmlab.sorm4j.mapping.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.mapping.extension.DefaultColumnFieldMapper;
import org.nkjmlab.sorm4j.mapping.extension.DefaultResultSetConverter;
import org.nkjmlab.sorm4j.mapping.extension.DefaultSqlParameterSetter;
import org.nkjmlab.sorm4j.mapping.extension.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.mapping.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.mapping.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.mapping.extension.TableNameMapper;

public class OrmConfigStoreBuilderImpl implements OrmConfigStoreBuilder {

  public static final ColumnFieldMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnFieldMapper();
  public static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();
  public static final ResultSetConverter DEFAULT_SQL_TO_JAVA_DATA_CONVERTER =
      new DefaultResultSetConverter();
  public static final SqlParameterSetter DEFAULT_JAVA_TO_SQL_DATA_CONVERTER =
      new DefaultSqlParameterSetter();
  public static final MultiRowProcessorType DEFAULT_MULTI_ROW_PROCESSOR = MULTI_ROW;

  private final String configName;
  private ColumnFieldMapper columnFieldMapper = DEFAULT_COLUMN_FIELD_MAPPER;
  private TableNameMapper tableNameMapper = DEFAULT_TABLE_NAME_MAPPER;
  private ResultSetConverter resultSetConverter = DEFAULT_SQL_TO_JAVA_DATA_CONVERTER;
  private SqlParameterSetter sqlParameterSetter = DEFAULT_JAVA_TO_SQL_DATA_CONVERTER;
  private MultiRowProcessorType multiRowProcessorType = DEFAULT_MULTI_ROW_PROCESSOR;
  private int batchSize = 32;
  private int multiRowSize = 32;
  private int batchSizeWithMultiRow = 5;

  public OrmConfigStoreBuilderImpl(String configName) {
    this.configName = configName;
  }

  @Override
  public OrmConfigStore build() {
    return new OrmConfigStore(configName, columnFieldMapper, tableNameMapper, resultSetConverter,
        sqlParameterSetter, createMultiRowProcessorFactory());
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
  public OrmConfigStoreBuilder setColumnFieldMapper(ColumnFieldMapper fieldNameMapper) {
    this.columnFieldMapper = fieldNameMapper;
    return this;
  }

  @Override
  public OrmConfigStoreBuilder setTableNameMapper(TableNameMapper tableNameMapper) {
    this.tableNameMapper = tableNameMapper;
    return this;
  }

  @Override
  public OrmConfigStoreBuilder setResultSetConverter(ResultSetConverter resultSetConverter) {
    this.resultSetConverter = resultSetConverter;
    return this;
  }

  @Override
  public OrmConfigStoreBuilder setSqlParameterSetter(SqlParameterSetter sqlParameterSetter) {
    this.sqlParameterSetter = sqlParameterSetter;
    return this;
  }

  @Override
  public OrmConfigStoreBuilder setMultiRowProcessorType(
      MultiRowProcessorType multiRowProcessorType) {
    this.multiRowProcessorType = multiRowProcessorType;
    return this;
  }

  @Override
  public OrmConfigStoreBuilder setBatchSize(int size) {
    this.batchSize = size;
    return this;
  }

  @Override
  public OrmConfigStoreBuilder setMultiRowSize(int size) {
    this.multiRowSize = size;
    return this;
  }

  @Override
  public OrmConfigStoreBuilder setBatchSizeWithMultiRow(int size) {
    this.batchSizeWithMultiRow = size;
    return this;
  }
}
