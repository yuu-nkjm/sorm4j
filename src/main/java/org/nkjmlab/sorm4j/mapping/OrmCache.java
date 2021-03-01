package org.nkjmlab.sorm4j.mapping;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class OrmCache {

  private static final ConcurrentMap<String, ConcurrentMap<String, TableMapping<?>>> tableMappingsCaches =
      new ConcurrentHashMap<>(); // key => Cache Name
  private static final ConcurrentMap<String, ConcurrentMap<Class<?>, ColumnsMapping<?>>> columnsMappingsCaches =
      new ConcurrentHashMap<>(); // key => Cache Name

  private static final ConcurrentMap<String, ConcurrentMap<Class<?>, TableName>> classNameToValidTableNameMaps =
      new ConcurrentHashMap<>();

  private static final ConcurrentMap<String, ConcurrentMap<String, TableName>> tableNameToValidTableNameMaps =
      new ConcurrentHashMap<>();

  public static ConcurrentMap<Class<?>, ColumnsMapping<?>> getColumnsMappings(String cacheName) {
    return columnsMappingsCaches.computeIfAbsent(cacheName, n -> new ConcurrentHashMap<>());
  }

  public static ConcurrentMap<String, TableMapping<?>> getTableMappings(String cacheName) {
    return tableMappingsCaches.computeIfAbsent(cacheName, n -> new ConcurrentHashMap<>());
  }

  public static ConcurrentMap<Class<?>, TableName> getClassNameToValidTableNameMap(
      String cacheName) {
    return classNameToValidTableNameMaps.computeIfAbsent(cacheName, n -> new ConcurrentHashMap<>());
  }

  public static ConcurrentMap<String, TableName> getTableNameToValidTableNameMaps(
      String cacheName) {
    return tableNameToValidTableNameMaps.computeIfAbsent(cacheName, n -> new ConcurrentHashMap<>());
  }

}
