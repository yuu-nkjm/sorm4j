package org.nkjmlab.sorm4j.mapping;

import org.nkjmlab.sorm4j.config.DefaultSqlToJavaDataConverter;

public final class ColumnOnTable extends Column {

  private int dataType;

  public ColumnOnTable(String name, int dataType) {
    super(name);
    this.dataType = dataType;
  }

  @Override
  public String toString() {
    return getName() + "(" + DefaultSqlToJavaDataConverter.sqlTypeToString(dataType) + ")";
  }
}
