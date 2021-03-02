package org.nkjmlab.sorm4j.mapping;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class OrmCache {

  private static final ConcurrentMap<String, ConcurrentMap<String, TableMapping<?>>> tableMappingsCaches =
      new ConcurrentHashMap<>(); // key => Cache Name
  private static final ConcurrentMap<String, ConcurrentMap<Class<?>, ColumnsMapping<?>>> columnsMappingsCaches =
      new ConcurrentHashMap<>(); // key => Cache Name

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
      String cacheName) {
    return classNameToValidTableNameMapCaches.computeIfAbsent(cacheName,
        n -> new ConcurrentHashMap<>());
  }

  public static ConcurrentMap<String, TableName> getTableNameToValidTableNameMaps(
      String cacheName) {
    return tableNameToValidTableNameMapCaches.computeIfAbsent(cacheName,
        n -> new ConcurrentHashMap<>());
  }

  public static void refresh(String cacheName) {
    tableMappingsCaches.put(cacheName, new ConcurrentHashMap<>());
    columnsMappingsCaches.put(cacheName, new ConcurrentHashMap<>());
    classNameToValidTableNameMapCaches.put(cacheName, new ConcurrentHashMap<>());
    tableNameToValidTableNameMapCaches.put(cacheName, new ConcurrentHashMap<>());
  }


}
