package org.nkjmlab.sorm4j.result.jdbc;

public interface JdbcColumnMetaData {

  String getTableCatalog();

  String getTableSchema();

  String getTableName();

  int getDataType();

  int getColumnSize();

  int getDecimalDigits();

  int getNumPrecRadix();

  int getOrdinalPosition();

  int getNullableFlag();

  int getCharOctetLength();

  String getIsNullable();

  String getColumnDefault();

  String getRemarks();

  String getIsAutoIncremented();

  String getIsGenerated();

  String getTypeName();

  String getColumnName();
}
