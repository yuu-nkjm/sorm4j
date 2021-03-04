package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.util.StringUtils.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.config.ColumnFieldMapper;
import org.nkjmlab.sorm4j.util.StringUtils;

abstract class Mapping<T> {
  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  protected final Class<T> objectClass;
  protected final ResultSetConverter resultSetConverter;
  protected final ColumnToAccessorMap columnToAccessorMap;

  public Mapping(ResultSetConverter sqlToJavaConverter, Class<T> objectClass,
      ColumnFieldMapper columnFieldMapper) {
    this.resultSetConverter = sqlToJavaConverter;
    this.objectClass = objectClass;
    this.columnToAccessorMap = columnFieldMapper.createColumnToAccessorMap(objectClass);
  }


  public Mapping(ResultSetConverter sqlToJavaConverter, Class<T> objectClass, List<Column> columns,
      ColumnFieldMapper columnFieldMapper) {
    this.resultSetConverter = sqlToJavaConverter;
    this.objectClass = objectClass;
    this.columnToAccessorMap = columnFieldMapper.createColumnToAccessorMap(objectClass, columns);
  }


  final Object getValue(Object object, String columnName) {
    final Accessor acc = columnToAccessorMap.get(columnName);
    if (acc == null) {
      throw new OrmException(format(
          "Error getting value from [{}] because column [{}] does not have a corresponding getter method or field access",
          object.getClass(), columnName));
    }
    try {
      return acc.get(object);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new OrmException(format(
          "Could not get a value from instance of [{}] for column [{}] with [{}] The instance is =[{}]",
          (object == null ? "null" : object.getClass().getName()), columnName,
          acc.getFormattedString(), object), e);
    }
  }


  final void setValue(Object object, String columnName, Object value) {
    final Accessor acc = columnToAccessorMap.get(columnName);
    if (acc == null) {
      throw new OrmException(StringUtils.format("Error setting value [{}]" + " of type [{}] in [{}]"
          + " because column [{}] does not have a corresponding setter method or field access =>[{}]",
          value, value.getClass().getSimpleName(), object.getClass().getName(), columnName,
          columnToAccessorMap.keySet()));
    }
    try {
      acc.set(object, value);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new OrmException(format(
          "Could not set a value for column [{}] to instance of [{}] with [{}]. The value is=[{}]",
          columnName, object == null ? "null" : object.getClass().getSimpleName(),
          acc.getFormattedString(), value), e);

    }
  }

  @Override
  public String toString() {
    return "Mapping [objectClass=" + objectClass.getName() + ", columnToAccessorMap="
        + columnToAccessorMap.values() + "]";
  }

  protected String getColumnToAccessorString() {
    List<String> columnStrs =
        columnToAccessorMap.keySet().stream().sorted().collect(Collectors.toList());
    return "COLUMNS " + columnStrs + " is mapped to [" + objectClass.getName() + "]"
        + System.lineSeparator()
        + String.join(System.lineSeparator(),
            columnStrs.stream()
                .map(e -> "  COLUM " + e + " => " + columnToAccessorMap.get(e).getFormattedString())
                .collect(Collectors.toList()));
  }


}
