package org.nkjmlab.sorm4j.internal.mapping;

import static org.nkjmlab.sorm4j.internal.util.StringUtils.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.extension.Accessor;

public final class ColumnToAccessorMap {
  private final Map<String, Accessor> columnToAccessorMap;

  /**
   * Column map to accessor. A key converted to a canonical name.
   *
   * @param columnToAccessorMap
   */
  public ColumnToAccessorMap(Map<String, Accessor> columnToAccessorMap) {
    this.columnToAccessorMap = columnToAccessorMap.entrySet().stream()
        .collect(Collectors.toMap(e -> toCanonical(e.getKey()), e -> e.getValue()));
  }

  /**
   * Gets the accessor of the given columnName. Column name is regarded as canonical name.
   *
   * @param columnName
   * @return
   */
  public Accessor get(String columnName) {
    return columnToAccessorMap.get(toCanonical(columnName));
  }

  public Set<String> keySet() {
    return columnToAccessorMap.keySet();
  }

  public Collection<Accessor> values() {
    return columnToAccessorMap.values();
  }

}
