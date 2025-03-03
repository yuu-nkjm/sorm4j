package org.nkjmlab.sorm4j.internal.jdbc_metadata;

import org.nkjmlab.sorm4j.jdbc_metadata.JdbcForeignKeyMetaData;

public class JdbcForeignKeyMetaDataImpl implements JdbcForeignKeyMetaData {
  private final String fkTable;
  private final String fkColumn;
  private final String pkTable;
  private final String pkColumn;
  private final short updateRule;
  private final short deleteRule;

  public JdbcForeignKeyMetaDataImpl(
      String fkTable,
      String fkColumn,
      String pkTable,
      String pkColumn,
      short updateRule,
      short deleteRule) {
    this.fkTable = fkTable;
    this.fkColumn = fkColumn;
    this.pkTable = pkTable;
    this.pkColumn = pkColumn;
    this.updateRule = updateRule;
    this.deleteRule = deleteRule;
  }

  @Override
  public String getFkTable() {
    return fkTable;
  }

  @Override
  public String getFkColumn() {
    return fkColumn;
  }

  @Override
  public String getPkTable() {
    return pkTable;
  }

  @Override
  public String getPkColumn() {
    return pkColumn;
  }

  @Override
  public short getUpdateRule() {
    return updateRule;
  }

  @Override
  public short getDeleteRule() {
    return deleteRule;
  }
}
