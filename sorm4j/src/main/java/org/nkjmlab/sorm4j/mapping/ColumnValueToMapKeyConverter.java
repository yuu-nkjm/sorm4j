package org.nkjmlab.sorm4j.mapping;

public interface ColumnValueToMapKeyConverter {

  /**
   * Converts column name to key. This method is used while converting {@link java.sql.ResultSet}
   * rows to Map.
   *
   * @param columnName
   * @return
   */
  String convertToKey(String columnName);

}
