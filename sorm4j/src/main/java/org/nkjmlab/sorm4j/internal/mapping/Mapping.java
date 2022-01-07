package org.nkjmlab.sorm4j.internal.mapping;

import org.nkjmlab.sorm4j.extension.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.extension.SormOptions;

abstract class Mapping<T> {

  private final Class<T> objectClass;
  final SormOptions options;
  final ColumnValueToJavaObjectConverters columnValueConverter;
  private final ColumnToAccessorMap columnToAccessorMap;

  Mapping(SormOptions options, ColumnValueToJavaObjectConverters columnValueConverter, Class<T> objectClass,
      ColumnToAccessorMap columnToAccessorMap) {
    this.options = options;
    this.columnValueConverter = columnValueConverter;
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
