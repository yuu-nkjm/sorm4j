package org.nkjmlab.sorm4j.internal;

import static org.nkjmlab.sorm4j.internal.mapping.TableMetaDataImpl.*;
import static org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils.*;
import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.SormContext;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.internal.mapping.SqlParametersToTableMapping;
import org.nkjmlab.sorm4j.internal.mapping.SqlResultToColumnsMapping;
import org.nkjmlab.sorm4j.internal.mapping.TableMetaDataImpl;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.mapping.ColumnToAccessorMapping;
import org.nkjmlab.sorm4j.mapping.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.mapping.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.mapping.ColumnValueToMapEntryConverter;
import org.nkjmlab.sorm4j.mapping.DefaultTableMetaDataParser;
import org.nkjmlab.sorm4j.mapping.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.mapping.SqlParametersSetter;
import org.nkjmlab.sorm4j.mapping.TableMetaDataParser;
import org.nkjmlab.sorm4j.mapping.TableName;
import org.nkjmlab.sorm4j.mapping.TableNameMapper;
import org.nkjmlab.sorm4j.mapping.TableSql;
import org.nkjmlab.sorm4j.mapping.TableSqlFactory;
import org.nkjmlab.sorm4j.result.ColumnNameWithMetaData;
import org.nkjmlab.sorm4j.result.TableMetaData;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;
import org.nkjmlab.sorm4j.util.logger.LoggerContext.Category;

public final class SormContextImpl implements SormContext {
  private final ConcurrentMap<String, SqlParametersToTableMapping<?>> tableMappings;
  private final ConcurrentMap<String, TableMetaData> tableMetaDataMap;
  private final ConcurrentMap<Class<?>, SqlResultToColumnsMapping<?>> columnsMappings;
  private final ConcurrentMap<Class<?>, TableName> classNameToValidTableNameMap;
  private final ConcurrentMap<String, TableName> tableNameToValidTableNameMap;
  private final SormConfig sormConfig;


  private SormContextImpl(SormConfig sormConfig) {
    this.sormConfig = sormConfig;
    this.tableMappings = new ConcurrentHashMap<>();
    this.tableMetaDataMap = new ConcurrentHashMap<>();
    this.columnsMappings = new ConcurrentHashMap<>();
    this.classNameToValidTableNameMap = new ConcurrentHashMap<>();
    this.tableNameToValidTableNameMap = new ConcurrentHashMap<>();
  }

  public SormContextImpl(LoggerContext loggerContext, ColumnToFieldAccessorMapper columnFieldMapper,
      TableNameMapper tableNameMapper,
      ColumnValueToJavaObjectConverters columnValueToJavaObjectConverter,
      ColumnValueToMapEntryConverter columnValueToMapEntryConverter,
      SqlParametersSetter sqlParametersSetter, TableSqlFactory tableSqlFactory,
      MultiRowProcessorFactory multiRowProcessorFactory, int transactionIsolationLevel) {
    this(new SormConfig(loggerContext, columnFieldMapper, tableNameMapper,
        columnValueToJavaObjectConverter, columnValueToMapEntryConverter, sqlParametersSetter,
        tableSqlFactory, multiRowProcessorFactory, transactionIsolationLevel));
  }

  TableMetaData getTableMetaData(Connection connection, String tableName) {
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

  <T> SqlParametersToTableMapping<T> getTableMapping(Connection connection, Class<T> objectClass) {
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


  <T> SqlResultToColumnsMapping<T> createColumnsMapping(Class<T> objectClass) {

    String columnAliasPrefix =
        getColumnAliasPrefix(objectClass).orElse(objectClass.getSimpleName() + "CLAZZ");

    ColumnToAccessorMapping columnToAccessorMap =
        sormConfig.getColumnToFieldAccessorMapper().createMapping(objectClass, columnAliasPrefix);


    return new SqlResultToColumnsMapping<>(sormConfig.getColumnValueToJavaObjectConverter(),
        objectClass, columnToAccessorMap);
  }

  <T> SqlParametersToTableMapping<T> createTableMapping(Class<T> objectClass, String tableName,
      Connection connection) throws SQLException {

    TableMetaData tableMetaData =
        createTableMetaData(objectClass, tableName, connection.getMetaData());

    String columnAliasPrefix =
        getColumnAliasPrefix(objectClass).orElse(objectClass.getSimpleName() + "CLAZZ");

    ColumnToAccessorMapping columnToAccessorMap =
        sormConfig.getColumnToFieldAccessorMapper().createMapping(objectClass, columnAliasPrefix);

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

    String columnAliasPrefix = getColumnAliasPrefix(objectClass).orElse(tableName + "TABLE");
    return new TableMetaDataImpl(tableName, columnAliasPrefix, columns, primaryKeys,
        autoGeneratedColumns);
  }

  @SuppressWarnings("unchecked")
  <T> SqlParametersToTableMapping<T> getCastedTableMapping(Connection connection,
      Class<?> objectClass) {
    return (SqlParametersToTableMapping<T>) getTableMapping(connection, objectClass);
  }


  @SuppressWarnings("unchecked")
  <T> SqlParametersToTableMapping<T> getCastedTableMapping(Connection connection, String tableName,
      Class<?> objectClass) {
    return (SqlParametersToTableMapping<T>) getTableMapping(connection, tableName, objectClass);
  }

  <T> SqlResultToColumnsMapping<T> getColumnsMapping(Class<T> objectClass) {
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

  String getTableName(Connection connection, Class<?> objectClass) {
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


  @Override
  public LoggerContext getLoggerContext() {
    return sormConfig.getLoggerContext();
  }


  @Override
  public int getTransactionIsolationLevel() {
    return sormConfig.getTransactionIsolationLevel();
  }


  ColumnValueToJavaObjectConverters getColumnValueToJavaObjectConverter() {
    return sormConfig.getColumnValueToJavaObjectConverter();
  }

  ColumnValueToMapEntryConverter getColumnValueToMapEntryConverter() {
    return sormConfig.getColumnValueToMapEntryConverter();
  }


  SqlParametersSetter getSqlParametersSetter() {
    return sormConfig.getSqlParametersSetter();
  }


  @Override
  public String toString() {
    return "SormContext [tableMappings=" + tableMappings + ", columnsMappings=" + columnsMappings
        + ", classNameToValidTableNameMap=" + classNameToValidTableNameMap
        + ", tableNameToValidTableNameMap=" + tableNameToValidTableNameMap + ", sormConfig="
        + sormConfig + "]";
  }

  static final class SormConfig {

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

    SormConfig(LoggerContext loggerContext, ColumnToFieldAccessorMapper columnFieldMapper,
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


    TableMetaDataParser getTableMetaDataReader() {
      return this.tableMetaDataReader;
    }


    int getTransactionIsolationLevel() {
      return transactionIsolationLevel;
    }

    ColumnValueToJavaObjectConverters getColumnValueToJavaObjectConverter() {
      return columnValueToJavaObjectConverter;
    }

    ColumnValueToMapEntryConverter getColumnValueToMapEntryConverter() {
      return columnValueToMapEntryConverter;
    }


    SqlParametersSetter getSqlParametersSetter() {
      return sqlParametersSetter;
    }

    LoggerContext getLoggerContext() {
      return loggerContext;
    }

    ColumnToFieldAccessorMapper getColumnToFieldAccessorMapper() {
      return columnFieldMapper;
    }


    TableNameMapper getTableNameMapper() {
      return tableNameMapper;
    }

    TableSqlFactory getTableSqlFactory() {
      return tableSqlFactory;
    }


    MultiRowProcessorFactory getMultiRowProcessorFactory() {
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
