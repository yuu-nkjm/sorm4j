package org.nkjmlab.sorm4j.config;

import java.sql.Connection;

public interface TableNameMapper extends OrmConfig {

  /**
   * Get table name corresponding with the object class.
   *
   * @param objectClass object class mapping to table
   * @param connection
   * @return table name exists in database.
   */
  String getTableName(Class<?> objectClass, Connection connection);



}
