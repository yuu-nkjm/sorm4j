package org.nkjmlab.sorm4j.internal;

import static java.lang.System.lineSeparator;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.nkjmlab.sorm4j.container.sql.TableSql;
import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.context.TableNameMapper;
import org.nkjmlab.sorm4j.context.logging.LogContext;
import org.nkjmlab.sorm4j.internal.container.sql.TableName;
import org.nkjmlab.sorm4j.internal.container.sql.metadata.ColumnMetaData;
import org.nkjmlab.sorm4j.internal.container.sql.metadata.TableMetaData;
import org.nkjmlab.sorm4j.internal.context.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.internal.context.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.internal.context.ColumnValueToMapValueConverters;
import org.nkjmlab.sorm4j.internal.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.internal.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.internal.context.TableSqlFactory;
import org.nkjmlab.sorm4j.internal.context.common.TableMetaDataImpl;
import org.nkjmlab.sorm4j.internal.mapping.ColumnToAccessorMapping;
import org.nkjmlab.sorm4j.internal.mapping.ContainerToTableMapper;
import org.nkjmlab.sorm4j.internal.mapping.result.ResultsToContainerMapper;
import org.nkjmlab.sorm4j.internal.util.Try;

public final class SormContextImpl implements SormContext {

  private final ConcurrentMap<String, TableMetaData> tableMetaDataMap;
  private final ConcurrentMap<String, TableSql> tableSqlMap;
  private final ConcurrentMap<Class<?>, TableName> classNameToValidTableNameMap;
  private final ConcurrentMap<String, TableName> tableNameToValidTableNameMap;
  private final ConcurrentMap<Class<?>, Map<String, ContainerToTableMapper<?>>>
      sqlParametersToTableMappings;
  private final ConcurrentMap<Class<?>, ResultsToContainerMapper<?>> sqlResultToColumnsMappings;
  private final SormConfig config;

  SormContextImpl(SormConfig sormConfig) {
    this.config = sormConfig;
    this.tableMetaDataMap = new ConcurrentHashMap<>();
    this.tableSqlMap = new ConcurrentHashMap<>();
    this.classNameToValidTableNameMap = new ConcurrentHashMap<>();
    this.tableNameToValidTableNameMap = new ConcurrentHashMap<>();
    this.sqlParametersToTableMappings = new ConcurrentHashMap<>();
    this.sqlResultToColumnsMappings = new ConcurrentHashMap<>();
  }

  public SormContextImpl(
      LogContext loggerContext,
      ColumnToFieldAccessorMapper columnFieldMapper,
      TableNameMapper tableNameMapper,
      ColumnValueToJavaObjectConverters columnValueToJavaObjectConverter,
      ColumnValueToMapValueConverters columnValueToMapValueConverter,
      SqlParametersSetter sqlParametersSetter,
      PreparedStatementSupplier statementSupplier,
      TableSqlFactory tableSqlFactory,
      MultiRowProcessorFactory multiRowProcessorFactory) {
    this(
        new SormConfig(
            loggerContext,
            columnFieldMapper,
            tableNameMapper,
            columnValueToJavaObjectConverter,
            columnValueToMapValueConverter,
            sqlParametersSetter,
            statementSupplier,
            tableSqlFactory,
            multiRowProcessorFactory));
  }

  TableMetaData getTableMetaData(Connection connection, String tableName) {
    return getTableMetaData(connection, tableName, NoValueType.class);
  }

  <T> TableMetaData getTableMetaData(
      Connection connection, String tableName, Class<T> objectClass) {
    TableName _tableName = toTableName(connection, tableName);
    TableMetaData ret =
        tableMetaDataMap.computeIfAbsent(
            _tableName.getName(),
            _key -> {
              try {
                return createTableMetaData(
                    objectClass, _tableName.getName(), connection.getMetaData());
              } catch (SQLException e) {
                throw Try.rethrow(e);
              }
            });
    return ret;
  }

  public <T> TableSql getTableSql(Connection connection, TableMetaData tableMetaData) {
    return tableSqlMap.computeIfAbsent(
        tableMetaData.getTableName(),
        _key -> {
          try {
            return config
                .getTableSqlFactory()
                .create(
                    tableMetaData,
                    org.nkjmlab.sorm4j.internal.container.sql.metadata.DbMetaData.of(
                        connection.getMetaData()));
          } catch (SQLException e) {
            throw Try.rethrow(e);
          }
        });
  }

  <T> ContainerToTableMapper<T> getTableMapping(Connection connection, Class<T> objectClass) {
    return getTableMapping(connection, toTableName(connection, objectClass), objectClass);
  }

  /**
   * Get table mapping by the table name and the object class. When there is no mapping, the method
   * create a mapping and register it.
   */
  <T> ContainerToTableMapper<T> getTableMapping(
      Connection connection, String tableName, Class<T> objectClass) {
    return getTableMapping(connection, toTableName(connection, tableName), objectClass);
  }

  @SuppressWarnings("unchecked")
  <T> ContainerToTableMapper<T> getTableMapping(
      Connection connection, TableName tableName, Class<T> objectClass) {
    ContainerToTableMapper<T> ret =
        (ContainerToTableMapper<T>)
            sqlParametersToTableMappings
                .computeIfAbsent(objectClass, _k -> new ConcurrentHashMap<>())
                .computeIfAbsent(
                    tableName.getName(),
                    _k -> {
                      try {
                        ContainerToTableMapper<T> m =
                            createTableMapping(objectClass, tableName.getName(), connection);
                        config
                            .getLoggerContext()
                            .createLogPoint(LogContext.Category.MAPPING_TO_TABLE, SormContext.class)
                            .ifPresent(lp -> lp.logMapping(m.toString()));
                        return m;
                      } catch (SQLException e) {
                        throw Try.rethrow(e);
                      }
                    });
    return ret;
  }

  <T> ResultsToContainerMapper<T> createColumnsMapping(Class<T> objectClass) {

    ColumnToAccessorMapping columnToAccessorMap =
        new ColumnToAccessorMapping(
            objectClass,
            config.getColumnToFieldAccessorMapper().createMapping(objectClass),
            config.getColumnToFieldAccessorMapper().getColumnAliasPrefix(objectClass));

    return new ResultsToContainerMapper<>(
        config.getColumnValueToJavaObjectConverter(), objectClass, columnToAccessorMap);
  }

  <T> ContainerToTableMapper<T> createTableMapping(
      Class<T> objectClass, String tableName, Connection connection) throws SQLException {

    ColumnToAccessorMapping columnToAccessorMap =
        new ColumnToAccessorMapping(
            objectClass,
            config.getColumnToFieldAccessorMapper().createMapping(objectClass),
            config.getColumnToFieldAccessorMapper().getColumnAliasPrefix(objectClass));

    TableMetaData tableMetaData = getTableMetaData(connection, tableName, objectClass);

    TableSql sql = getTableSql(connection, tableMetaData);

    // validate(objectClass, tableMetaData, columnToAccessorMap.keySet());

    return new ContainerToTableMapper<>(
        config.getLoggerContext(),
        config.getColumnValueToJavaObjectConverter(),
        config.getSqlParametersSetter(),
        config.getPreparedStatementSupplier(),
        config.getMultiRowProcessorFactory(),
        objectClass,
        columnToAccessorMap,
        tableMetaData,
        sql);
  }

  // private void validate(Class<?> objectClass, TableMetaData tableMetaData,
  // Set<String> keySetWithoutAlias) {
  // List<String> notMatchColumns = tableMetaData.getColumns().stream()
  // .filter(e -> !keySetWithoutAlias.contains(toCanonicalCase(e))).sorted()
  // .collect(Collectors.toList());
  // if (!notMatchColumns.isEmpty()) {
  // throw new SormException(newString(
  // "{} does not match any field. Table [{}] contains Columns {} but [{}] contains field accessors
  // {}.",
  // notMatchColumns, tableMetaData.getTableName(),
  // tableMetaData.getColumns().stream().map(c -> c.toString()).sorted()
  // .collect(Collectors.toList()),
  // objectClass.getName(),
  // keySetWithoutAlias.stream().sorted().collect(Collectors.toList())));
  // }
  // }

  private <T> TableMetaDataImpl createTableMetaData(
      Class<T> objectClass, String tableName, DatabaseMetaData metaData) throws SQLException {

    List<ColumnMetaData> columns =
        config.getTableMetaDataReader().getColumnsMetaData(metaData, tableName);

    List<String> primaryKeys = config.getTableMetaDataReader().getPrimaryKeys(metaData, tableName);

    List<String> autoGeneratedColumns =
        config.getTableMetaDataReader().getAutoGeneratedColumns(metaData, tableName);

    String prefix = config.getColumnToFieldAccessorMapper().getColumnAliasPrefix(objectClass);
    return new TableMetaDataImpl(tableName, prefix, columns, primaryKeys, autoGeneratedColumns);
  }

  @SuppressWarnings("unchecked")
  <T> ContainerToTableMapper<T> getCastedTableMapping(
      Connection connection, Class<?> objectClass) {
    return (ContainerToTableMapper<T>) getTableMapping(connection, objectClass);
  }

  @SuppressWarnings("unchecked")
  <T> ContainerToTableMapper<T> getCastedTableMapping(
      Connection connection, String tableName, Class<?> objectClass) {
    return (ContainerToTableMapper<T>) getTableMapping(connection, tableName, objectClass);
  }

  <T> ResultsToContainerMapper<T> getColumnsMapping(Class<T> objectClass) {
    @SuppressWarnings("unchecked")
    ResultsToContainerMapper<T> ret =
        (ResultsToContainerMapper<T>)
            sqlResultToColumnsMappings.computeIfAbsent(
                objectClass,
                _k -> {
                  ResultsToContainerMapper<T> m = createColumnsMapping(objectClass);
                  config
                      .getLoggerContext()
                      .createLogPoint(LogContext.Category.MAPPING_TO_COLUMNS, SormContext.class)
                      .ifPresent(lp -> lp.logMapping(m.toString()));
                  return m;
                });
    return ret;
  }

  private TableName toTableName(Connection connection, Class<?> objectClass) {
    return classNameToValidTableNameMap.computeIfAbsent(
        objectClass,
        k -> {
          try {
            return TableName.of(
                config.getTableNameMapper().getTableName(objectClass, connection.getMetaData()));
          } catch (SQLException e) {
            throw Try.rethrow(e);
          }
        });
  }

  String getTableName(Connection connection, Class<?> objectClass) {
    return toTableName(connection, objectClass).getName();
  }

  private TableName toTableName(Connection connection, String tableName) {
    return tableNameToValidTableNameMap.computeIfAbsent(
        tableName,
        k -> {
          try {
            return TableName.of(
                config.getTableNameMapper().getTableName(tableName, connection.getMetaData()));
          } catch (SQLException e) {
            throw Try.rethrow(e);
          }
        });
  }

  @Override
  public LogContext getLogContext() {
    return config.getLoggerContext();
  }

  @Override
  public ColumnValueToJavaObjectConverters getColumnValueToJavaObjectConverter() {
    return config.getColumnValueToJavaObjectConverter();
  }

  @Override
  public ColumnValueToMapValueConverters getColumnValueToMapValueConverter() {
    return config.getColumnValueToMapValueConverter();
  }

  @Override
  public SqlParametersSetter getSqlParametersSetter() {
    return config.getSqlParametersSetter();
  }

  @Override
  public PreparedStatementSupplier getPreparedStatementSupplier() {
    return config.getPreparedStatementSupplier();
  }

  /** Returns string of this context. This is for debugging. */
  @Override
  public String toString() {
    return "SormContext {"
        + lineSeparator()
        + "[Table metadata]"
        + lineSeparator()
        + convertMapToString(tableMetaDataMap)
        + lineSeparator()
        + "[SqlParameterToTableMappings]"
        + lineSeparator()
        + convertNestedMapToString(sqlParametersToTableMappings)
        + lineSeparator()
        + "[SqlResultToColumnsMapping]"
        + lineSeparator()
        + convertClassMapToString(sqlResultToColumnsMappings)
        + lineSeparator()
        + "[classNameToValidTableNameMap]"
        + lineSeparator()
        + convertClassMapToString(classNameToValidTableNameMap)
        + lineSeparator()
        + "[tableNameToValidTableNameMap]"
        + lineSeparator()
        + convertMapToString(tableNameToValidTableNameMap)
        + lineSeparator()
        + "[SormConfig]"
        + lineSeparator()
        + config
        + lineSeparator()
        + "}";
  }

  private String convertClassMapToString(Map<Class<?>, ? extends Object> map) {
    return convertMapToString(
        map.entrySet().stream()
            .collect(Collectors.toMap(e -> e.getKey().getName(), e -> e.getValue())));
  }

  private String convertMapToString(Map<String, ? extends Object> map) {
    List<String> keySet = map.keySet().stream().sorted().collect(Collectors.toList());
    return String.join(
        lineSeparator(),
        keySet.stream().map(e -> e + " => " + map.get(e).toString()).collect(Collectors.toList()));
  }

  private String convertNestedMapToString(
      Map<Class<?>, Map<String, ContainerToTableMapper<?>>> sqlParametersToTableMappings) {
    return sqlParametersToTableMappings.entrySet().stream()
        .map(
            entry -> {
              Class<?> outerKey = entry.getKey();
              Map<String, ? extends Object> innerMap = entry.getValue();
              String innerMapStr =
                  innerMap.entrySet().stream()
                      .map(
                          innerEntry -> "  " + innerEntry.getKey() + " => " + innerEntry.getValue())
                      .collect(Collectors.joining(lineSeparator()));

              return outerKey + ":" + lineSeparator() + innerMapStr;
            })
        .collect(Collectors.joining(lineSeparator() + lineSeparator()));
  }

  public SormContext.Builder builder() {
    return SormContext.builder()
        .setLogContext(config.getLoggerContext())
        .setMultiRowProcessorFactory(config.getMultiRowProcessorFactory())
        .setTableNameMapper(config.getTableNameMapper())
        .setTableSqlFactory(config.getTableSqlFactory());
  }

  private static class NoValueType {}
}
