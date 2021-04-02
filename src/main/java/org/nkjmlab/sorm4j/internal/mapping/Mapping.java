package org.nkjmlab.sorm4j.internal.mapping;

import static org.nkjmlab.sorm4j.internal.util.StringUtils.*;
import java.lang.reflect.InvocationTargetException;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.extension.Accessor;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.internal.util.StringUtils;

abstract class Mapping<T> {
  static final org.slf4j.Logger log = org.nkjmlab.sorm4j.internal.util.LoggerFactory.getLogger();

  private final Class<T> objectClass;
  protected final ResultSetConverter resultSetConverter;
  protected final ColumnToAccessorMap columnToAccessorMap;

  Mapping(ResultSetConverter resultSetConverter, Class<T> objectClass,
      ColumnToAccessorMap columnToAccessorMap) {
    this.resultSetConverter = resultSetConverter;
    this.objectClass = objectClass;
    this.columnToAccessorMap = columnToAccessorMap;
  }


  final Object getValue(Object object, Accessor acc) {
    try {
      return acc.get(object);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new SormException(format(
          "Could not get a value from instance of [{}] for column [{}] with [{}] The instance is =[{}]",
          (object == null ? "null" : object.getClass().getName()), acc.getFormattedString(),
          acc.getFormattedString(), object), e);
    }
  }

  final Accessor getAccessor(Object object, String columnName) {
    final Accessor acc = columnToAccessorMap.get(columnName);
    if (acc == null) {
      throw new SormException(format(
          "Error getting value from [{}] because column [{}] does not have a corresponding getter method or field access",
          object.getClass(), columnName));
    }
    return acc;
  }


  final void setValue(Object object, String columnName, Object value) {
    final Accessor acc = columnToAccessorMap.get(columnName);
    if (acc == null) {
      throw new SormException(StringUtils.format("Error setting value [{}]"
          + " of type [{}] in [{}]"
          + " because column [{}] does not have a corresponding setter method or field access =>[{}]",
          value, value.getClass().getSimpleName(), object.getClass().getName(), columnName,
          columnToAccessorMap.toString()));
    }
    try {
      acc.set(object, value);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new SormException(format(
          "Could not set a value for column [{}] to instance of [{}] with [{}]. The value is=[{}]",
          columnName, object == null ? "null" : object.getClass().getSimpleName(),
          acc.getFormattedString(), value), e);

    }
  }

  @Override
  public String toString() {
    return "Mapping [objectClass=" + objectClass.getName() + ", columnToAccessorMap="
        + columnToAccessorMap.toString() + "]";
  }

  public Class<T> getObjectClass() {
    return objectClass;
  }


  protected String getColumnToAccessorString() {
    return "[" + objectClass.getName() + "] is map to " + columnToAccessorMap.toString();
  }



}
