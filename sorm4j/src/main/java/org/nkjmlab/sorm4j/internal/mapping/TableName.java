package org.nkjmlab.sorm4j.internal.mapping;

/**
 * Value object of table name.
 *
 * @author nkjm
 */
public final class TableName {

  private final String name;

  public TableName(String name) {
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
}
