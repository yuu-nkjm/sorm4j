package org.nkjmlab.sorm4j.internal.mapping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.SormFactory;
import org.nkjmlab.sorm4j.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.Configurator.MultiRowProcessorType;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.extension.TableName;
import org.nkjmlab.sorm4j.extension.TableNameMapper;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.internal.util.StringUtils;

/**
 * A configuration store of sorm4j.
 *
 * @author nkjm
 *
 */
public final class ConfigStore {
  public static ConfigStore INITIAL_DEFAULT_CONFIG_STORE =
      new ConfiguratorImpl(SormFactory.DEFAULT_CONFIG_NAME).build();

  // private static final org.slf4j.Logger log =
  // org.nkjmlab.sorm4j.internal.util.LoggerFactory.getLogger();

  private final String configName;
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


  public ConfigStore(String cacheName, Map<String, Object> options,
      ColumnFieldMapper fieldNameMapper, TableNameMapper tableNameMapper,
      ResultSetConverter resultSetConverter, SqlParametersSetter sqlParametersSetter,
      MultiRowProcessorType multiRowProcessorType, int batchSize, int multiRowSize,
      int batchSizeWithMultiRow, int transactionIsolationLevel) {
    this.configName = cacheName;
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

  public String getConfigName() {
    return configName;
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
    return OrmCache.getTableMappings(configName);
  }

  public ConcurrentMap<Class<?>, ColumnsMapping<?>> getColumnsMappings() {
    return OrmCache.getColumnsMappings(configName);
  }

  public ConcurrentMap<Class<?>, TableName> getClassNameToValidTableNameMap() {
    return OrmCache.getClassNameToValidTableNameMap(configName);
  }

  public ConcurrentMap<String, TableName> getTableNameToValidTableNameMaps() {
    return OrmCache.getTableNameToValidTableNameMaps(configName);
  }

  public int getTransactionIsolationLevel() {
    return transactionIsolationLevel;
  }

  private static final ConcurrentMap<String, ConfigStore> configStores = new ConcurrentHashMap<>();
  static {
    configStores.put(SormFactory.DEFAULT_CONFIG_NAME, INITIAL_DEFAULT_CONFIG_STORE);
  }

  public static ConfigStore refreshAndRegister(ConfigStore configStore) {
    refresh(configStore.getConfigName());
    configStores.put(configStore.getConfigName(), configStore);
    return configStore;
  }

  public static ConfigStore get(String configName) {
    ConfigStore ret = configStores.get(configName);
    if (ret != null) {
      return ret;
    }
    throw new SormException(
        StringUtils.format("Config name [{}] is not registered yet. Registered config names = {}",
            configName, configStores.keySet()));
  }

  public static ConfigStore getDefaultConfigStore() {
    return get(SormFactory.DEFAULT_CONFIG_NAME);
  }

  public static void refresh(String configName) {
    OrmCache.refresh(configName);
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
    return "ConfigStore [configName=" + configName + ", columnFieldMapper=" + columnFieldMapper
        + ", tableNameMapper=" + tableNameMapper + ", resultSetConverter=" + resultSetConverter
        + ", sqlParametersSetter=" + sqlParametersSetter + ", multiRowProcessorType="
        + multiRowProcessorType + ", batchSize=" + batchSize + ", multiRowSize=" + multiRowSize
        + ", batchSizeWithMultiRow=" + batchSizeWithMultiRow + ", transactionIsolationLevel="
        + transactionIsolationLevel + "]";
  }

  public Map<String, Object> getOptions() {
    return options;
  }

}
