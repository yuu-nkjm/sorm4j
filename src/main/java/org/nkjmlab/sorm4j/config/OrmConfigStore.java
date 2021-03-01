package org.nkjmlab.sorm4j.config;

import java.sql.Connection;
import org.nkjmlab.sorm4j.mapping.DefaultColumnFieldMapper;
import org.nkjmlab.sorm4j.mapping.DefaultPreparedStatementParametersSetter;
import org.nkjmlab.sorm4j.mapping.DefaultResultSetValueGetter;
import org.nkjmlab.sorm4j.mapping.DefaultTableNameGuesser;

public final class OrmConfigStore {

  public static final String DEFAULT_CACHE = "DEFAULT_CACHE";
  public static final ColumnFieldMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnFieldMapper();
  public static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameGuesser();
  public static final ResultSetValueGetter DEFAULT_SQL_TO_JAVA_DATA_CONVERTER =
      new DefaultResultSetValueGetter();
  public static final PreparedStatementParametersSetter DEFAULT_JAVA_TO_SQL_DATA_CONVERTER =
      new DefaultPreparedStatementParametersSetter();
  public static final MultiRowProcessorFactory DEFAULT_MULTI_ROW_PROCESSOR_FACTORY =
      new MultiRowProcessorFactory();

  private final String cacheName;
  private final ColumnFieldMapper columnFieldMapper;
  private final TableNameMapper tableNameMapper;
  private final ResultSetValueGetter resultSetValueGetter;
  private final PreparedStatementParametersSetter preparedStatementParametersSetter;
  private final MultiRowProcessorFactory multiProcessorFactory;

  // private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();
  public static final int DEFAULT_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;


  public static final OrmConfigStore DEFAULT_CONFIGURATIONS = new OrmConfigStore();


  public OrmConfigStore() {
    this(DEFAULT_CACHE, DEFAULT_COLUMN_FIELD_MAPPER, DEFAULT_TABLE_NAME_MAPPER,
        DEFAULT_SQL_TO_JAVA_DATA_CONVERTER, DEFAULT_JAVA_TO_SQL_DATA_CONVERTER,
        DEFAULT_MULTI_ROW_PROCESSOR_FACTORY);
  }

  public OrmConfigStore(String cacheName, ColumnFieldMapper fieldNameMapper,
      TableNameMapper tableNameMapper, ResultSetValueGetter sqlToJavaConverter,
      PreparedStatementParametersSetter javaToSqlConverter, MultiRowProcessorFactory batchConfig) {
    this.cacheName = cacheName;
    this.columnFieldMapper = fieldNameMapper;
    this.tableNameMapper = tableNameMapper;
    this.resultSetValueGetter = sqlToJavaConverter;
    this.preparedStatementParametersSetter = javaToSqlConverter;
    this.multiProcessorFactory = batchConfig;
  }

  public String getCacheName() {
    return cacheName;
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

    private String cacheName = DEFAULT_CACHE;
    private ColumnFieldMapper columnFieldMapper = DEFAULT_COLUMN_FIELD_MAPPER;
    private TableNameMapper tableNameMapper = DEFAULT_TABLE_NAME_MAPPER;
    private ResultSetValueGetter resultSetValueGetter = DEFAULT_SQL_TO_JAVA_DATA_CONVERTER;
    private PreparedStatementParametersSetter preparedStatementParametersSetter =
        DEFAULT_JAVA_TO_SQL_DATA_CONVERTER;
    private MultiRowProcessorFactory multiRowProcessorFactory = DEFAULT_MULTI_ROW_PROCESSOR_FACTORY;

    public OrmConfigStore build() {
      return new OrmConfigStore(cacheName, columnFieldMapper, tableNameMapper, resultSetValueGetter,
          preparedStatementParametersSetter, multiRowProcessorFactory);
    }

    public Builder setCacheName(String cacheName) {
      this.cacheName = cacheName;
      return this;
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

    public Builder setMultiRowProcessorFactory(MultiRowProcessorFactory batchConfig) {
      this.multiRowProcessorFactory = batchConfig;
      return this;
    }
  }

}
