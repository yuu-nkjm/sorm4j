package org.nkjmlab.sorm4j.internal.mapping;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.extension.Accessor;
import org.nkjmlab.sorm4j.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.ColumnName;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SormLogger;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.extension.TableName;
import org.nkjmlab.sorm4j.extension.TableNameMapper;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.internal.util.LogPointFactory;
import org.nkjmlab.sorm4j.internal.util.StringUtils;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.TableMetaData;

final class Mappings {

  private static final TableSqlFactory tableSqlFactory = new TableSqlFactory();
  private final TableNameMapper tableNameMapper;
  private final ColumnFieldMapper columnFieldMapper;
  private final MultiRowProcessorFactory multiRowProcessorFactory;
  private final ResultSetConverter resultSetConverter;
  private final SqlParametersSetter sqlParametersSetter;

  private final ConcurrentMap<String, TableMapping<?>> tableMappings;

  private final ConcurrentMap<Class<?>, ColumnsMapping<?>> columnsMappings;

  private final ConcurrentMap<Class<?>, TableName> classNameToValidTableNameMap;

  private final ConcurrentMap<String, TableName> tableNameToValidTableNameMap;
  private final SormOptions options;


  public Mappings(SormOptions options, TableNameMapper tableNameMapper,
      ColumnFieldMapper columnFieldMapper, MultiRowProcessorFactory multiRowProcessorFactory,
      ResultSetConverter resultSetConverter, SqlParametersSetter sqlParametersSetter,
      ConcurrentMap<String, TableMapping<?>> tableMappings,
      ConcurrentMap<Class<?>, ColumnsMapping<?>> columnsMappings,
      ConcurrentMap<Class<?>, TableName> classNameToValidTableNameMap,
      ConcurrentMap<String, TableName> tableNameToValidTableNameMap) {
    this.options = options;
    this.tableNameMapper = tableNameMapper;
    this.columnFieldMapper = columnFieldMapper;
    this.multiRowProcessorFactory = multiRowProcessorFactory;
    this.resultSetConverter = resultSetConverter;
    this.sqlParametersSetter = sqlParametersSetter;
    this.tableMappings = tableMappings;
    this.columnsMappings = columnsMappings;
    this.classNameToValidTableNameMap = classNameToValidTableNameMap;
    this.tableNameToValidTableNameMap = tableNameToValidTableNameMap;
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
  <T> TableMapping<T> getTableMapping(Connection connection, String tableName,
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
          LogPointFactory.createLogPoint(SormLogger.Category.MAPPING)
              .ifPresent(lp -> lp.info(Mappings.class, "[{}]" + System.lineSeparator() + "{}",
                  lp.getTag(), m.getFormattedString()));
          return m;
        }, Try::rethrow));
    return ret;
  }

  public <T> ColumnsMapping<T> createColumnsMapping(Class<T> objectClass) {
    ColumnToAccessorMap columnToAccessorMap =
        new ColumnToAccessorMap(objectClass, columnFieldMapper.createAccessors(objectClass));

    return new ColumnsMapping<>(options, objectClass, resultSetConverter, columnToAccessorMap);
  }

  public <T> TableMapping<T> createTableMapping(Class<T> objectClass, String tableName,
      Connection connection) throws SQLException {

    DatabaseMetaData metaData = connection.getMetaData();

    List<ColumnName> allColumns = columnFieldMapper.getColumns(metaData, tableName);

    List<String> primaryKeys = columnFieldMapper.getPrimaryKeys(metaData, tableName).stream()
        .map(c -> c.getName()).collect(Collectors.toList());

    List<String> autoGeneratedColumns =
        columnFieldMapper.getAutoGeneratedColumns(metaData, tableName).stream()
            .map(c -> c.getName()).collect(Collectors.toList());

    List<String> columns = allColumns.stream().map(c -> c.getName()).collect(Collectors.toList());

    String colmunAliasPrefix =
        Optional.ofNullable(objectClass.getAnnotation(OrmColumnAliasPrefix.class))
            .map(a -> a.value()).orElse("");

    TableMetaData tableMetaData = new TableMetaDataImpl(tableName, colmunAliasPrefix, columns,
        primaryKeys, autoGeneratedColumns);

    TableSql sql = tableSqlFactory.create(tableMetaData);

    Map<String, Accessor> accessors = columnFieldMapper.createAccessors(objectClass, allColumns);

    Set<String> keySetWithoutAlias = accessors.keySet();
    if (!StringUtils.equalsAsCanonical(columns, keySetWithoutAlias)) {
      throw new SormException(StringUtils.format(
          "{} does not match any field. Table [{}] contains Columns {} but [{}] contains Fields {}.",
          columns.stream().filter(e -> !keySetWithoutAlias.contains(StringUtils.toCanonical(e)))
              .sorted().collect(Collectors.toList()),
          tableName, allColumns.stream().sorted().collect(Collectors.toList()),
          objectClass.getName(),
          keySetWithoutAlias.stream().sorted().collect(Collectors.toList())));
    }

    ColumnToAccessorMap columnToAccessorMap = new ColumnToAccessorMap(objectClass, accessors);

    return new TableMapping<>(options, resultSetConverter, objectClass, columnToAccessorMap,
        sqlParametersSetter, multiRowProcessorFactory, tableMetaData, sql);
  }


  @SuppressWarnings("unchecked")
  protected <T> TableMapping<T> getCastedTableMapping(Connection connection, Class<?> objectClass) {
    return (TableMapping<T>) getTableMapping(connection, objectClass);
  }


  @SuppressWarnings("unchecked")
  protected <T> TableMapping<T> getCastedTableMapping(Connection connection, String tableName,
      Class<?> objectClass) {
    return (TableMapping<T>) getTableMapping(connection, tableName, objectClass);
  }

  <T> ColumnsMapping<T> getColumnsMapping(Class<T> objectClass) {
    @SuppressWarnings("unchecked")
    ColumnsMapping<T> ret = (ColumnsMapping<T>) columnsMappings.computeIfAbsent(objectClass, _k -> {
      ColumnsMapping<T> m = createColumnsMapping(objectClass);

      LogPointFactory.createLogPoint(SormLogger.Category.MAPPING).ifPresent(
          lp -> lp.info(Mappings.class, System.lineSeparator() + m.getFormattedString()));

      return m;
    });
    return ret;
  }

  private TableName toTableName(Connection connection, Class<?> objectClass) {
    return classNameToValidTableNameMap.computeIfAbsent(objectClass, Try.createFunctionWithThrow(
        k -> tableNameMapper.getTableName(objectClass, connection.getMetaData()), Try::rethrow));
  }

  public String getTableName(Connection connection, Class<?> objectClass) {
    return toTableName(connection, objectClass).getName();
  }

  private TableName toTableName(Connection connection, String tableName) {
    return tableNameToValidTableNameMap.computeIfAbsent(tableName, Try.createFunctionWithThrow(
        k -> tableNameMapper.getTableName(tableName, connection.getMetaData()), Try::rethrow));
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
}
