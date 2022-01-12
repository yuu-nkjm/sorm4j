package org.nkjmlab.sorm4j.mapping;

import java.util.Objects;

/**
 * Value object of table name.
 *
 * @author nkjm
 *
 */
public final class TableName {

  private final String name;

  public TableName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof TableName))
      return false;
    TableName other = (TableName) obj;
    return Objects.equals(name, other.name);
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
    return Objects.hash(name);
  }

  /**
   * Uses {@link #getName()} when you want to get name.
   */
  @Override
  public String toString() {
    return name;
  }

}
