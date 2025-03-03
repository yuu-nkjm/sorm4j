package org.nkjmlab.sorm4j.common;

public class ColumnMetaDataImpl implements ColumnMetaData {
  /**
   * The name of the column. Corresponds to {@code COLUMN_NAME} in {@link
   * java.sql.DatabaseMetaData#getColumns}.
   */
  private final String columnName;

  /**
   * The database-specific type name of the column. Corresponds to {@code TYPE_NAME} in {@link
   * java.sql.DatabaseMetaData#getColumns}.
   */
  private final String typeName;

  public ColumnMetaDataImpl(String columnName, String typeName) {
    this.columnName = columnName;
    this.typeName = typeName;
  }

  @Override
  public int compareTo(ColumnMetaData o) {
    return columnName.compareTo(o.getColumnName());
  }

  @Override
  public String getTypeName() {
    return typeName;
  }

  @Override
  public String getColumnName() {
    return columnName;
  }
}
