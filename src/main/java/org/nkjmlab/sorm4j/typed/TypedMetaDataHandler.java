package org.nkjmlab.sorm4j.typed;

import org.nkjmlab.sorm4j.sql.TableMetaData;

public interface TypedMetaDataHandler<T> {
  /**
   * Gets table name corresponding to the given object class.
   *
   * @return
   */
  String getTableName();


  /**
   * Gets table metadata corresponding to the given object class.
   *
   * @return
   */
  TableMetaData getTableMetaData();


  /**
   * Gets table metadata to the given object class and the table name.
   *
   * @return
   */
  TableMetaData getTableMetaData(String tableName);

}
