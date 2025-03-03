package org.nkjmlab.sorm4j.internal.jdbc_metadata;

import java.util.Map;

import org.nkjmlab.sorm4j.jdbc_metadata.JdbcTableMetaData;

public class JdbcTableMetaDataImpl implements JdbcTableMetaData {
  private final String tableCat;
  private final String tableSchem;
  private final String tableName;
  private final String tableType;
  private final String remarks;
  private final String typeCat;
  private final String typeSchem;
  private final String typeName;
  private final String selfReferencingColName;
  private final String refGeneration;

  public JdbcTableMetaDataImpl(Map<String, Object> map) {
    this.tableCat = (String) map.get("TABLE_CAT");
    this.tableSchem = (String) map.get("TABLE_SCHEM");
    this.tableName = (String) map.get("TABLE_NAME");
    this.tableType = (String) map.get("TABLE_TYPE");
    this.remarks = (String) map.get("REMARKS");
    this.typeCat = (String) map.get("TYPE_CAT");
    this.typeSchem = (String) map.get("TYPE_SCHEM");
    this.typeName = (String) map.get("TYPE_NAME");
    this.selfReferencingColName = (String) map.get("SELF_REFERENCING_COL_NAME");
    this.refGeneration = (String) map.get("REF_GENERATION");
  }

  @Override
  public String getTableCat() {
    return tableCat;
  }

  @Override
  public String getTableSchem() {
    return tableSchem;
  }

  @Override
  public String getTableName() {
    return tableName;
  }

  @Override
  public String getTableType() {
    return tableType;
  }

  @Override
  public String getRemarks() {
    return remarks;
  }

  @Override
  public String getTypeCat() {
    return typeCat;
  }

  @Override
  public String getTypeSchem() {
    return typeSchem;
  }

  @Override
  public String getTypeName() {
    return typeName;
  }

  @Override
  public String getSelfReferencingColName() {
    return selfReferencingColName;
  }

  @Override
  public String getRefGeneration() {
    return refGeneration;
  }

  @Override
  public String toString() {
    return "JdbcTableMetaDataImpl{"
        + "tableCat='"
        + tableCat
        + '\''
        + ", tableSchem='"
        + tableSchem
        + '\''
        + ", tableName='"
        + tableName
        + '\''
        + ", tableType='"
        + tableType
        + '\''
        + ", remarks='"
        + remarks
        + '\''
        + ", typeCat='"
        + typeCat
        + '\''
        + ", typeSchem='"
        + typeSchem
        + '\''
        + ", typeName='"
        + typeName
        + '\''
        + ", selfReferencingColName='"
        + selfReferencingColName
        + '\''
        + ", refGeneration='"
        + refGeneration
        + '\''
        + '}';
  }
}
