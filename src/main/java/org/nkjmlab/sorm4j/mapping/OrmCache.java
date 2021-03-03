package org.nkjmlab.sorm4j.mapping;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class OrmCache {

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

  public static void refresh(String configName) {
    tableMappingsCaches.put(configName, new ConcurrentHashMap<>());
    columnsMappingsCaches.put(configName, new ConcurrentHashMap<>());
    classNameToValidTableNameMapCaches.put(configName, new ConcurrentHashMap<>());
    tableNameToValidTableNameMapCaches.put(configName, new ConcurrentHashMap<>());
  }


}
