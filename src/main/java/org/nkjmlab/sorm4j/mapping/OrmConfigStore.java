package org.nkjmlab.sorm4j.mapping;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.nkjmlab.sorm4j.mapping.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.mapping.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.mapping.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.mapping.extension.TableNameMapper;

/**
 * A configuration store of sorm4j.
 *
 * @author nkjm
 *
 */
public final class OrmConfigStore {

  // private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  private static final ConcurrentMap<String, OrmConfigStore> configStores =
      new ConcurrentHashMap<>();

  public static final String DEFAULT_CONFIG_NAME = "DEFAULT_CONFIG";
  public static final OrmConfigStore INITIAL_DEFAULT_CONFIG_STORE =
      new OrmConfigStoreBuilderImpl(DEFAULT_CONFIG_NAME).build();

  private final String configName;
  private final ColumnFieldMapper columnFieldMapper;
  private final TableNameMapper tableNameMapper;
  private final ResultSetConverter resultSetConverter;
  private final SqlParameterSetter sqlParameterSetter;
  private final MultiRowProcessorGeneratorFactory multiProcessorFactory;

  public static final int DEFAULT_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;


  static {
    configStores.put(DEFAULT_CONFIG_NAME, INITIAL_DEFAULT_CONFIG_STORE);
  }

  public OrmConfigStore(String cacheName, ColumnFieldMapper fieldNameMapper,
      TableNameMapper tableNameMapper, ResultSetConverter resultSetConverter,
      SqlParameterSetter javaToSqlConverter, MultiRowProcessorGeneratorFactory batchConfig) {
    this.configName = cacheName;
    this.columnFieldMapper = fieldNameMapper;
    this.tableNameMapper = tableNameMapper;
    this.resultSetConverter = resultSetConverter;
    this.sqlParameterSetter = javaToSqlConverter;
    this.multiProcessorFactory = batchConfig;
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


  public static OrmConfigStore refreshAndRegister(OrmConfigStore configStore) {
    refresh(configStore.getConfigName());
    configStores.put(configStore.getConfigName(), configStore);
    return configStore;
  }

  public static OrmConfigStore get(String configName) {
    return configStores.get(configName);
  }

  public static OrmConfigStore getDefaultConfigStore() {
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

}
