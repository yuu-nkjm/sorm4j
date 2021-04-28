package org.nkjmlab.sorm4j.extension;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ParameterSetter {
  boolean isApplicable(SormOptions options, PreparedStatement stmt, int parameterIndex,
      Class<?> parameterClass, Object parameter);

  void setParameter(SormOptions options, PreparedStatement stmt, int parameterIndex,
      Class<?> parameterClass, Object parameter) throws SQLException;
}