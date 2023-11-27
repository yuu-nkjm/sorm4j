package org.nkjmlab.sorm4j.common;

import java.util.Objects;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;

/**
 * ColumnName name and data type for message.
 *
 * @author yuu_nkjm
 */
public final class ColumnMetaData implements Comparable<ColumnMetaData> {

  private final String msg;
  private final String typeName;
  private final int dataType;
  private final int ordinalPosition;
  private final String isAutoIncremented;
  private final String isNullable;
  private final String isGenerated;
  private final String name;

  public ColumnMetaData(
      String name,
      int dataType,
      String typeName,
      int ordinalPosition,
      String isNullable,
      String isAutoIncremented,
      String isGenerated) {
    this.name = name;
    this.typeName = typeName;
    this.dataType = dataType;
    this.ordinalPosition = ordinalPosition;
    this.isNullable = isNullable;
    this.isAutoIncremented = isAutoIncremented;
    this.isGenerated = isGenerated;
    Object[] params = {
      String.format("%02d", getOrdinalPosition()),
      getName(),
      getTypeName(),
      getDataType(),
      getIsNullable(),
      getIsAutoIncremented(),
      getIsGenerated()
    };
    this.msg =
        ParameterizedStringFormatter.LENGTH_256.format(
            "{{}: {} [{}({})] [n={},a={},g={}]}", params);
  }

  private int getDataType() {
    return dataType;
  }

  public String getTypeName() {
    return typeName;
  }

  public int getOrdinalPosition() {
    return ordinalPosition;
  }

  public String getIsAutoIncremented() {
    return isAutoIncremented;
  }

  public String getIsNullable() {
    return isNullable;
  }

  public String getIsGenerated() {
    return isGenerated;
  }

  @Override
  public String toString() {
    return msg;
  }

  @Override
  public int compareTo(ColumnMetaData o) {
    return name.compareTo(o.name);
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
    return Objects.hash(
        dataType, isAutoIncremented, isGenerated, isNullable, msg, name, ordinalPosition, typeName);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof ColumnMetaData)) return false;
    ColumnMetaData other = (ColumnMetaData) obj;
    return dataType == other.dataType
        && Objects.equals(isAutoIncremented, other.isAutoIncremented)
        && Objects.equals(isGenerated, other.isGenerated)
        && Objects.equals(isNullable, other.isNullable)
        && Objects.equals(msg, other.msg)
        && Objects.equals(name, other.name)
        && ordinalPosition == other.ordinalPosition
        && Objects.equals(typeName, other.typeName);
  }
}
