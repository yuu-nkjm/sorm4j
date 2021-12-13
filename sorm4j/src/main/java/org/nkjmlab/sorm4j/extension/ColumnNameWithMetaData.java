package org.nkjmlab.sorm4j.extension;

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
//  private final String typeName;
//  private final int ordinalPosition;
//  private final String isAutoIncremented;
//  private final String isNullable;
//  private final String isGenerated;

  public ColumnNameWithMetaData(String name, int dataType, String typeName, int ordinalPosition,
      String isNullable, String isAutoIncremented, String isGenerated) {
    super(name);
    // this.typeName = typeName;
    // this.ordinalPosition = ordinalPosition;
    // this.isNullable = isNullable;
    // this.isAutoIncremented = isAutoIncremented;
    // this.isGenerated = isGenerated;
    this.msg = newString("{{}: {} [{}({})] [n={},a={},g={}]}", String.format("%02d", ordinalPosition),
        name, typeName, dataType, isNullable, isAutoIncremented, isGenerated);
  }

  @Override
  public String toString() {
    return msg;
  }
}
