package org.nkjmlab.sorm4j.extension;

import java.util.Objects;

/**
 * Value object of column.
 *
 * @author nkjm
 *
 */
public class Column implements Comparable<Column> {

  private final String name;

  public Column(String name) {
    this.name = name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof Column))
      return false;
    Column other = (Column) obj;
    return Objects.equals(name, other.name);
  }

  @Override
  public int compareTo(Column o) {
    return name.compareTo(o.name);
  }

  @Override
  public String toString() {
    return name;
  }

  public String getName() {
    return name;
  }

}
