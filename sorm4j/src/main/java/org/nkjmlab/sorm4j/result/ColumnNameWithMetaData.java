package org.nkjmlab.sorm4j.result;

import static org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils.*;
import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * ColumnName name and data type for message.
 *
 * @author nkjm
 *
 */
@Experimental
public class ColumnNameWithMetaData extends ColumnName {

  private final String msg;
  private final String typeName;
  private final int dataType;
  private final int ordinalPosition;
  private final String isAutoIncremented;
  private final String isNullable;
  private final String isGenerated;

  public ColumnNameWithMetaData(String name, int dataType, String typeName, int ordinalPosition,
      String isNullable, String isAutoIncremented, String isGenerated) {
    super(name);
    this.typeName = typeName;
    this.dataType = dataType;
    this.ordinalPosition = ordinalPosition;
    this.isNullable = isNullable;
    this.isAutoIncremented = isAutoIncremented;
    this.isGenerated = isGenerated;
    this.msg = newString("{{}: {} [{}({})] [n={},a={},g={}]}",
        String.format("%02d", getOrdinalPosition()), getName(), getTypeName(), getDataType(),
        getIsNullable(), getIsAutoIncremented(), getIsGenerated());
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
}
