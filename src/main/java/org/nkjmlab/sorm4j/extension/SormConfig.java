package org.nkjmlab.sorm4j.extension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.extension.SormConfigBuilder.MultiRowProcessorType;
import org.nkjmlab.sorm4j.internal.mapping.ColumnsMapping;
import org.nkjmlab.sorm4j.internal.mapping.Mappings;
import org.nkjmlab.sorm4j.internal.mapping.SormOptionsImpl;
import org.nkjmlab.sorm4j.internal.mapping.TableMapping;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessorFactory;

/**
 * A configuration store of sorm4j.
 *
 * @author nkjm
 *
 */
@Experimental
public final class SormConfig {

  private final String cacheName;
  private final ColumnFieldMapper columnFieldMapper;
  private final TableNameMapper tableNameMapper;
  private final ResultSetConverter resultSetConverter;
  private final SqlParametersSetter sqlParametersSetter;
  private final MultiRowProcessorFactory multiRowProcessorFactory;
  private final MultiRowProcessorType multiRowProcessorType;
  private final int batchSize;
  private final int multiRowSize;
  private final int batchSizeWithMultiRow;
  private final int transactionIsolationLevel;
  private final Map<String, Object> options;
  private final Mappings mappings;


  public SormConfig(String cacheName, Map<String, Object> options,
      ColumnFieldMapper fieldNameMapper, TableNameMapper tableNameMapper,
      ResultSetConverter resultSetConverter, SqlParametersSetter sqlParametersSetter,
      MultiRowProcessorType multiRowProcessorType, int batchSize, int multiRowSize,
      int batchSizeWithMultiRow, int transactionIsolationLevel) {
    this.cacheName = cacheName;
    this.columnFieldMapper = fieldNameMapper;
    this.tableNameMapper = tableNameMapper;
    this.resultSetConverter = resultSetConverter;
    this.sqlParametersSetter = sqlParametersSetter;
    this.multiRowProcessorType = multiRowProcessorType;
    this.batchSize = batchSize;
    this.multiRowSize = multiRowSize;
    this.batchSizeWithMultiRow = batchSizeWithMultiRow;
    this.options = options;
    SormOptions _options = new SormOptionsImpl(options);
    this.multiRowProcessorFactory =
        MultiRowProcessorFactory.createMultiRowProcessorFactory(_options, sqlParametersSetter,
            multiRowProcessorType, batchSize, multiRowSize, batchSizeWithMultiRow);
    this.transactionIsolationLevel = transactionIsolationLevel;
    this.mappings =
        new Mappings(_options, tableNameMapper, fieldNameMapper, multiRowProcessorFactory,
            resultSetConverter, sqlParametersSetter, getTableMappings(), getColumnsMappings(),
            getClassNameToValidTableNameMap(), getTableNameToValidTableNameMaps());
  }

  public String getCacheName() {
    return cacheName;
  }

  public ColumnFieldMapper getColumnFieldMapper() {
    return columnFieldMapper;
  }

  public ResultSetConverter getResultSetConverter() {
    return resultSetConverter;
  }

  public TableNameMapper getTableNameMapper() {
    return tableNameMapper;
  }

  public MultiRowProcessorFactory getMultiRowProcessorFactory() {
    return multiRowProcessorFactory;
  }

  public SqlParametersSetter getSqlParametersSetter() {
    return sqlParametersSetter;
  }

  public ConcurrentMap<String, TableMapping<?>> getTableMappings() {
    return OrmCache.getTableMappings(cacheName);
  }

  public ConcurrentMap<Class<?>, ColumnsMapping<?>> getColumnsMappings() {
    return OrmCache.getColumnsMappings(cacheName);
  }

  public ConcurrentMap<Class<?>, TableName> getClassNameToValidTableNameMap() {
    return OrmCache.getClassNameToValidTableNameMap(cacheName);
  }

  public ConcurrentMap<String, TableName> getTableNameToValidTableNameMaps() {
    return OrmCache.getTableNameToValidTableNameMaps(cacheName);
  }

  public int getTransactionIsolationLevel() {
    return transactionIsolationLevel;
  }

  public MultiRowProcessorType getMultiRowProcessorType() {
    return multiRowProcessorType;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public int getMultiRowSize() {
    return multiRowSize;
  }

  public int getBatchSizeWithMultiRow() {
    return batchSizeWithMultiRow;
  }

  public Mappings getMappings() {
    return mappings;
  }

  @Override
  public String toString() {
    return "SormConfig [cacheName=" + cacheName + ", columnFieldMapper=" + columnFieldMapper
        + ", tableNameMapper=" + tableNameMapper + ", resultSetConverter=" + resultSetConverter
        + ", sqlParametersSetter=" + sqlParametersSetter + ", multiRowProcessorType="
        + multiRowProcessorType + ", batchSize=" + batchSize + ", multiRowSize=" + multiRowSize
        + ", batchSizeWithMultiRow=" + batchSizeWithMultiRow + ", transactionIsolationLevel="
        + transactionIsolationLevel + "]";
  }

  public Map<String, Object> getOptions() {
    return options;
  }

  private static class OrmCache {

    private OrmCache() {}

    private static final ConcurrentMap<String, ConcurrentMap<String, TableMapping<?>>> tableMappingsCaches =
        new ConcurrentHashMap<>(); // key => Config Name
    private static final ConcurrentMap<String, ConcurrentMap<Class<?>, ColumnsMapping<?>>> columnsMappingsCaches =
        new ConcurrentHashMap<>();

    private static final ConcurrentMap<String, ConcurrentMap<Class<?>, TableName>> classNameToValidTableNameMapCaches =
        new ConcurrentHashMap<>();

    private static final ConcurrentMap<String, ConcurrentMap<String, TableName>> tableNameToValidTableNameMapCaches =
        new ConcurrentHashMap<>();

    public static ConcurrentMap<Class<?>, ColumnsMapping<?>> getColumnsMappings(String cacheName) {
      return columnsMappingsCaches.computeIfAbsent(cacheName, n -> new ConcurrentHashMap<>());
    }

    public static ConcurrentMap<String, TableMapping<?>> getTableMappings(String cacheName) {
      return tableMappingsCaches.computeIfAbsent(cacheName, n -> new ConcurrentHashMap<>());
    }

    public static ConcurrentMap<Class<?>, TableName> getClassNameToValidTableNameMap(
        String configName) {
      return classNameToValidTableNameMapCaches.computeIfAbsent(configName,
          n -> new ConcurrentHashMap<>());
    }

    public static ConcurrentMap<String, TableName> getTableNameToValidTableNameMaps(
        String configName) {
      return tableNameToValidTableNameMapCaches.computeIfAbsent(configName,
          n -> new ConcurrentHashMap<>());
    }


  }

}
