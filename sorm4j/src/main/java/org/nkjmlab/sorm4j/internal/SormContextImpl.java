package org.nkjmlab.sorm4j.internal;

import static java.lang.System.*;
import static org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils.*;
import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.common.ColumnMetaData;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.context.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.ColumnValueToMapEntryConverter;
import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.context.TableNameMapper;
import org.nkjmlab.sorm4j.context.TableSql;
import org.nkjmlab.sorm4j.context.TableSqlFactory;
import org.nkjmlab.sorm4j.internal.mapping.ColumnToAccessorMapping;
import org.nkjmlab.sorm4j.internal.mapping.SqlParametersToTableMapping;
import org.nkjmlab.sorm4j.internal.mapping.SqlResultToColumnsMapping;
import org.nkjmlab.sorm4j.internal.mapping.TableName;
import org.nkjmlab.sorm4j.internal.result.TableMetaDataImpl;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.result.TableMetaData;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;

public final class SormContextImpl implements SormContext {

  private final ConcurrentMap<String, TableMetaData> tableMetaDataMap;
  private final ConcurrentMap<Class<?>, TableName> classNameToValidTableNameMap;
  private final ConcurrentMap<String, TableName> tableNameToValidTableNameMap;
  private final ConcurrentMap<String, SqlParametersToTableMapping<?>> sqlParametersToTableMappings;
  private final ConcurrentMap<Class<?>, SqlResultToColumnsMapping<?>> sqlResultToColumnsMappings;

  private final SormConfig sormConfig;

  private SormContextImpl(SormConfig sormConfig) {
    this.sormConfig = sormConfig;
    this.tableMetaDataMap = new ConcurrentHashMap<>();
    this.classNameToValidTableNameMap = new ConcurrentHashMap<>();
    this.tableNameToValidTableNameMap = new ConcurrentHashMap<>();
    this.sqlParametersToTableMappings = new ConcurrentHashMap<>();
    this.sqlResultToColumnsMappings = new ConcurrentHashMap<>();
  }

  public SormContextImpl(LoggerContext loggerContext, ColumnToFieldAccessorMapper columnFieldMapper,
      TableNameMapper tableNameMapper,
      ColumnValueToJavaObjectConverters columnValueToJavaObjectConverter,
      ColumnValueToMapEntryConverter columnValueToMapEntryConverter,
      SqlParametersSetter sqlParametersSetter, PreparedStatementSupplier statementSupplier,
      TableSqlFactory tableSqlFactory, MultiRowProcessorFactory multiRowProcessorFactory) {
    this(new SormConfig(loggerContext, columnFieldMapper, tableNameMapper,
        columnValueToJavaObjectConverter, columnValueToMapEntryConverter, sqlParametersSetter,
        statementSupplier, tableSqlFactory, multiRowProcessorFactory));
  }

  TableMetaData getTableMetaData(Connection connection, String tableName) {
    return getTableMetaData(connection, tableName, Object.class);
  }

  private <T> TableMetaData getTableMetaData(Connection connection, String tableName,
      Class<T> objectClass) {
    TableName _tableName = toTableName(connection, tableName);
    TableMetaData ret =
        tableMetaDataMap.computeIfAbsent(_tableName.getName(), Try.createFunctionWithThrow(_key -> {
          TableMetaData m =
              createTableMetaData(objectClass, _tableName.getName(), connection.getMetaData());
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
    String key = objectClass.getName() + "-" + tableName.getName();
    @SuppressWarnings("unchecked")
    SqlParametersToTableMapping<T> ret =
        (SqlParametersToTableMapping<T>) sqlParametersToTableMappings.computeIfAbsent(key, _k -> {
          try {
            SqlParametersToTableMapping<T> m =
                createTableMapping(objectClass, tableName.getName(), connection);
            sormConfig.getLoggerContext()
                .createLogPoint(LoggerContext.Category.MAPPING, SormContext.class)
                .ifPresent(lp -> lp.logMapping(m.toString()));
            return m;
          } catch (SQLException e) {
            throw Try.rethrow(e);
          }
        });
    return ret;
  }


  <T> SqlResultToColumnsMapping<T> createColumnsMapping(Class<T> objectClass) {

    ColumnToAccessorMapping columnToAccessorMap = new ColumnToAccessorMapping(objectClass,
        sormConfig.getColumnToFieldAccessorMapper().createMapping(objectClass),
        sormConfig.getColumnToFieldAccessorMapper().getColumnAliasPrefix(objectClass));

    return new SqlResultToColumnsMapping<>(sormConfig.getColumnValueToJavaObjectConverter(),
        objectClass, columnToAccessorMap);
  }

  <T> SqlParametersToTableMapping<T> createTableMapping(Class<T> objectClass, String tableName,
      Connection connection) throws SQLException {



    ColumnToAccessorMapping columnToAccessorMap = new ColumnToAccessorMapping(objectClass,
        sormConfig.getColumnToFieldAccessorMapper().createMapping(objectClass),
        sormConfig.getColumnToFieldAccessorMapper().getColumnAliasPrefix(objectClass));

    TableMetaData tableMetaData = getTableMetaData(connection, tableName, objectClass);

    validate(objectClass, tableMetaData, columnToAccessorMap.keySet());


    TableSql sql = sormConfig.getTableSqlFactory().create(tableMetaData, objectClass, connection);

    return new SqlParametersToTableMapping<>(sormConfig.getLoggerContext(),
        sormConfig.getColumnValueToJavaObjectConverter(), sormConfig.getSqlParametersSetter(),
        sormConfig.getPreparedStatementSupplier(), sormConfig.getMultiRowProcessorFactory(),
        objectClass, columnToAccessorMap, tableMetaData, sql);
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

    List<ColumnMetaData> columns =
        sormConfig.getTableMetaDataReader().getColumnsMetaData(metaData, tableName);

    List<String> primaryKeys =
        sormConfig.getTableMetaDataReader().getPrimaryKeys(metaData, tableName);

    List<String> autoGeneratedColumns =
        sormConfig.getTableMetaDataReader().getAutoGeneratedColumns(metaData, tableName);

    String prefix = sormConfig.getColumnToFieldAccessorMapper().getColumnAliasPrefix(objectClass);
    return new TableMetaDataImpl(tableName, prefix, columns, primaryKeys, autoGeneratedColumns);
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
    SqlResultToColumnsMapping<T> ret = (SqlResultToColumnsMapping<T>) sqlResultToColumnsMappings
        .computeIfAbsent(objectClass, _k -> {
          SqlResultToColumnsMapping<T> m = createColumnsMapping(objectClass);
          sormConfig.getLoggerContext()
              .createLogPoint(LoggerContext.Category.MAPPING, SormContext.class)
              .ifPresent(lp -> lp.logMapping(m.toString()));
          return m;
        });
    return ret;
  }

  private TableName toTableName(Connection connection, Class<?> objectClass) {
    return classNameToValidTableNameMap.computeIfAbsent(objectClass, k -> {
      try {
        return new TableName(
            sormConfig.getTableNameMapper().getTableName(objectClass, connection.getMetaData()));
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
        return new TableName(
            sormConfig.getTableNameMapper().getTableName(tableName, connection.getMetaData()));
      } catch (SQLException e) {
        throw Try.rethrow(e);
      }
    });
  }


  @Override
  public LoggerContext getLoggerContext() {
    return sormConfig.getLoggerContext();
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

  PreparedStatementSupplier getPreparedStatementSupplier() {
    return sormConfig.getPreparedStatementSupplier();
  }



  /**
   * Returns string of this context. This is for debugging.
   */
  @Override
  public String toString() {
    return "SormContext {" + lineSeparator() + "[Table metadata]" + lineSeparator()
        + convertMapToString(tableMetaDataMap) + lineSeparator() + "[SqlParameterToTableMappings]"
        + lineSeparator() + convertMapToString(sqlParametersToTableMappings) + lineSeparator()
        + "[SqlResultToColumnsMapping]" + lineSeparator()
        + convertClassMapToString(sqlResultToColumnsMappings) + lineSeparator()
        + "[classNameToValidTableNameMap]" + lineSeparator()
        + convertClassMapToString(classNameToValidTableNameMap) + lineSeparator()
        + "[tableNameToValidTableNameMap]" + lineSeparator()
        + convertMapToString(tableNameToValidTableNameMap) + lineSeparator() + "[SormConfig]"
        + lineSeparator() + sormConfig + lineSeparator() + "}";
  }

  private String convertClassMapToString(Map<Class<?>, ? extends Object> map) {
    return convertMapToString(map.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey().getName(), e -> e.getValue())));
  }

  private String convertMapToString(Map<String, ? extends Object> map) {
    List<String> keySet = map.keySet().stream().sorted().collect(Collectors.toList());
    return String.join(lineSeparator(),
        keySet.stream().map(e -> e + " => " + map.get(e).toString()).collect(Collectors.toList()));
  }

}
