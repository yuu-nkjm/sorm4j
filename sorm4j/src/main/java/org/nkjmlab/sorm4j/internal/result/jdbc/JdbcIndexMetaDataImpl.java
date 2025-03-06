package org.nkjmlab.sorm4j.internal.result.jdbc;

import java.util.Map;

import org.nkjmlab.sorm4j.container.sql.metadata.jdbc.JdbcIndexMetaData;

public class JdbcIndexMetaDataImpl implements JdbcIndexMetaData {
  private final String tableCat;
  private final String tableSchem;
  private final String tableName;
  private final boolean nonUnique;
  private final String indexQualifier;
  private final String indexName;
  private final int type;
  private final int ordinalPosition;
  private final String columnName;
  private final String ascOrDesc;
  private final int cardinality;
  private final int pages;
  private final String filterCondition;

  public JdbcIndexMetaDataImpl(Map<String, Object> map) {
    this.tableCat = (String) map.get("TABLE_CAT");
    this.tableSchem = (String) map.get("TABLE_SCHEM");
    this.tableName = (String) map.get("TABLE_NAME");
    this.nonUnique = Boolean.TRUE.equals(map.get("NON_UNIQUE"));
    this.indexQualifier = (String) map.get("INDEX_QUALIFIER");
    this.indexName = (String) map.get("INDEX_NAME");
    this.type = toInt(map.get("TYPE"));
    this.ordinalPosition = toInt(map.get("ORDINAL_POSITION"));
    this.columnName = (String) map.get("COLUMN_NAME");
    this.ascOrDesc = (String) map.get("ASC_OR_DESC");
    this.cardinality = toInt(map.get("CARDINALITY"));
    this.pages = toInt(map.get("PAGES"));
    this.filterCondition = (String) map.get("FILTER_CONDITION");
  }

  private static int toInt(Object value) {
    return (value instanceof Number) ? ((Number) value).intValue() : 0;
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
  public boolean isNonUnique() {
    return nonUnique;
  }

  @Override
  public String getIndexQualifier() {
    return indexQualifier;
  }

  @Override
  public String getIndexName() {
    return indexName;
  }

  @Override
  public int getType() {
    return type;
  }

  @Override
  public int getOrdinalPosition() {
    return ordinalPosition;
  }

  @Override
  public String getColumnName() {
    return columnName;
  }

  @Override
  public String getAscOrDesc() {
    return ascOrDesc;
  }

  @Override
  public int getCardinality() {
    return cardinality;
  }

  @Override
  public int getPages() {
    return pages;
  }

  @Override
  public String getFilterCondition() {
    return filterCondition;
  }

  @Override
  public String toString() {
    return "JdbcIndexMetaDataImpl{"
        + "tableCat='"
        + tableCat
        + '\''
        + ", tableSchem='"
        + tableSchem
        + '\''
        + ", tableName='"
        + tableName
        + '\''
        + ", nonUnique="
        + nonUnique
        + ", indexQualifier='"
        + indexQualifier
        + '\''
        + ", indexName='"
        + indexName
        + '\''
        + ", type="
        + type
        + ", ordinalPosition="
        + ordinalPosition
        + ", columnName='"
        + columnName
        + '\''
        + ", ascOrDesc='"
        + ascOrDesc
        + '\''
        + ", cardinality="
        + cardinality
        + ", pages="
        + pages
        + ", filterCondition='"
        + filterCondition
        + '\''
        + '}';
  }
}
