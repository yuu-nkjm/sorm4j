package org.nkjmlab.sorm4j.internal.sql;

/**
 * Value object of table name.
 *
 * @author nkjm
 */
public final class TableName implements Comparable<TableName> {

  private final String name;

  private TableName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof TableName)) return false;
    TableName other = (TableName) obj;
    return name.equals(other.name);
  }

  /**
   * Gets name of this object.
   *
   * @return
   */
  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  /** Uses {@link #getName()} when you want to get name. */
  @Override
  public String toString() {
    return name;
  }

  public static TableName of(String tableName) {
    return new TableName(tableName);
  }

  @Override
  public int compareTo(TableName other) {
    return this.name.compareTo(other.name);
  }
}
