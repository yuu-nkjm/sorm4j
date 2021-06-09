package org.nkjmlab.sorm4j.extension;

import java.util.Objects;

/**
 * Value object of column.
 *
 * @author nkjm
 *
 */
public class ColumnName implements Comparable<ColumnName> {

  private final String name;

  public ColumnName(String name) {
    this.name = name;
  }

  @Override
  public int compareTo(ColumnName o) {
    return name.compareTo(o.name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof ColumnName))
      return false;
    ColumnName other = (ColumnName) obj;
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
