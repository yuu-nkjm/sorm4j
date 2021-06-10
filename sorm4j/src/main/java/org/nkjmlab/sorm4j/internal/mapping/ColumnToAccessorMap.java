package org.nkjmlab.sorm4j.internal.mapping;

import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.extension.Accessor;

public final class ColumnToAccessorMap {

  private final Map<String, Accessor> columnToAccessorMap;
  private final Map<String, Accessor> aliasColumnToAccessorMap;
  private final String columnAliasPrefix;

  /**
   * ColumnName map to accessor. A key converted to a canonical name.
   *
   * @param columnToAccessorMap
   */
  public ColumnToAccessorMap(Class<?> objectClass, Map<String, Accessor> columnToAccessorMap,
      String columnAliasPrefix, Map<String, Accessor> aliasColumnToAccessorMap) {
    this.columnToAccessorMap = columnToAccessorMap.entrySet().stream()
        .collect(Collectors.toMap(e -> toCanonicalCase(e.getKey()), e -> e.getValue()));
    this.columnAliasPrefix = columnAliasPrefix;

    this.aliasColumnToAccessorMap = aliasColumnToAccessorMap.entrySet().stream()
        .collect(Collectors.toMap(e -> toCanonicalCase(e.getKey()), e -> e.getValue()));
  }

  public ColumnToAccessorMap(Class<?> objectClass, Map<String, Accessor> columnToAccessorMap) {
    this(objectClass, columnToAccessorMap, "", Collections.emptyMap());
  }


  /**
   * Gets the accessor of the given columnName. ColumnName name is regarded as canonical name.
   *
   * @param columnName
   * @return
   */
  public Accessor get(String columnName) {
    String cn = toCanonicalCase(columnName);
    Accessor ret = columnToAccessorMap.get(cn);
    return ret != null ? ret : aliasColumnToAccessorMap.get(cn);
  }



  @Override
  public String toString() {
    String keySetString =
        columnToAccessorMap.keySet().stream().sorted().collect(Collectors.toList()).toString();
    return "columns " + keySetString + System.lineSeparator()
        + String.join(System.lineSeparator(), columnToAccessorMap.keySet().stream()
            .map(e -> "  Column " + e + " => " + columnToAccessorMap.get(e).getFormattedString())
            .collect(Collectors.toList()))
        + System.lineSeparator() + "  OrmColumnAliasPrefix is [" + columnAliasPrefix + "]"
        + (columnAliasPrefix.length() == 0 ? "" : System.lineSeparator())
        + String.join(System.lineSeparator(), aliasColumnToAccessorMap.keySet().stream().map(
            e -> "  Column " + e + " => " + aliasColumnToAccessorMap.get(e).getFormattedString())
            .collect(Collectors.toList()));

  }

}
