package org.nkjmlab.sorm4j.internal.mapping;

import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.extension.Accessor;
import org.nkjmlab.sorm4j.internal.util.StringUtils;

public final class ColumnToAccessorMap {

  private final Map<String, Accessor> columnToAccessorMap;
  private final Map<String, Accessor> aliasColumnToAccessorMap;
  private final String columnAliasPrefix;

  /**
   * ColumnName map to accessor. A key converted to a canonical name.
   *
   * @param columnToAccessorMap
   */
  public ColumnToAccessorMap(Class<?> objectClass, Map<String, Accessor> columnToAccessorMap) {

    this.columnToAccessorMap = columnToAccessorMap.entrySet().stream()
        .collect(Collectors.toMap(e -> toCanonicalCase(e.getKey()), e -> e.getValue()));

    this.columnAliasPrefix =
        Optional.ofNullable(objectClass.getAnnotation(OrmColumnAliasPrefix.class))
            .map(a -> a.value()).orElse("");
    if (columnAliasPrefix.length() == 0) {
      this.aliasColumnToAccessorMap = new HashMap<>();
      return;
    }

    Map<String, Accessor> tmp = new HashMap<>();

    for (String key : columnToAccessorMap.keySet()) {
      String aKey = toCanonicalCase(columnAliasPrefix + key);
      if (this.columnToAccessorMap.containsKey(aKey)) {
        throw new SormException(StringUtils.format(
            "Modify table alias because table alias [{}] and column [{}] is concatenated and it becomes duplicated column",
            columnAliasPrefix, key));
      }
      tmp.put(aKey, columnToAccessorMap.get(key));
    }

    this.aliasColumnToAccessorMap = tmp;

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
    return "COLUMNS " + keySetString + System.lineSeparator() + String.join(System.lineSeparator(),
        columnToAccessorMap.keySet().stream()
            .map(e -> "  COLUMN " + e + " => " + columnToAccessorMap.get(e).getFormattedString())
            .collect(Collectors.toList()))
        + System.lineSeparator() + "  DETAIL =>" + columnToAccessorMap.values()
        + System.lineSeparator() + "  OrmColumnAliasPrefix is [" + columnAliasPrefix + "]"
        + System.lineSeparator()
        + String.join(System.lineSeparator(), aliasColumnToAccessorMap.keySet().stream().map(
            e -> "  COLUMN " + e + " => " + aliasColumnToAccessorMap.get(e).getFormattedString())
            .collect(Collectors.toList()));

  }

}