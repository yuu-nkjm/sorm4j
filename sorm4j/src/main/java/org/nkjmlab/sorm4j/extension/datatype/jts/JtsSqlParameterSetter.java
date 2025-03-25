package org.nkjmlab.sorm4j.extension.datatype.jts;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.nkjmlab.sorm4j.context.SqlParameterSetter;

public class JtsSqlParameterSetter implements SqlParameterSetter {
  private final JtsSupportTypes cache;

  public JtsSqlParameterSetter() {
    this.cache = new JtsSupportTypes();
  }

  private boolean isJtsContainer(Class<?> type) {
    return cache.isSupport(type);
  }

  @Override
  public boolean test(PreparedStatement stmt, int parameterIndex, Object parameter)
      throws SQLException {
    return isJtsContainer(parameter.getClass());
  }

  @Override
  public void setParameter(PreparedStatement stmt, int parameterIndex, Object parameter)
      throws SQLException {
    stmt.setObject(parameterIndex, ((JtsGeometry) parameter).geometry());
  }
}
