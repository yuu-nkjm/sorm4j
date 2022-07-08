package org.nkjmlab.sorm4j.context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public interface SqlParameterSetter {

  boolean test(PreparedStatement stmt, int parameterIndex, Object parameter) throws SQLException;


  /**
   * Sets parameter.
   *
   * @param stmt
   * @param parameterIndex
   * @param parameter
   *
   * @throws SQLException
   */
  void setParameter(PreparedStatement stmt, int parameterIndex, Object parameter)
      throws SQLException;
}
