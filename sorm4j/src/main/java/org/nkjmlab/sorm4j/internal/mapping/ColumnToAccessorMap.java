package org.nkjmlab.sorm4j.internal.mapping;

import static org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils.*;
import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.extension.Accessor;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;

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

  public String getColumnAliasPrefix() {
    return columnAliasPrefix;
  }

  final void setValue(Object object, String columnName, Object value) {
    final Accessor acc = get(columnName);
    if (acc == null) {
      throw new SormException(ParameterizedStringUtils.newString("Error: setting value [{}]"
          + " of type [{}] in [{}]"
          + " because column [{}] does not have a corresponding setter method or field access =>[{}]",
          value, value.getClass().getSimpleName(), object.getClass().getName(), columnName,
          columnToAccessorMap.toString()));
    }
    try {
      acc.set(object, value);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new SormException(newString(
          "Could not set a value for column [{}] to instance of [{}] with [{}]. The value is=[{}]",
          columnName, object == null ? "null" : object.getClass().getSimpleName(),
          acc.getFormattedString(), value), e);

    }
  }

  private final Accessor getAccessor(Object object, String columnName) {
    final Accessor acc = get(columnName);
    if (acc == null) {
      throw new SormException(newString(
          "Error: getting value from [{}] because column [{}] does not have a corresponding getter method or field access. {}",
          object.getClass(), columnName, this));
    }
    return acc;
  }

  final Object getValue(Object object, String columnName) {
    Accessor acc = getAccessor(object, columnName);
    try {
      return acc.get(object);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new SormException(ParameterizedStringUtils.newString(
          "Could not get a value from instance of [{}] for column [{}] with [{}] The instance is =[{}]",
          (object == null ? "null" : object.getClass().getName()), acc.getFormattedString(),
          acc.getFormattedString(), object), e);
    }
  }

}
