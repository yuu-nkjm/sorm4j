package org.nkjmlab.sorm4j.jdbc_metadata;

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
