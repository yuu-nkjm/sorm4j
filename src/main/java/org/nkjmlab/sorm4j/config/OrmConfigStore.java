package org.nkjmlab.sorm4j.config;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import org.nkjmlab.sorm4j.mapping.DefaultColumnFieldMapper;
import org.nkjmlab.sorm4j.mapping.DefaultPreparedStatementParametersSetter;
import org.nkjmlab.sorm4j.mapping.DefaultResultSetValueGetter;
import org.nkjmlab.sorm4j.mapping.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.mapping.MultiRowProcessor;
import org.nkjmlab.sorm4j.mapping.TableMapping;

public final class OrmConfigStore {

  // private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  private static final ConcurrentMap<String, OrmConfigStore> configStores =
      new ConcurrentHashMap<>();

  public static final String DEFAULT_CONFIG_NAME = "DEFAULT_CONFIG";
  public static final ColumnFieldMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnFieldMapper();
  public static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();
  public static final ResultSetValueGetter DEFAULT_SQL_TO_JAVA_DATA_CONVERTER =
      new DefaultResultSetValueGetter();
  public static final PreparedStatementParametersSetter DEFAULT_JAVA_TO_SQL_DATA_CONVERTER =
      new DefaultPreparedStatementParametersSetter();
  public static final MultiRowProcessorFactory DEFAULT_MULTI_ROW_PROCESSOR_FACTORY =
      new MultiRowProcessorFactory();

  private final String configName;
  private final ColumnFieldMapper columnFieldMapper;
  private final TableNameMapper tableNameMapper;
  private final ResultSetValueGetter resultSetValueGetter;
  private final PreparedStatementParametersSetter preparedStatementParametersSetter;
  private final MultiRowProcessorFactory multiProcessorFactory;

  public static final int DEFAULT_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;


  static {
    configStores.put(DEFAULT_CONFIG_NAME,
        new OrmConfigStore(DEFAULT_CONFIG_NAME, DEFAULT_COLUMN_FIELD_MAPPER,
            DEFAULT_TABLE_NAME_MAPPER, DEFAULT_SQL_TO_JAVA_DATA_CONVERTER,
            DEFAULT_JAVA_TO_SQL_DATA_CONVERTER, DEFAULT_MULTI_ROW_PROCESSOR_FACTORY));
  }

  OrmConfigStore(String cacheName, ColumnFieldMapper fieldNameMapper,
      TableNameMapper tableNameMapper, ResultSetValueGetter sqlToJavaConverter,
      PreparedStatementParametersSetter javaToSqlConverter, MultiRowProcessorFactory batchConfig) {
    this.configName = cacheName;
    this.columnFieldMapper = fieldNameMapper;
    this.tableNameMapper = tableNameMapper;
    this.resultSetValueGetter = sqlToJavaConverter;
    this.preparedStatementParametersSetter = javaToSqlConverter;
    this.multiProcessorFactory = batchConfig;
  }

  public String getConfigName() {
    return configName;
  }

  public ColumnFieldMapper getColumnFieldMapper() {
    return columnFieldMapper;
  }

  public ResultSetValueGetter getSqlToJavaDataConverter() {
    return resultSetValueGetter;
  }


  public TableNameMapper getTableNameMapper() {
    return tableNameMapper;
  }

  public MultiRowProcessorFactory getMultiProcessorFactory() {
    return multiProcessorFactory;
  }

  public PreparedStatementParametersSetter getJavaToSqlDataConverter() {
    return preparedStatementParametersSetter;
  }


  public static class Builder {

    private final String configName;
    private ColumnFieldMapper columnFieldMapper = DEFAULT_COLUMN_FIELD_MAPPER;
    private TableNameMapper tableNameMapper = DEFAULT_TABLE_NAME_MAPPER;
    private ResultSetValueGetter resultSetValueGetter = DEFAULT_SQL_TO_JAVA_DATA_CONVERTER;
    private PreparedStatementParametersSetter preparedStatementParametersSetter =
        DEFAULT_JAVA_TO_SQL_DATA_CONVERTER;
    private MultiRowProcessorFactory multiRowProcessorFactory = DEFAULT_MULTI_ROW_PROCESSOR_FACTORY;

    public Builder(String configName) {
      this.configName = configName;
    }

    public OrmConfigStore build() {
      return new OrmConfigStore(configName, columnFieldMapper, tableNameMapper,
          resultSetValueGetter, preparedStatementParametersSetter, multiRowProcessorFactory);
    }


    public Builder setColumnFieldMapper(ColumnFieldMapper fieldNameMapper) {
      this.columnFieldMapper = fieldNameMapper;
      return this;
    }

    public Builder setTableNameMapper(TableNameMapper tableNameMapper) {
      this.tableNameMapper = tableNameMapper;
      return this;
    }

    public Builder setSqlToJavaDataConverter(ResultSetValueGetter resultSetValueGetter) {
      this.resultSetValueGetter = resultSetValueGetter;
      return this;
    }

    public Builder setJavaToSqlDataConverter(
        PreparedStatementParametersSetter preparedStatementParametersSetter) {
      this.preparedStatementParametersSetter = preparedStatementParametersSetter;
      return this;
    }

    public Builder setMultiRowProcessorFactory(
        Function<TableMapping<?>, MultiRowProcessor<?>> func) {
      this.multiRowProcessorFactory = MultiRowProcessorFactory.of(func);
      return this;
    }

    public Builder setMultiRowProcessorFactory(MultiRowProcessorFactory multiRowProcessorFactory) {
      this.multiRowProcessorFactory = multiRowProcessorFactory;
      return this;
    }
  }


  public static OrmConfigStore put(OrmConfigStore newConfigStore) {
    return configStores.put(newConfigStore.getConfigName(), newConfigStore);
  }

  public static OrmConfigStore get(String key) {
    return configStores.get(key);
  }

  public static OrmConfigStore getDefaultConfig() {
    return get(DEFAULT_CONFIG_NAME);
  }

}
