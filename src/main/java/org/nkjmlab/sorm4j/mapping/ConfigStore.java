package org.nkjmlab.sorm4j.mapping;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.mapping.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.mapping.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.mapping.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.mapping.extension.TableNameMapper;
import org.nkjmlab.sorm4j.util.StringUtils;

/**
 * A configuration store of sorm4j.
 *
 * @author nkjm
 *
 */
public final class ConfigStore {

  // private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();


  private static final ConcurrentMap<String, ConfigStore> configStores = new ConcurrentHashMap<>();

  public static final String DEFAULT_CONFIG_NAME = "DEFAULT_CONFIG";
  public static final ConfigStore INITIAL_DEFAULT_CONFIG_STORE =
      new ConfigStoreBuilderImpl(DEFAULT_CONFIG_NAME).build();

  private final String configName;
  private final ColumnFieldMapper columnFieldMapper;
  private final TableNameMapper tableNameMapper;
  private final ResultSetConverter resultSetConverter;
  private final SqlParameterSetter sqlParameterSetter;
  private final MultiRowProcessorGeneratorFactory multiProcessorFactory;
  private final int transactionIsolationLevel;



  static {
    configStores.put(DEFAULT_CONFIG_NAME, INITIAL_DEFAULT_CONFIG_STORE);
  }

  public ConfigStore(String cacheName, ColumnFieldMapper fieldNameMapper,
      TableNameMapper tableNameMapper, ResultSetConverter resultSetConverter,
      SqlParameterSetter javaToSqlConverter, MultiRowProcessorGeneratorFactory batchConfig,
      int transactionIsolationLevel) {
    this.configName = cacheName;
    this.columnFieldMapper = fieldNameMapper;
    this.tableNameMapper = tableNameMapper;
    this.resultSetConverter = resultSetConverter;
    this.sqlParameterSetter = javaToSqlConverter;
    this.multiProcessorFactory = batchConfig;
    this.transactionIsolationLevel = transactionIsolationLevel;
  }


  public String getConfigName() {
    return configName;
  }


  public ColumnFieldMapper getColumnFieldMapper() {
    return columnFieldMapper;
  }


  public ResultSetConverter getSqlToJavaDataConverter() {
    return resultSetConverter;
  }



  public TableNameMapper getTableNameMapper() {
    return tableNameMapper;
  }


  public MultiRowProcessorGeneratorFactory getMultiProcessorFactory() {
    return multiProcessorFactory;
  }


  public SqlParameterSetter getSqlParameterSetter() {
    return sqlParameterSetter;
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
    return get(DEFAULT_CONFIG_NAME);
  }

  public static void refresh(String configName) {
    OrmCache.refresh(configName);
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

}
