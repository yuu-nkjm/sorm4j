package org.nkjmlab.sorm4j.mapping;

public final class ColumnOnTable extends Column {

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
