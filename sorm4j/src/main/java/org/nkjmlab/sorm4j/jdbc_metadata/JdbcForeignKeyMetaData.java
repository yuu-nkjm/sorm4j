package org.nkjmlab.sorm4j.jdbc_metadata;

public interface JdbcForeignKeyMetaData {

  String getFkTable();

  String getFkColumn();

  String getPkTable();

  String getPkColumn();

  short getUpdateRule();

  short getDeleteRule();
}
