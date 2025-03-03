package org.nkjmlab.sorm4j.internal.jdbc_metadata;

import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;
import org.nkjmlab.sorm4j.jdbc_metadata.JdbcColumnMetaData;

public class JdbcColumnMetaDataImpl implements JdbcColumnMetaData {

  /**
   * The name of the column. Corresponds to {@code COLUMN_NAME} in {@link
   * java.sql.DatabaseMetaData#getColumns}.
   */
  private final String columnName;

  /**
   * The database-specific type name of the column. Corresponds to {@code TYPE_NAME} in {@link
   * java.sql.DatabaseMetaData#getColumns}.
   */
  private final String typeName;

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
    this.columnName = columnName;
    this.typeName = typeName;
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

  @Override
  public String getTableCatalog() {
    return tableCatalog;
  }

  @Override
  public String getTableSchema() {
    return tableSchema;
  }

  @Override
  public String getTableName() {
    return tableName;
  }

  @Override
  public int getDataType() {
    return dataType;
  }

  @Override
  public int getColumnSize() {
    return columnSize;
  }

  @Override
  public int getDecimalDigits() {
    return decimalDigits;
  }

  @Override
  public int getNumPrecRadix() {
    return numPrecRadix;
  }

  @Override
  public int getOrdinalPosition() {
    return ordinalPosition;
  }

  @Override
  public int getNullableFlag() {
    return nullableFlag;
  }

  @Override
  public int getCharOctetLength() {
    return charOctetLength;
  }

  @Override
  public String getIsNullable() {
    return isNullable;
  }

  @Override
  public String getColumnDefault() {
    return columnDefault;
  }

  @Override
  public String getRemarks() {
    return remarks;
  }

  @Override
  public String getIsAutoIncremented() {
    return isAutoIncremented;
  }

  @Override
  public String getIsGenerated() {
    return isGenerated;
  }

  @Override
  public String getTypeName() {
    return typeName;
  }

  @Override
  public String getColumnName() {
    return columnName;
  }

  @Override
  public String toString() {
    return msg;
  }
}
