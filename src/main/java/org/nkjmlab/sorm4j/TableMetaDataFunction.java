package org.nkjmlab.sorm4j;

import org.nkjmlab.sorm4j.sql.TableMetaData;

public interface TableMetaDataFunction {
  /**
   * Gets table name corresponding to the given object class.
   *
   * @param objectClass
   * @return
   */
  String getTableName(Class<?> objectClass);


  /**
   * Gets table metadata corresponding to the given object class.
   *
   * @param objectClass
   * @return
   */
  TableMetaData getTableMetaData(Class<?> objectClass);


  /**
   * Gets table metadata to the given object class and the table name.
   *
   * @param objectClass
   * @return
   */
  TableMetaData getTableMetaData(Class<?> objectClass, String tableName);

}
