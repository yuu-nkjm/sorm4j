package org.nkjmlab.sorm4j.extension;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
@FunctionalInterface
public interface SqlParameterSetter {

  /**
   * Sets parameter.
   *
   * @param options
   * @param stmt
   * @param parameterIndex
   * @param parameterClass
   * @param parameter
   * @throws SQLException
   */
  void setParameter(SormOptions options, PreparedStatement stmt, int parameterIndex,
      Class<?> parameterClass, Object parameter) throws SQLException;
}
