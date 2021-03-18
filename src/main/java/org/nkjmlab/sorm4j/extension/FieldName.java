package org.nkjmlab.sorm4j.extension;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Value object of feild name.
 *
 * @author nkjm
 *
 */

public final class FieldName {

  private final String name;

  public FieldName(Field field) {
    this(field.getName());
  }

  public FieldName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof FieldName))
      return false;
    FieldName other = (FieldName) obj;
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
