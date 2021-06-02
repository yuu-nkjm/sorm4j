package org.nkjmlab.sorm4j.extension;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext.Category;
import org.nkjmlab.sorm4j.internal.mapping.ColumnToAccessorMap;
import org.nkjmlab.sorm4j.internal.mapping.ColumnsMapping;
import org.nkjmlab.sorm4j.internal.mapping.TableMapping;
import org.nkjmlab.sorm4j.internal.mapping.TableMetaDataImpl;
import org.nkjmlab.sorm4j.internal.mapping.TableSql;
import org.nkjmlab.sorm4j.internal.mapping.TableSqlFactory;
import org.nkjmlab.sorm4j.internal.util.StringUtils;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.TableMetaData;

/**
 * A context for sorm.
 *
 * @author nkjm
 *
 */
@Experimental
public final class SormContext {

  private static final TableSqlFactory tableSqlFactory = new TableSqlFactory();
  private final ConcurrentMap<String, TableMapping<?>> tableMappings;
  private final ConcurrentMap<Class<?>, ColumnsMapping<?>> columnsMappings;
  private final ConcurrentMap<Class<?>, TableName> classNameToValidTableNameMap;
  private final ConcurrentMap<String, TableName> tableNameToValidTableNameMap;
  private final SormConfig sormConfig;


  public SormContext(SormConfig sormConfig) {
    this.sormConfig = sormConfig;
    this.tableMappings = new ConcurrentHashMap<>();
    this.columnsMappings = new ConcurrentHashMap<>();
    this.classNameToValidTableNameMap = new ConcurrentHashMap<>();
    this.tableNameToValidTableNameMap = new ConcurrentHashMap<>();
  }


  public Map<String, String> getTableMappingStatusMap() {
    return tableMappings.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getFormattedString()));
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
          sormConfig.getLoggerContext().createLogPoint(Category.MAPPING).ifPresent(lp -> lp.logger
              .info("[{}]" + System.lineSeparator() + "{}", lp.getTag(), m.getFormattedString()));
          return m;
        }, Try::rethrow));
    return ret;
  }

  public <T> ColumnsMapping<T> createColumnsMapping(Class<T> objectClass) {
    ColumnToAccessorMap columnToAccessorMap = new ColumnToAccessorMap(objectClass,
        sormConfig.getColumnFieldMapper().createAccessors(objectClass));

    return new ColumnsMapping<>(sormConfig.getOptions(), sormConfig.getResultSetConverter(),
        objectClass, columnToAccessorMap);
  }

  public <T> TableMapping<T> createTableMapping(Class<T> objectClass, String tableName,
      Connection connection) throws SQLException {

    DatabaseMetaData metaData = connection.getMetaData();

    List<ColumnName> allColumns = sormConfig.getColumnFieldMapper().getColumns(metaData, tableName);

    List<String> primaryKeys = sormConfig.getColumnFieldMapper().getPrimaryKeys(metaData, tableName)
        .stream().map(c -> c.getName()).collect(Collectors.toList());

    List<String> autoGeneratedColumns =
        sormConfig.getColumnFieldMapper().getAutoGeneratedColumns(metaData, tableName).stream()
            .map(c -> c.getName()).collect(Collectors.toList());

    List<String> columns = allColumns.stream().map(c -> c.getName()).collect(Collectors.toList());

    String colmunAliasPrefix =
        Optional.ofNullable(objectClass.getAnnotation(OrmColumnAliasPrefix.class))
            .map(a -> a.value()).orElse("");

    TableMetaData tableMetaData = new TableMetaDataImpl(tableName, colmunAliasPrefix, columns,
        primaryKeys, autoGeneratedColumns);

    TableSql sql = tableSqlFactory.create(tableMetaData);

    Map<String, Accessor> accessors =
        sormConfig.getColumnFieldMapper().createAccessors(objectClass, allColumns);

    Set<String> keySetWithoutAlias = accessors.keySet();
    if (!StringUtils.equalsAsCanonical(columns, keySetWithoutAlias)) {
      throw new SormException(StringUtils.format(
          "{} does not match any field. Table [{}] contains Columns {} but [{}] contains Fields {}.",
          columns.stream().filter(e -> !keySetWithoutAlias.contains(StringUtils.toCanonical(e)))
              .sorted().collect(Collectors.toList()),
          tableName,
          allColumns.stream().map(c -> c.toString()).sorted().collect(Collectors.toList()),
          objectClass.getName(),
          keySetWithoutAlias.stream().sorted().collect(Collectors.toList())));
    }

    ColumnToAccessorMap columnToAccessorMap = new ColumnToAccessorMap(objectClass, accessors);

    return new TableMapping<>(sormConfig.getLoggerContext(), sormConfig.getOptions(),
        sormConfig.getResultSetConverter(), sormConfig.getSqlParametersSetter(),
        sormConfig.getMultiRowProcessorFactory(), objectClass, columnToAccessorMap, tableMetaData,
        sql);
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
          .ifPresent(lp -> lp.logger.info(System.lineSeparator() + m.getFormattedString()));

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


}
