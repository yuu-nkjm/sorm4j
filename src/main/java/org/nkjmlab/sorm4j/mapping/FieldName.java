package org.nkjmlab.sorm4j.mapping;

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
  public String toString() {
    return name;
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
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

}
