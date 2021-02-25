package org.nkjmlab.sorm4j.config;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface JavaToSqlDataConverter extends OrmConfig {
  /**
   * Sets parameters in the given prepared statement. i.e. Convert From Java To Sql.
   *
   *
   * @param stmt {@link java.sql.PreparedStatement} to have parameters set into
   * @param parameters parameters values
   * @throws SQLException
   * @since 1.0
   */
  void setParameter(PreparedStatement stmt, int column, Object parameter) throws SQLException;

  void setParameters(PreparedStatement stmt, Object... parameters) throws SQLException;


}
