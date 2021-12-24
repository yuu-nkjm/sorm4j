package org.nkjmlab.sorm4j.internal.mapping;

import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SormOptions;

abstract class Mapping<T> {

  private final Class<T> objectClass;
  final SormOptions options;
  final ResultSetConverter resultSetConverter;
  private final ColumnToAccessorMap columnToAccessorMap;

  Mapping(SormOptions options, ResultSetConverter resultSetConverter, Class<T> objectClass,
      ColumnToAccessorMap columnToAccessorMap) {
    this.options = options;
    this.resultSetConverter = resultSetConverter;
    this.objectClass = objectClass;
    this.columnToAccessorMap = columnToAccessorMap;
  }

  @Override
  public String toString() {
    return "Mapping [objectClass=" + objectClass.getName() + ", columnToAccessorMap="
        + columnToAccessorMap.toString() + "]";
  }

  Class<T> getObjectClass() {
    return objectClass;
  }

  String getColumnToAccessorString() {
    return "[" + objectClass.getName() + "] is mapped to " + columnToAccessorMap.toString();
  }

  ColumnToAccessorMap getColumnToAccessorMap() {
    return columnToAccessorMap;
  }


}
