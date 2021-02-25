package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.util.StringUtils.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class ColumnToAccessorMap {
  // key => UPPER COLUMN_NAME
  private final Map<String, Accessor> columnToAccessorMap;

  public ColumnToAccessorMap(Map<String, Accessor> columnToAccessorMap) {
    this.columnToAccessorMap = columnToAccessorMap.entrySet().stream()
        .collect(Collectors.toMap(e -> toUpperCase(e.getKey()), e -> e.getValue()));
  }

  public Accessor get(String columnName) {
    Accessor v = columnToAccessorMap.get(columnName);
    return v != null ? v : columnToAccessorMap.get(toUpperCase(columnName));
  }

  public Set<String> keySet() {
    return columnToAccessorMap.keySet();
  }

  public Collection<Accessor> values() {
    return columnToAccessorMap.values();
  }

}
