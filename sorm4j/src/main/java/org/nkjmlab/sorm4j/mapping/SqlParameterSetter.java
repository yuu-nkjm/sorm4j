package org.nkjmlab.sorm4j.mapping;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
@FunctionalInterface
public interface SqlParameterSetter {

  /**
   * Sets parameter.
   * @param stmt
   * @param parameterIndex
   * @param parameter
   *
   * @throws SQLException
   */
  void setParameter(PreparedStatement stmt, int parameterIndex, Object parameter) throws SQLException;
}
