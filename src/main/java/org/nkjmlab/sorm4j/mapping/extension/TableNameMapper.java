package org.nkjmlab.sorm4j.mapping.extension;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.nkjmlab.sorm4j.mapping.TableName;

/**
 * A mapper from class or table name to a valid table name exists on database.
 *
 * @author nkjm
 *
 */
public interface TableNameMapper {

  /**
   * Get table name corresponding with the object class.
   *
   * @param objectClass object class mapping to table
   * @param metaData
   * @return table name exists in database.
   */
  TableName getTableName(Class<?> objectClass, DatabaseMetaData metaData) throws SQLException;

  TableName toValidTableName(String tableName, DatabaseMetaData metaData) throws SQLException;

}
