package org.nkjmlab.sorm4j.extension;

import static org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils.*;
import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.common.TableMetaData;
import org.nkjmlab.sorm4j.extension.impl.DefaultColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.extension.impl.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.extension.impl.DefaultColumnValueToMapEntryConverter;
import org.nkjmlab.sorm4j.extension.impl.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.extension.impl.DefaultTableMetaDataParser;
import org.nkjmlab.sorm4j.extension.impl.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.extension.impl.DefaultTableSqlFactory;
import org.nkjmlab.sorm4j.extension.impl.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext.Category;
import org.nkjmlab.sorm4j.extension.logger.SormLogger;
import org.nkjmlab.sorm4j.internal.mapping.ColumnToAccessorMapping;
import org.nkjmlab.sorm4j.internal.mapping.SqlParametersToTableMapping;
import org.nkjmlab.sorm4j.internal.mapping.SqlResultToColumnsMapping;
import org.nkjmlab.sorm4j.internal.mapping.TableMetaDataImpl;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * A context for sorm.
 *
 * @author nkjm
 *
 */
@Experimental
public final class SormContext {
  public static final SormContext DEFAULT_CONTEXT = SormContext.builder().build();

  private final ConcurrentMap<String, SqlParametersToTableMapping<?>> tableMappings;
  private final ConcurrentMap<String, TableMetaData> tableMetaDataMap;
  private final ConcurrentMap<Class<?>, SqlResultToColumnsMapping<?>> columnsMappings;
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

  public <T> SqlParametersToTableMapping<T> getTableMapping(Connection connection,
      Class<T> objectClass) {
    return getTableMapping(connection, toTableName(connection, objectClass), objectClass);
  }

  /**
   * Get table mapping by the table name and the object class. When there is no mapping, the method
   * create a mapping and register it.
   *
   */
  <T> SqlParametersToTableMapping<T> getTableMapping(Connection connection, String tableName,
      Class<T> objectClass) {
    return getTableMapping(connection, toTableName(connection, tableName), objectClass);
  }

  <T> SqlParametersToTableMapping<T> getTableMapping(Connection connection, TableName tableName,
      Class<T> objectClass) {
    String key = tableName.getName() + "-" + objectClass.getName();
    @SuppressWarnings("unchecked")
    SqlParametersToTableMapping<T> ret =
        (SqlParametersToTableMapping<T>) tableMappings.computeIfAbsent(key, _k -> {
          try {
            SqlParametersToTableMapping<T> m =
                createTableMapping(objectClass, tableName.getName(), connection);
            sormConfig.getLoggerContext().createLogPoint(Category.MAPPING, SormContext.class)
                .ifPresent(lp -> lp.logMapping(m.getFormattedString()));
            return m;
          } catch (SQLException e) {
            throw Try.rethrow(e);
          }
        });
    return ret;
  }


  public <T> SqlResultToColumnsMapping<T> createColumnsMapping(Class<T> objectClass) {

    ColumnToAccessorMapping columnToAccessorMap =
        sormConfig.getColumnToFieldAccessorMapper().createMapping(objectClass);


    return new SqlResultToColumnsMapping<>(sormConfig.getColumnValueToJavaObjectConverter(),
        objectClass, columnToAccessorMap);
  }

  public <T> SqlParametersToTableMapping<T> createTableMapping(Class<T> objectClass,
      String tableName, Connection connection) throws SQLException {

    TableMetaData tableMetaData =
        createTableMetaData(objectClass, tableName, connection.getMetaData());

    ColumnToAccessorMapping columnToAccessorMap =
        sormConfig.getColumnToFieldAccessorMapper().createMapping(objectClass);

    validate(objectClass, tableMetaData, columnToAccessorMap.getAccessors().keySet());


    TableSql sql = sormConfig.getTableSqlFactory().create(tableMetaData, objectClass, connection);

    return new SqlParametersToTableMapping<>(sormConfig.getLoggerContext(),
        sormConfig.getColumnValueToJavaObjectConverter(), sormConfig.getSqlParametersSetter(),
        sormConfig.getMultiRowProcessorFactory(), objectClass, columnToAccessorMap, tableMetaData,
        sql);
  }


  private void validate(Class<?> objectClass, TableMetaData tableMetaData,
      Set<String> keySetWithoutAlias) {
    List<String> notMatchColumns = tableMetaData.getColumns().stream()
        .filter(e -> !keySetWithoutAlias.contains(toCanonicalCase(e))).sorted()
        .collect(Collectors.toList());
    if (!notMatchColumns.isEmpty()) {
      throw new SormException(newString(
          "{} does not match any field. Table [{}] contains Columns {} but [{}] contains field accessors {}.",
          notMatchColumns, tableMetaData.getTableName(),
          tableMetaData.getColumns().stream().map(c -> c.toString()).sorted()
              .collect(Collectors.toList()),
          objectClass.getName(),
          keySetWithoutAlias.stream().sorted().collect(Collectors.toList())));
    }

  }


  private <T> TableMetaData createTableMetaData(Class<T> objectClass, String tableName,
      DatabaseMetaData metaData) throws SQLException {
    List<ColumnNameWithMetaData> columns =
        sormConfig.getTableMetaDataReader().getColumns(metaData, tableName);

    List<String> primaryKeys =
        sormConfig.getTableMetaDataReader().getPrimaryKeys(metaData, tableName).stream()
            .map(c -> c.getName()).collect(Collectors.toList());

    List<String> autoGeneratedColumns =
        sormConfig.getTableMetaDataReader().getAutoGeneratedColumns(metaData, tableName).stream()
            .map(c -> c.getName()).collect(Collectors.toList());

    String columnAliasPrefix = getColumnAliasPrefix(objectClass);
    return new TableMetaDataImpl(tableName, columnAliasPrefix, columns, primaryKeys,
        autoGeneratedColumns);
  }

  public static String getColumnAliasPrefix(Class<?> objectClass) {
    return Optional.ofNullable(objectClass.getAnnotation(OrmColumnAliasPrefix.class))
        .map(a -> a.value()).orElse("");
  }

  @SuppressWarnings("unchecked")
  public <T> SqlParametersToTableMapping<T> getCastedTableMapping(Connection connection,
      Class<?> objectClass) {
    return (SqlParametersToTableMapping<T>) getTableMapping(connection, objectClass);
  }


  @SuppressWarnings("unchecked")
  public <T> SqlParametersToTableMapping<T> getCastedTableMapping(Connection connection,
      String tableName, Class<?> objectClass) {
    return (SqlParametersToTableMapping<T>) getTableMapping(connection, tableName, objectClass);
  }

  public <T> SqlResultToColumnsMapping<T> getColumnsMapping(Class<T> objectClass) {
    @SuppressWarnings("unchecked")
    SqlResultToColumnsMapping<T> ret =
        (SqlResultToColumnsMapping<T>) columnsMappings.computeIfAbsent(objectClass, _k -> {
          SqlResultToColumnsMapping<T> m = createColumnsMapping(objectClass);
          sormConfig.getLoggerContext().createLogPoint(Category.MAPPING, SormContext.class)
              .ifPresent(lp -> lp.logMapping(m.getFormattedString()));

          return m;
        });
    return ret;
  }

  private TableName toTableName(Connection connection, Class<?> objectClass) {
    return classNameToValidTableNameMap.computeIfAbsent(objectClass, k -> {
      try {
        return sormConfig.getTableNameMapper().getTableName(objectClass, connection.getMetaData());
      } catch (SQLException e) {
        throw Try.rethrow(e);
      }
    });
  }

  public String getTableName(Connection connection, Class<?> objectClass) {
    return toTableName(connection, objectClass).getName();
  }

  private TableName toTableName(Connection connection, String tableName) {
    return tableNameToValidTableNameMap.computeIfAbsent(tableName, k -> {
      try {
        return sormConfig.getTableNameMapper().getTableName(tableName, connection.getMetaData());
      } catch (SQLException e) {
        throw Try.rethrow(e);
      }
    });
  }


  public LoggerContext getLoggerContext() {
    return sormConfig.getLoggerContext();
  }


  public int getTransactionIsolationLevel() {
    return sormConfig.getTransactionIsolationLevel();
  }


  public ColumnValueToJavaObjectConverters getColumnValueToJavaObjectConverter() {
    return sormConfig.getColumnValueToJavaObjectConverter();
  }

  public ColumnValueToMapEntryConverter getColumnValueToMapEntryConverter() {
    return sormConfig.getColumnValueToMapEntryConverter();
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

    private static final MultiRowProcessorFactory DEFAULT_MULTI_ROW_PROCESSOR_FACTORY =
        MultiRowProcessorFactory.builder().build();

    private static final SqlParametersSetter DEFAULT_SQL_PARAMETER_SETTER =
        new DefaultSqlParametersSetter();

    private static final ColumnValueToJavaObjectConverters DEFAULT_RESULT_SET_CONVERTER =
        new DefaultColumnValueToJavaObjectConverters();

    public static final ColumnValueToMapEntryConverter DEFAULT_COLUMN_VALUE_TO_MAP_CONVERTER =
        new DefaultColumnValueToMapEntryConverter();

    private static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();

    private static final TableSqlFactory DEFAULT_TABLE_SQL_FACTORY = new DefaultTableSqlFactory();

    private static final int DEFAULT_TRANSACTION_ISOLATION_LEVEL =
        Connection.TRANSACTION_READ_COMMITTED;

    private TableNameMapper tableNameMapper = DEFAULT_TABLE_NAME_MAPPER;
    private ColumnValueToJavaObjectConverters columnValueToJavaObjectConverter =
        DEFAULT_RESULT_SET_CONVERTER;
    private ColumnValueToMapEntryConverter columnValueToMapEntryConverter =
        DEFAULT_COLUMN_VALUE_TO_MAP_CONVERTER;
    private SqlParametersSetter sqlParametersSetter = DEFAULT_SQL_PARAMETER_SETTER;
    private MultiRowProcessorFactory multiRowProcessorFactory = DEFAULT_MULTI_ROW_PROCESSOR_FACTORY;
    private TableSqlFactory tableSqlFactory = DEFAULT_TABLE_SQL_FACTORY;

    private ColumnToFieldAccessorMapper columnFieldMapper;
    private int transactionIsolationLevel = DEFAULT_TRANSACTION_ISOLATION_LEVEL;
    private Map<String, Object> options = new HashMap<>();
    private LoggerContext.Builder loggerConfigBuilder = LoggerContext.builder();



    private Builder() {}

    public SormContext build() {
      LoggerContext loggerContext = loggerConfigBuilder.build();
      columnFieldMapper = columnFieldMapper != null ? columnFieldMapper
          : new DefaultColumnToFieldAccessorMapper(loggerContext);
      return new SormContext(new SormConfig(loggerContext, columnFieldMapper, tableNameMapper,
          columnValueToJavaObjectConverter, columnValueToMapEntryConverter, sqlParametersSetter,
          tableSqlFactory, multiRowProcessorFactory, transactionIsolationLevel));
    }


    public Builder setColumnFieldMapper(ColumnToFieldAccessorMapper fieldNameMapper) {
      this.columnFieldMapper = fieldNameMapper;
      return this;
    }


    public Builder setTableNameMapper(TableNameMapper tableNameMapper) {
      this.tableNameMapper = tableNameMapper;
      return this;
    }


    public Builder setColumnValueToJavaObjectConverter(
        ColumnValueToJavaObjectConverters converter) {
      this.columnValueToJavaObjectConverter = converter;
      return this;
    }

    public Builder setColumnValueToMapEntryConverter(ColumnValueToMapEntryConverter converter) {
      this.columnValueToMapEntryConverter = converter;
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


    public Builder setMultiRowProcessorFactory(MultiRowProcessorFactory multiRowProcessorFactory) {
      this.multiRowProcessorFactory = multiRowProcessorFactory;
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
    private final ColumnToFieldAccessorMapper columnFieldMapper;
    private final MultiRowProcessorFactory multiRowProcessorFactory;
    private final ColumnValueToJavaObjectConverters columnValueToJavaObjectConverter;
    private final ColumnValueToMapEntryConverter columnValueToMapEntryConverter;
    private final SqlParametersSetter sqlParametersSetter;
    private final int transactionIsolationLevel;
    private final LoggerContext loggerContext;
    private final TableSqlFactory tableSqlFactory;
    private final TableMetaDataParser tableMetaDataReader = new DefaultTableMetaDataParser();

    public SormConfig(LoggerContext loggerContext, ColumnToFieldAccessorMapper columnFieldMapper,
        TableNameMapper tableNameMapper,
        ColumnValueToJavaObjectConverters columnValueToJavaObjectConverter,
        ColumnValueToMapEntryConverter columnValueToMapEntryConverter,
        SqlParametersSetter sqlParametersSetter, TableSqlFactory tableSqlFactory,
        MultiRowProcessorFactory multiRowProcessorFactory, int transactionIsolationLevel) {
      this.loggerContext = loggerContext;
      this.transactionIsolationLevel = transactionIsolationLevel;
      this.tableNameMapper = tableNameMapper;
      this.columnFieldMapper = columnFieldMapper;
      this.multiRowProcessorFactory = multiRowProcessorFactory;
      this.columnValueToJavaObjectConverter = columnValueToJavaObjectConverter;
      this.columnValueToMapEntryConverter = columnValueToMapEntryConverter;
      this.sqlParametersSetter = sqlParametersSetter;
      this.tableSqlFactory = tableSqlFactory;
    }


    public TableMetaDataParser getTableMetaDataReader() {
      return this.tableMetaDataReader;
    }


    public int getTransactionIsolationLevel() {
      return transactionIsolationLevel;
    }

    public ColumnValueToJavaObjectConverters getColumnValueToJavaObjectConverter() {
      return columnValueToJavaObjectConverter;
    }

    public ColumnValueToMapEntryConverter getColumnValueToMapEntryConverter() {
      return columnValueToMapEntryConverter;
    }


    public SqlParametersSetter getSqlParametersSetter() {
      return sqlParametersSetter;
    }

    public LoggerContext getLoggerContext() {
      return loggerContext;
    }

    public ColumnToFieldAccessorMapper getColumnToFieldAccessorMapper() {
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


    @Override
    public String toString() {
      return "SormConfig [tableNameMapper=" + tableNameMapper + ", columnFieldMapper="
          + columnFieldMapper + ", multiRowProcessorFactory=" + multiRowProcessorFactory
          + ", columnValueToJavaObjectConverter=" + columnValueToJavaObjectConverter
          + ", columnValueToMapEntryConverter=" + columnValueToMapEntryConverter
          + ", sqlParametersSetter=" + sqlParametersSetter + ", transactionIsolationLevel="
          + transactionIsolationLevel + ", loggerContext=" + loggerContext + ", tableSqlFactory="
          + tableSqlFactory + "]";
    }

  }



}
