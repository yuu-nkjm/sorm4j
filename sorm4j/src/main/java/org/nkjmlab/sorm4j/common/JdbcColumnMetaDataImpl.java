package org.nkjmlab.sorm4j.common;

import java.util.Objects;

import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;

public class JdbcColumnMetaDataImpl extends ColumnMetaDataImpl implements JdbcColumnMetaData {
  /**
   * The catalog name of the table. Corresponds to {@code TABLE_CAT} in {@link
   * java.sql.DatabaseMetaData#getColumns}.
   */
  private final String tableCatalog;

  /**
   * The schema name of the table. Corresponds to {@code TABLE_SCHEM} in {@link
   * java.sql.DatabaseMetaData#getColumns}.
   */
  private final String tableSchema;

  /**
   * The name of the table. Corresponds to {@code TABLE_NAME} in {@link
   * java.sql.DatabaseMetaData#getColumns}.
   */
  private final String tableName;

  /**
   * The SQL data type of the column as defined in {@link java.sql.Types}. Corresponds to {@code
   * DATA_TYPE} in {@link java.sql.DatabaseMetaData#getColumns}.
   */
  private final int dataType;

  /**
   * The column size. For numeric data, this is the precision. Corresponds to {@code COLUMN_SIZE} in
   * {@link java.sql.DatabaseMetaData#getColumns}.
   */
  private final int columnSize;

  /**
   * The numeric precision radix (10 or 2). Corresponds to {@code NUM_PREC_RADIX} in {@link
   * java.sql.DatabaseMetaData#getColumns}.
   */
  private final int numPrecRadix;

  /**
   * The number of fractional digits (for numeric types). Corresponds to {@code DECIMAL_DIGITS} in
   * {@link java.sql.DatabaseMetaData#getColumns}.
   */
  private final int decimalDigits;

  /**
   * The position of the column in the table (starting from 1). Corresponds to {@code
   * ORDINAL_POSITION} in {@link java.sql.DatabaseMetaData#getColumns}.
   */
  private final int ordinalPosition;

  /**
   * The nullability flag as an integer. 0 = {@code columnNoNulls}, 1 = {@code columnNullable}, 2 =
   * {@code columnNullableUnknown}. Corresponds to {@code NULLABLE} in {@link
   * java.sql.DatabaseMetaData#getColumns}.
   */
  private final int nullableFlag;

  /**
   * The maximum length (in bytes) for character and binary columns. Corresponds to {@code
   * CHAR_OCTET_LENGTH} in {@link java.sql.DatabaseMetaData#getColumns}.
   */
  private final int charOctetLength;

  /**
   * Indicates whether the column allows NULL values. "YES" = nullable, "NO" = not nullable.
   * Corresponds to {@code IS_NULLABLE} in {@link java.sql.DatabaseMetaData#getColumns}.
   */
  private final String isNullable;

  /**
   * The default value of the column, if specified. Corresponds to {@code COLUMN_DEF} in {@link
   * java.sql.DatabaseMetaData#getColumns}.
   */
  private final String columnDefault;

  /**
   * The description or comments for the column. Corresponds to {@code REMARKS} in {@link
   * java.sql.DatabaseMetaData#getColumns}.
   */
  private final String remarks;

  /**
   * Indicates whether the column is auto-incremented. "YES" = auto-increment, "NO" = not
   * auto-increment. Corresponds to {@code IS_AUTOINCREMENT} in {@link
   * java.sql.DatabaseMetaData#getColumns}.
   */
  private final String isAutoIncremented;

  /**
   * Indicates whether the column is a generated column. "YES" = generated, "NO" = not generated.
   * Corresponds to {@code IS_GENERATEDCOLUMN} in {@link java.sql.DatabaseMetaData#getColumns}.
   */
  private final String isGenerated;

  private final String msg;

  public JdbcColumnMetaDataImpl(
      String tableCatalog,
      String tableSchema,
      String tableName,
      String columnName,
      int dataType,
      String typeName,
      int columnSize,
      int numPrecRadix,
      int decimalDigits,
      int ordinalPosition,
      int nullableFlag,
      int charOctetLength,
      String isNullable,
      String columnDefault,
      String remarks,
      String isAutoIncremented,
      String isGenerated) {
    super(columnName, typeName);
    this.tableCatalog = tableCatalog;
    this.tableSchema = tableSchema;
    this.tableName = tableName;
    this.dataType = dataType;
    this.columnSize = columnSize;
    this.decimalDigits = decimalDigits;
    this.numPrecRadix = numPrecRadix;
    this.ordinalPosition = ordinalPosition;
    this.nullableFlag = nullableFlag;
    this.charOctetLength = charOctetLength;
    this.isNullable = isNullable;
    this.columnDefault = columnDefault;
    this.remarks = remarks;
    this.isAutoIncremented = isAutoIncremented;
    this.isGenerated = isGenerated;

    Object[] params = {
      tableCatalog,
      tableSchema,
      tableName,
      String.format("%02d", ordinalPosition),
      columnName,
      typeName,
      dataType,
      columnSize,
      decimalDigits,
      numPrecRadix,
      charOctetLength,
      nullableFlag,
      isNullable,
      columnDefault,
      remarks,
      isAutoIncremented,
      isGenerated
    };
    this.msg =
        ParameterizedStringFormatter.LENGTH_256.format(
            "{{}.{}.{}: {} {} [{}({})] [size={}, decimal={}, radix={}, octetLen={}, nullableFlag={}] "
                + "[n={}, default='{}', comment='{}'] [a={}, g={}]}",
            params);
  }

  public String getTableCatalog() {
    return tableCatalog;
  }

  public String getTableSchema() {
    return tableSchema;
  }

  public String getTableName() {
    return tableName;
  }

  public int getDataType() {
    return dataType;
  }

  public int getColumnSize() {
    return columnSize;
  }

  public int getDecimalDigits() {
    return decimalDigits;
  }

  public int getNumPrecRadix() {
    return numPrecRadix;
  }

  public int getOrdinalPosition() {
    return ordinalPosition;
  }

  public int getNullableFlag() {
    return nullableFlag;
  }

  public int getCharOctetLength() {
    return charOctetLength;
  }

  public String getIsNullable() {
    return isNullable;
  }

  public String getColumnDefault() {
    return columnDefault;
  }

  public String getRemarks() {
    return remarks;
  }

  public String getIsAutoIncremented() {
    return isAutoIncremented;
  }

  public String getIsGenerated() {
    return isGenerated;
  }

  @Override
  public String toString() {
    return msg;
  }

  @Override
  public int hashCode() {
    return super.hashCode()
        ^ Objects.hash(
            tableCatalog,
            tableSchema,
            tableName,
            dataType,
            columnSize,
            decimalDigits,
            numPrecRadix,
            ordinalPosition,
            nullableFlag,
            charOctetLength,
            isNullable,
            columnDefault,
            remarks,
            isAutoIncremented,
            isGenerated);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof JdbcColumnMetaDataImpl)) return false;
    JdbcColumnMetaDataImpl other = (JdbcColumnMetaDataImpl) obj;
    if (!super.equals(obj)) return false;
    return super.equals(obj)
        && dataType == other.dataType
        && columnSize == other.columnSize
        && decimalDigits == other.decimalDigits
        && numPrecRadix == other.numPrecRadix
        && ordinalPosition == other.ordinalPosition
        && nullableFlag == other.nullableFlag
        && charOctetLength == other.charOctetLength
        && Objects.equals(tableCatalog, other.tableCatalog)
        && Objects.equals(tableSchema, other.tableSchema)
        && Objects.equals(tableName, other.tableName)
        && Objects.equals(isNullable, other.isNullable)
        && Objects.equals(columnDefault, other.columnDefault)
        && Objects.equals(remarks, other.remarks)
        && Objects.equals(isAutoIncremented, other.isAutoIncremented)
        && Objects.equals(isGenerated, other.isGenerated);
  }
}
