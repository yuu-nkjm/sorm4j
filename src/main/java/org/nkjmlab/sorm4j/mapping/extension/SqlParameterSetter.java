package org.nkjmlab.sorm4j.mapping.extension;

import java.io.IOException;
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
   * Sets parameters into the given prepared statement. i.e. Convert from java objects to SQL.
   *
   * @param stmt {@link java.sql.PreparedStatement} to have parameters set into
   * @param parameters parameters values
   * @throws SQLException
   * @throws IOException
   *
   */
  void setParameters(PreparedStatement stmt, Object... parameters) throws SQLException;


}
