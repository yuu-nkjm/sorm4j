package org.nkjmlab.sorm4j.extension;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A setter for given {@link PreparedStatement}.
 *
 * @author nkjm
 *
 */
public interface SqlParametersSetter {

  /**
   * Sets parameters into the given prepared statement. i.e. Convert from java objects to SQL.
   *
   * @param options
   * @param stmt {@link java.sql.PreparedStatement} to have parameters set into
   * @param parameters parameters values
   *
   */
  void setParameters(SormOptions options, PreparedStatement stmt, Object... parameters)
      throws SQLException;


}
