package org.nkjmlab.sorm4j.sql.metadata.jdbc;

public interface JdbcTableMetaData {

  String getTableCat();

  String getTableSchem();

  String getTableName();

  String getTableType();

  String getRemarks();

  String getTypeCat();

  String getTypeSchem();

  String getTypeName();

  String getSelfReferencingColName();

  String getRefGeneration();
}
