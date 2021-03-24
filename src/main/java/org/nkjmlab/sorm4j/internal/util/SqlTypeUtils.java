package org.nkjmlab.sorm4j.internal.util;

import java.util.HashMap;
import java.util.Map;

public final class SqlTypeUtils {

  private SqlTypeUtils() {}

  public static final String sqlTypeToString(final int type) {
    return typeStringMap.getOrDefault(type, "");
  }

  private static final Map<Integer, String> typeStringMap = initalizeTypeStringMap();

  private static Map<Integer, String> initalizeTypeStringMap() {
    Map<Integer, String> typeStringMap = new HashMap<>();
    typeStringMap.put(java.sql.Types.ARRAY, "ARRAY");
    typeStringMap.put(java.sql.Types.BIGINT, "BIGINT");
    typeStringMap.put(java.sql.Types.BINARY, "BINARY");
    typeStringMap.put(java.sql.Types.BIT, "BIT");
    typeStringMap.put(java.sql.Types.BLOB, "BLOB");
    typeStringMap.put(java.sql.Types.BOOLEAN, "BOOLEAN");
    typeStringMap.put(java.sql.Types.CHAR, "CHAR");
    typeStringMap.put(java.sql.Types.CLOB, "CLOB");
    typeStringMap.put(java.sql.Types.DATALINK, "DATALINK");
    typeStringMap.put(java.sql.Types.DATE, "DATE");
    typeStringMap.put(java.sql.Types.DECIMAL, "DECIMAL");
    typeStringMap.put(java.sql.Types.DISTINCT, "DISTINCT");
    typeStringMap.put(java.sql.Types.DOUBLE, "DOUBLE");
    typeStringMap.put(java.sql.Types.FLOAT, "FLOAT");
    typeStringMap.put(java.sql.Types.INTEGER, "INTEGER");
    typeStringMap.put(java.sql.Types.JAVA_OBJECT, "JAVA_OBJECT");
    typeStringMap.put(java.sql.Types.LONGVARBINARY, "LONGVARBINARY");
    typeStringMap.put(java.sql.Types.LONGVARCHAR, "LONGVARCHAR");
    typeStringMap.put(java.sql.Types.NULL, "NULL");
    typeStringMap.put(java.sql.Types.NUMERIC, "NUMERIC");
    typeStringMap.put(java.sql.Types.OTHER, "OTHER");
    typeStringMap.put(java.sql.Types.REAL, "REAL");
    typeStringMap.put(java.sql.Types.REF, "REF");
    typeStringMap.put(java.sql.Types.ROWID, "ROWID");
    typeStringMap.put(java.sql.Types.SMALLINT, "SMALLINT");
    typeStringMap.put(java.sql.Types.STRUCT, "STRUCT");
    typeStringMap.put(java.sql.Types.TIME, "TIME");
    typeStringMap.put(java.sql.Types.TIMESTAMP, "TIMESTAMP");
    typeStringMap.put(java.sql.Types.TINYINT, "TINYINT");
    typeStringMap.put(java.sql.Types.VARBINARY, "VARBINARY");
    typeStringMap.put(java.sql.Types.VARCHAR, "VARCHAR");
    return typeStringMap;
  }

}
