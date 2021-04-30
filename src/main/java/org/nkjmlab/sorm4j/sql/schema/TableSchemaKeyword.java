package org.nkjmlab.sorm4j.sql.schema;

public interface TableSchemaKeyword {

  /** Data type **/
  public static final String VARCHAR = "VARCHAR";
  public static final String CHAR = "CHAR";
  public static final String DATE = "DATE";
  public static final String TIME = "TIME";
  public static final String TIMESTAMP = "TIMESTAMP";
  public static final String TIMESTAMP_AS_CURRENT_TIMESTAMP = "TIMESTAMP AS CURRENT_TIMESTAMP";
  public static final String REAL = "REAL";
  public static final String DOUBLE = "DOUBLE";
  public static final String BIGINT = "BIGINT";
  public static final String INT = "INT";
  public static final String BOOLEAN = "BOOLEAN";
  public static final String DECIMAL = "DECIMAL";
  public static final String TINYINT = "TINYINT";
  public static final String SMALLINT = "SMALLINT";
  public static final String IDENTITY = "IDENTITY";

  /** Constraint and misc **/
  public static final String UNIQUE = "UNIQUE";
  public static final String NOT_NULL = "NOT NULL";
  public static final String PRIMARY_KEY = "PRIMARY KEY";
  public static final String AUTO_INCREMENT = "AUTO_INCREMENT";

}