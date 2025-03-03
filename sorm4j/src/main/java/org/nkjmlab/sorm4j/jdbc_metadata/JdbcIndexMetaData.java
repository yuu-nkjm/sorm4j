package org.nkjmlab.sorm4j.jdbc_metadata;

public interface JdbcIndexMetaData {
  String getTableCat();

  String getTableSchem();

  String getTableName();

  boolean isNonUnique();

  String getIndexQualifier();

  String getIndexName();

  int getType();

  int getOrdinalPosition();

  String getColumnName();

  String getAscOrDesc();

  int getCardinality();

  int getPages();

  String getFilterCondition();
}
