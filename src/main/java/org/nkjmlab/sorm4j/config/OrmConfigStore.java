package org.nkjmlab.sorm4j.config;

import java.sql.Connection;

public final class OrmConfigStore {
  public static final String DEFAULT_CACHE = "DEFAULT_CACHE";

  public static final ColumnFieldMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnFieldMapper();
  public static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameGuesser();
  public static final SqlToJavaDataConverter DEFAULT_SQL_TO_JAVA_DATA_CONVERTER =
      new DefaultSqlToJavaDataConverter();
  public static final JavaToSqlDataConverter DEFAULT_JAVA_TO_SQL_DATA_CONVERTER =
      new DefaultJavaToSqlDataConverter();
  public static final MultiRowProcessorFactory DEFAULT_BATCH_CONFIG = new MultiRowProcessorFactory();

  private final String cacheName;
  private final ColumnFieldMapper fieldNameMapper;
  private final TableNameMapper tableNameMapper;
  private final SqlToJavaDataConverter sqlToJavaDataConverter;
  private final JavaToSqlDataConverter javaToSqlDataConverter;
  private final MultiRowProcessorFactory batchConfig;

  // private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();
  public static final int DEFAULT_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;


  public static final OrmConfigStore DEFAULT_CONFIGURATIONS = new OrmConfigStore();


  public OrmConfigStore() {
    this(DEFAULT_CACHE, DEFAULT_COLUMN_FIELD_MAPPER, DEFAULT_TABLE_NAME_MAPPER,
        DEFAULT_SQL_TO_JAVA_DATA_CONVERTER, DEFAULT_JAVA_TO_SQL_DATA_CONVERTER,
        DEFAULT_BATCH_CONFIG);
  }

  public OrmConfigStore(String cacheName, ColumnFieldMapper fieldNameMapper,
      TableNameMapper tableNameMapper, SqlToJavaDataConverter sqlToJavaConverter,
      JavaToSqlDataConverter javaToSqlConverter, MultiRowProcessorFactory batchConfig) {
    this.cacheName = cacheName;
    this.fieldNameMapper = fieldNameMapper;
    this.tableNameMapper = tableNameMapper;
    this.sqlToJavaDataConverter = sqlToJavaConverter;
    this.javaToSqlDataConverter = javaToSqlConverter;
    this.batchConfig = batchConfig;

  }


  public String getCacheName() {
    return cacheName;
  }


  public ColumnFieldMapper getFieldNameMapper() {
    return fieldNameMapper;
  }


  public SqlToJavaDataConverter getSqlToJavaDataConverter() {
    return sqlToJavaDataConverter;
  }


  public TableNameMapper getTableNameMapper() {
    return tableNameMapper;
  }

  public MultiRowProcessorFactory getBatchConfig() {
    return batchConfig;
  }

  public JavaToSqlDataConverter getJavaToSqlDataConverter() {
    return javaToSqlDataConverter;
  }


  public static class Builder {

    private String cacheName = DEFAULT_CACHE;
    private ColumnFieldMapper fieldNameMapper = DEFAULT_COLUMN_FIELD_MAPPER;
    private TableNameMapper tableNameMapper = DEFAULT_TABLE_NAME_MAPPER;
    private SqlToJavaDataConverter sqlToJavaDataConverter = DEFAULT_SQL_TO_JAVA_DATA_CONVERTER;
    private JavaToSqlDataConverter javaToSqlDataConverter = DEFAULT_JAVA_TO_SQL_DATA_CONVERTER;
    private MultiRowProcessorFactory batchConfig = DEFAULT_BATCH_CONFIG;

    public OrmConfigStore build() {
      return new OrmConfigStore(cacheName, fieldNameMapper, tableNameMapper, sqlToJavaDataConverter,
          javaToSqlDataConverter, batchConfig);
    }

    public void setCacheName(String cacheName) {
      this.cacheName = cacheName;
    }

    public void setFieldNameMapper(ColumnFieldMapper fieldNameMapper) {
      this.fieldNameMapper = fieldNameMapper;
    }

    public void setTableNameMapper(TableNameMapper tableNameMapper) {
      this.tableNameMapper = tableNameMapper;
    }

    public void setSqlToJavaDataConverter(SqlToJavaDataConverter sqlToJavaDataConverter) {
      this.sqlToJavaDataConverter = sqlToJavaDataConverter;
    }

    public void setJavaToSqlDataConverter(JavaToSqlDataConverter javaToSqlDataConverter) {
      this.javaToSqlDataConverter = javaToSqlDataConverter;
    }

    public void setBatchConfig(MultiRowProcessorFactory batchConfig) {
      this.batchConfig = batchConfig;
    }

  }



}
