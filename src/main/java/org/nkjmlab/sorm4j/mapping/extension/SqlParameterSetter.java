package org.nkjmlab.sorm4j.mapping.extension;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A setter for given {@link PreparedStatement}.
 *
 * @author nkjm
 *
 */
public interface SqlParameterSetter {
  /**
   * Sets parameters in the given prepared statement. i.e. Convert From Java To Sql.
   *
   * @param stmt {@link java.sql.PreparedStatement} to have parameters set into
   * @param parameter parameters values
   * @throws SQLException
   *
   */
  void setParameter(PreparedStatement stmt, int column, Object parameter) throws SQLException;

  void setParameters(PreparedStatement stmt, Object... parameters) throws SQLException;


}
