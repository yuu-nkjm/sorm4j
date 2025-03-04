package org.nkjmlab.sorm4j.result.jdbc;

public interface JdbcForeignKeyMetaData {

  String getFkTable();

  String getFkColumn();

  String getPkTable();

  String getPkColumn();

  short getUpdateRule();

  short getDeleteRule();
}
