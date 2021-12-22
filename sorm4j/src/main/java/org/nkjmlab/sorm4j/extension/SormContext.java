package org.nkjmlab.sorm4j.extension;

import static org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils.*;
import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.extension.impl.DefaultColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.impl.DefaultResultSetConverter;
import org.nkjmlab.sorm4j.extension.impl.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.extension.impl.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.extension.impl.DefaultTableSqlFactory;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext.Category;
import org.nkjmlab.sorm4j.extension.logger.SormLogger;
import org.nkjmlab.sorm4j.internal.SormOptionsImpl;
import org.nkjmlab.sorm4j.internal.mapping.ColumnToAccessorMap;
import org.nkjmlab.sorm4j.internal.mapping.ColumnsMapping;
import org.nkjmlab.sorm4j.internal.mapping.TableMapping;
import org.nkjmlab.sorm4j.internal.mapping.TableMetaDataImpl;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.schema.TableMetaData;

/**
 * A context for sorm.
 *
 * @author nkjm
 *
 */
@Experimental
public final class SormContext {

  private final ConcurrentMap<String, TableMapping<?>> tableMappings;
  private final ConcurrentMap<String, TableMetaData> tableMetaDataMap;
  private final ConcurrentMap<Class<?>, ColumnsMapping<?>> columnsMappings;
  private final ConcurrentMap<Class<?>, TableName> classNameToValidTableNameMap;
  private final ConcurrentMap<String, TableName> tableNameToValidTableNameMap;
  private final SormConfig sormConfig;


  private SormContext(SormConfig sormConfig) {
    this.sormConfig = sormConfig;
    this.tableMappings = new ConcurrentHashMap<>();
    this.tableMetaDataMap = new ConcurrentHashMap<>();
    this.columnsMappings = new ConcurrentHashMap<>();
    this.classNameToValidTableNameMap = new ConcurrentHashMap<>();
    this.tableNameToValidTableNameMap = new ConcurrentHashMap<>();
  }


  public TableMetaData getTableMetaData(Connection connection, String tableName) {
    return getTableMetaData(connection, toTableName(connection, tableName));
  }

  private TableMetaData getTableMetaData(Connection connection, TableName tableName) {
    TableMetaData ret =
        tableMetaDataMap.computeIfAbsent(tableName.getName(), Try.createFunctionWithThrow(_key -> {
          TableMetaData m =
              createTableMetaData(Object.class, tableName.getName(), connection.getMetaData());
          return m;
        }, Try::rethrow));
    return ret;
  }

  public <T> TableMapping<T> getTableMapping(Connection connection, Class<T> objectClass) {
    TableName tableName = toTableName(connection, objectClass);
    return getTableMapping(connection, tableName, objectClass);
  }

  /**
   * Get table mapping by the table name and the object class. When there is no mapping, the method
   * create a mapping and register it.
   *
   */
  public <T> TableMapping<T> getTableMapping(Connection connection, String tableName,
      Class<T> objectClass) {
    return getTableMapping(connection, toTableName(connection, tableName), objectClass);
  }

  <T> TableMapping<T> getTableMapping(Connection connection, TableName tableName,
      Class<T> objectClass) {
    String key = tableName.getName() + "-" + objectClass.getName();
    @SuppressWarnings("unchecked")
    TableMapping<T> ret =
        (TableMapping<T>) tableMappings.computeIfAbsent(key, Try.createFunctionWithThrow(_key -> {
          TableMapping<T> m = createTableMapping(objectClass, tableName.getName(), connection);
          sormConfig.getLoggerContext().createLogPoint(Category.MAPPING)
              .ifPresent(lp -> lp.logMapping(m.getFormattedString()));
          return m;
        }, Try::rethrow));
    return ret;
  }


  public <T> ColumnsMapping<T> createColumnsMapping(Class<T> objectClass) {
    Map<String, Accessor> accessors =
        sormConfig.getColumnFieldMapper().createAccessors(objectClass);
    String aliasPrefix = sormConfig.getColumnFieldMapper().getColumnAliasPrefix(objectClass);
    Map<String, Accessor> aliasAccessors =
        sormConfig.getColumnFieldMapper().createAliasAccessors(aliasPrefix, accessors);
    ColumnToAccessorMap columnToAccessorMap =
        new ColumnToAccessorMap(objectClass, accessors, aliasPrefix, aliasAccessors);

    return new ColumnsMapping<>(sormConfig.getOptions(), sormConfig.getResultSetConverter(),
        objectClass, columnToAccessorMap);
  }

  public <T> TableMapping<T> createTableMapping(Class<T> objectClass, String tableName,
      Connection connection) throws SQLException {

    DatabaseMetaData metaData = connection.getMetaData();
    TableMetaData tableMetaData = createTableMetaData(objectClass, tableName, metaData);
    List<String> columns = tableMetaData.getColumns();


    TableSql sql = sormConfig.getTableSqlFactory().create(tableMetaData, objectClass, connection);

    Map<String, Accessor> accessors =
        sormConfig.getColumnFieldMapper().createAccessors(objectClass, columns);

    Set<String> keySetWithoutAlias = accessors.keySet();
    if (!equalsAsCanonical(columns, keySetWithoutAlias)) {
      throw new SormException(newString(
          "{} does not match any field. Table [{}] contains Columns {} but [{}] contains Fields {}.",
          columns.stream().filter(e -> !keySetWithoutAlias.contains(toCanonicalCase(e))).sorted()
              .collect(Collectors.toList()),
          tableName,
          tableMetaData.getColumns().stream().map(c -> c.toString()).sorted()
              .collect(Collectors.toList()),
          objectClass.getName(),
          keySetWithoutAlias.stream().sorted().collect(Collectors.toList())));
    }
    Map<String, Accessor> aliasAccessors = sormConfig.getColumnFieldMapper()
        .createAliasAccessors(tableMetaData.getColumnAliasPrefix(), accessors);

    ColumnToAccessorMap columnToAccessorMap = new ColumnToAccessorMap(objectClass, accessors,
        tableMetaData.getColumnAliasPrefix(), aliasAccessors);

    return new TableMapping<>(sormConfig.getLoggerContext(), sormConfig.getOptions(),
        sormConfig.getResultSetConverter(), sormConfig.getSqlParametersSetter(),
        sormConfig.getMultiRowProcessorFactory(), objectClass, columnToAccessorMap, tableMetaData,
        sql);
  }


  private <T> TableMetaData createTableMetaData(Class<T> objectClass, String tableName,
      DatabaseMetaData metaData) throws SQLException {
    List<ColumnNameWithMetaData> columns =
        sormConfig.getColumnFieldMapper().getColumns(metaData, tableName);

    List<String> primaryKeys = sormConfig.getColumnFieldMapper().getPrimaryKeys(metaData, tableName)
        .stream().map(c -> c.getName()).collect(Collectors.toList());

    List<String> autoGeneratedColumns =
        sormConfig.getColumnFieldMapper().getAutoGeneratedColumns(metaData, tableName).stream()
            .map(c -> c.getName()).collect(Collectors.toList());

    String columnAliasPrefix = sormConfig.getColumnFieldMapper().getColumnAliasPrefix(objectClass);
    return new TableMetaDataImpl(tableName, columnAliasPrefix, columns, primaryKeys,
        autoGeneratedColumns);
  }

  @SuppressWarnings("unchecked")
  public <T> TableMapping<T> getCastedTableMapping(Connection connection, Class<?> objectClass) {
    return (TableMapping<T>) getTableMapping(connection, objectClass);
  }


  @SuppressWarnings("unchecked")
  public <T> TableMapping<T> getCastedTableMapping(Connection connection, String tableName,
      Class<?> objectClass) {
    return (TableMapping<T>) getTableMapping(connection, tableName, objectClass);
  }

  public <T> ColumnsMapping<T> getColumnsMapping(Class<T> objectClass) {
    @SuppressWarnings("unchecked")
    ColumnsMapping<T> ret = (ColumnsMapping<T>) columnsMappings.computeIfAbsent(objectClass, _k -> {
      ColumnsMapping<T> m = createColumnsMapping(objectClass);
      sormConfig.getLoggerContext().createLogPoint(Category.MAPPING)
          .ifPresent(lp -> lp.logMapping(m.getFormattedString()));

      return m;
    });
    return ret;
  }

  private TableName toTableName(Connection connection, Class<?> objectClass) {
    return classNameToValidTableNameMap.computeIfAbsent(objectClass, Try.createFunctionWithThrow(
        k -> sormConfig.getTableNameMapper().getTableName(objectClass, connection.getMetaData()),
        Try::rethrow));
  }

  public String getTableName(Connection connection, Class<?> objectClass) {
    return toTableName(connection, objectClass).getName();
  }

  private TableName toTableName(Connection connection, String tableName) {
    return tableNameToValidTableNameMap.computeIfAbsent(tableName,
        Try.createFunctionWithThrow(
            k -> sormConfig.getTableNameMapper().getTableName(tableName, connection.getMetaData()),
            Try::rethrow));
  }


  public LoggerContext getLoggerContext() {
    return sormConfig.getLoggerContext();
  }


  public int getTransactionIsolationLevel() {
    return sormConfig.getTransactionIsolationLevel();
  }


  public SormOptions getOptions() {
    return sormConfig.getOptions();
  }


  public ResultSetConverter getResultSetConverter() {
    return sormConfig.getResultSetConverter();
  }


  public SqlParametersSetter getSqlParametersSetter() {
    return sormConfig.getSqlParametersSetter();
  }


  @Override
  public String toString() {
    return "SormContext [tableMappings=" + tableMappings + ", columnsMappings=" + columnsMappings
        + ", classNameToValidTableNameMap=" + classNameToValidTableNameMap
        + ", tableNameToValidTableNameMap=" + tableNameToValidTableNameMap + ", sormConfig="
        + sormConfig + "]";
  }

  public static Builder builder() {
    return new Builder();
  }

  @Experimental
  public static class Builder {

    private static final MultiRowProcessorType DEFAULT_MULTI_ROW_PROCESSOR =
        MultiRowProcessorType.MULTI_ROW;

    private static final SqlParametersSetter DEFAULT_SQL_PARAMETER_SETTER =
        new DefaultSqlParametersSetter();

    private static final ResultSetConverter DEFAULT_RESULT_SET_CONVERTER =
        new DefaultResultSetConverter();

    private static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();

    private static final TableSqlFactory DEFAULT_TABLE_SQL_FACTORY = new DefaultTableSqlFactory();

    private static final int DEFAULT_TRANSACTION_ISOLATION_LEVEL =
        Connection.TRANSACTION_READ_COMMITTED;

    private TableNameMapper tableNameMapper = DEFAULT_TABLE_NAME_MAPPER;
    private ResultSetConverter resultSetConverter = DEFAULT_RESULT_SET_CONVERTER;
    private SqlParametersSetter sqlParametersSetter = DEFAULT_SQL_PARAMETER_SETTER;
    private MultiRowProcessorType multiRowProcessorType = DEFAULT_MULTI_ROW_PROCESSOR;
    private TableSqlFactory tableSqlFactory = DEFAULT_TABLE_SQL_FACTORY;

    private ColumnFieldMapper columnFieldMapper;
    private int batchSize = 32;
    private int multiRowSize = 32;
    private int batchSizeWithMultiRow = 5;
    private int transactionIsolationLevel = DEFAULT_TRANSACTION_ISOLATION_LEVEL;
    private Map<String, Object> options = new HashMap<>();
    private LoggerContext.Builder loggerConfigBuilder = LoggerContext.builder();



    private Builder() {}

    public SormContext build() {
      LoggerContext loggerContext = loggerConfigBuilder.build();
      columnFieldMapper = columnFieldMapper != null ? columnFieldMapper
          : new DefaultColumnFieldMapper(loggerContext);
      return new SormContext(
          new SormConfig(loggerContext, options, columnFieldMapper, tableNameMapper,
              resultSetConverter, sqlParametersSetter, tableSqlFactory, multiRowProcessorType,
              batchSize, multiRowSize, batchSizeWithMultiRow, transactionIsolationLevel));
    }


    public Builder setColumnFieldMapper(ColumnFieldMapper fieldNameMapper) {
      this.columnFieldMapper = fieldNameMapper;
      return this;
    }


    public Builder setTableNameMapper(TableNameMapper tableNameMapper) {
      this.tableNameMapper = tableNameMapper;
      return this;
    }


    public Builder setResultSetConverter(ResultSetConverter resultSetConverter) {
      this.resultSetConverter = resultSetConverter;
      return this;
    }


    public Builder setSqlParametersSetter(SqlParametersSetter sqlParametersSetter) {
      this.sqlParametersSetter = sqlParametersSetter;
      return this;
    }

    public Builder setTableSqlFactory(TableSqlFactory tableSqlFactory) {
      this.tableSqlFactory = tableSqlFactory;
      return this;
    }


    public Builder setMultiRowProcessorType(MultiRowProcessorType multiRowProcessorType) {
      this.multiRowProcessorType = multiRowProcessorType;
      return this;
    }


    public Builder setBatchSize(int size) {
      this.batchSize = size;
      return this;
    }


    public Builder setMultiRowSize(int size) {
      this.multiRowSize = size;
      return this;
    }


    public Builder setBatchSizeWithMultiRow(int size) {
      this.batchSizeWithMultiRow = size;
      return this;
    }


    public Builder setTransactionIsolationLevel(int level) {
      this.transactionIsolationLevel = level;
      return this;
    }


    public Builder setOption(String name, Object value) {
      this.options.put(name, value);
      return this;
    }


    public Builder setLoggerOnAll() {
      this.loggerConfigBuilder.onAll();
      return this;
    }

    public Builder setLoggerOffAll() {
      this.loggerConfigBuilder.offAll();
      return this;
    }

    public Builder setLoggerOn(LoggerContext.Category... categories) {
      this.loggerConfigBuilder.on(categories);
      return this;
    }

    public Builder setLoggerOff(LoggerContext.Category... categories) {
      this.loggerConfigBuilder.off(categories);
      return this;
    }

    public Builder setLoggerSupplier(Supplier<SormLogger> loggerSupplier) {
      this.loggerConfigBuilder.setLoggerSupplier(loggerSupplier);
      return this;
    }

  }



  public static final class SormConfig {

    private final TableNameMapper tableNameMapper;
    private final ColumnFieldMapper columnFieldMapper;
    private final MultiRowProcessorFactory multiRowProcessorFactory;
    private final ResultSetConverter resultSetConverter;
    private final SqlParametersSetter sqlParametersSetter;
    private final SormOptions options;
    private final int transactionIsolationLevel;
    private final LoggerContext loggerContext;
    private final TableSqlFactory tableSqlFactory;

    public SormConfig(LoggerContext loggerContext, Map<String, Object> options,
        ColumnFieldMapper columnFieldMapper, TableNameMapper tableNameMapper,
        ResultSetConverter resultSetConverter, SqlParametersSetter sqlParametersSetter,
        TableSqlFactory tableSqlFactory, MultiRowProcessorType multiRowProcessorType, int batchSize,
        int multiRowSize, int batchSizeWithMultiRow, int transactionIsolationLevel) {
      this.loggerContext = loggerContext;
      this.options = new SormOptionsImpl(options);
      this.transactionIsolationLevel = transactionIsolationLevel;
      this.tableNameMapper = tableNameMapper;
      this.columnFieldMapper = columnFieldMapper;
      this.multiRowProcessorFactory = MultiRowProcessorFactory.createMultiRowProcessorFactory(
          loggerContext, this.options, sqlParametersSetter, multiRowProcessorType, batchSize,
          multiRowSize, batchSizeWithMultiRow);
      this.resultSetConverter = resultSetConverter;
      this.sqlParametersSetter = sqlParametersSetter;
      this.tableSqlFactory = tableSqlFactory;
    }


    public int getTransactionIsolationLevel() {
      return transactionIsolationLevel;
    }


    public ResultSetConverter getResultSetConverter() {
      return resultSetConverter;
    }

    public SqlParametersSetter getSqlParametersSetter() {
      return sqlParametersSetter;
    }

    public SormOptions getOptions() {
      return options;
    }

    public LoggerContext getLoggerContext() {
      return loggerContext;
    }


    @Override
    public String toString() {
      return "SormConfig [tableNameMapper=" + tableNameMapper + ", columnFieldMapper="
          + columnFieldMapper + ", multiRowProcessorFactory=" + multiRowProcessorFactory
          + ", resultSetConverter=" + resultSetConverter + ", sqlParametersSetter="
          + sqlParametersSetter + ", options=" + options + ", transactionIsolationLevel="
          + transactionIsolationLevel + ", loggerContext=" + loggerContext + "]";
    }


    public ColumnFieldMapper getColumnFieldMapper() {
      return columnFieldMapper;
    }


    public TableNameMapper getTableNameMapper() {
      return tableNameMapper;
    }

    public TableSqlFactory getTableSqlFactory() {
      return tableSqlFactory;
    }


    public MultiRowProcessorFactory getMultiRowProcessorFactory() {
      return multiRowProcessorFactory;
    }

  }



}
