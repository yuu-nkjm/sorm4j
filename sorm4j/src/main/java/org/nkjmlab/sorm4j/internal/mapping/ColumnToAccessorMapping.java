package org.nkjmlab.sorm4j.internal.mapping;

import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.context.FieldAccessor;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;

public final class ColumnToAccessorMapping {

  private final Map<String, FieldAccessor> columnToAccessorMap;
  private final Map<String, FieldAccessor> aliasColumnToAccessorMap;
  private final String columnAliasPrefix;

  /**
   * ColumnName map to accessor. A key converted to a canonical name.
   *
   * @param columnToAccessorMap
   */
  public ColumnToAccessorMapping(Class<?> objectClass,
      Map<String, FieldAccessor> columnToAccessorMap, String columnAliasPrefix) {
    this.columnToAccessorMap = columnToAccessorMap.entrySet().stream()
        .collect(Collectors.toMap(e -> toCanonicalCase(e.getKey()), e -> e.getValue()));
    this.columnAliasPrefix = columnAliasPrefix;

    this.aliasColumnToAccessorMap =
        columnAliasPrefix == null || columnAliasPrefix.length() == 0 ? Collections.emptyMap()
            : createAliasAccessors(columnAliasPrefix, columnToAccessorMap).entrySet().stream()
                .collect(Collectors.toMap(e -> toCanonicalCase(e.getKey()), e -> e.getValue()));
  }



  /**
   * Gets the accessor of the given columnName. ColumnName name is regarded as canonical name.
   *
   * @param columnName
   * @return
   */
  public FieldAccessor get(String columnName) {
    String cn = toCanonicalCase(columnName);
    FieldAccessor ret = columnToAccessorMap.get(cn);
    return ret != null ? ret : aliasColumnToAccessorMap.get(cn);
  }



  @Override
  public String toString() {
    List<String> keySet =
        columnToAccessorMap.keySet().stream().sorted().collect(Collectors.toList());
    List<String> aliasKeySet =
        aliasColumnToAccessorMap.keySet().stream().sorted().collect(Collectors.toList());
    return "columns " + keySet + System.lineSeparator() + String.join(System.lineSeparator(),
        keySet.stream()
            .map(e -> "  CsvColumn " + e + " <=> " + columnToAccessorMap.get(e).getFormattedString())
            .collect(Collectors.toList()))
        + System.lineSeparator() + "column aliases " + aliasKeySet + System.lineSeparator()
        + String.join(System.lineSeparator(), aliasKeySet.stream().map(
            e -> "  CsvColumn " + e + " <=> " + aliasColumnToAccessorMap.get(e).getFormattedString())
            .collect(Collectors.toList()));

  }

  public String getColumnAliasPrefix() {
    return columnAliasPrefix;
  }

  public final void setValue(Object object, String columnName, Object value) {
    final FieldAccessor acc = get(columnName);
    if (acc == null) {
      Object[] params = {value, value.getClass().getSimpleName(), object.getClass().getName(), columnName, columnToAccessorMap.toString()};
      throw new SormException(ParameterizedStringFormatter.LENGTH_256.format("Error: setting value [{}]"
      + " of type [{}] in [{}]"
      + " because column [{}] does not have a corresponding setter method or field access =>[{}]", params));
    }
    try {
      acc.set(object, value);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      Object[] params = {columnName, object == null ? "null" : object.getClass().getSimpleName(), acc.getFormattedString(), value};
      throw new SormException(ParameterizedStringFormatter.LENGTH_256.format("Could not set a value for column [{}] to instance of [{}] with [{}]. The value is=[{}]", params), e);

    }
  }

  private final FieldAccessor getAccessor(Object object, String columnName) {
    final FieldAccessor acc = get(columnName);
    if (acc == null) {
      Object[] params = {object.getClass(), columnName, this};
      throw new SormException(ParameterizedStringFormatter.LENGTH_256.format("Error: getting value from [{}] because column [{}] does not have a corresponding getter method or field access. {}", params));
    }
    return acc;
  }

  public final Object getValue(Object object, String columnName) {
    FieldAccessor acc = getAccessor(object, columnName);
    try {
      return acc.get(object);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      Object[] params = {(object == null ? "null" : object.getClass().getName()), acc.getFormattedString(), acc.getFormattedString(), object};
      throw new SormException(ParameterizedStringFormatter.LENGTH_256.format("Could not get a value from instance of [{}] for column [{}] with [{}] The instance is =[{}]", params), e);
    }
  }


  public Set<String> keySet() {
    return columnToAccessorMap.keySet();
  }


  private Map<String, FieldAccessor> createAliasAccessors(String prefix,
      Map<String, FieldAccessor> accessors) {
    if (prefix.length() == 0) {
      return Collections.emptyMap();
    }

    Map<String, FieldAccessor> ret = new HashMap<>();

    for (String key : accessors.keySet()) {
      String aKey = toCanonicalCase(prefix + key);
      if (accessors.containsKey(aKey)) {
        Object[] params = {prefix, key};
        throw new SormException(ParameterizedStringFormatter.LENGTH_256.format("Modify table alias because table alias [{}] and column [{}] is concatenated and it becomes duplicated column", params));
      }
      ret.put(aKey, accessors.get(key));
    }
    return ret;
  }



}
