package org.nkjmlab.sorm4j.mapping;

final class ColumnOnTable extends Column {

  private int dataType;

  public ColumnOnTable(String name, int dataType) {
    super(name);
    this.dataType = dataType;
  }

  @Override
  public String toString() {
    return getName() + "(" + DefaultResultSetValueGetter.sqlTypeToString(dataType) + ")";
  }
}
