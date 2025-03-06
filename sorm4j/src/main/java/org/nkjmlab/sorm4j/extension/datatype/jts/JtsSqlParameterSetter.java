package org.nkjmlab.sorm4j.extension.datatype.jts;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.nkjmlab.sorm4j.context.SqlParameterSetter;

public class JtsSqlParameterSetter implements SqlParameterSetter {
  private final ContainerCache cache;

  public JtsSqlParameterSetter() {
    this.cache = new ContainerCache();
  }

  private boolean isJtsContainer(Class<?> type) {
    return cache.isContainer(type);
  }

  @Override
  public boolean test(PreparedStatement stmt, int parameterIndex, Object parameter)
      throws SQLException {
    return isJtsContainer(parameter.getClass());
  }

  @Override
  public void setParameter(PreparedStatement stmt, int parameterIndex, Object parameter)
      throws SQLException {
    stmt.setObject(parameterIndex, ((GeometryJts) parameter).getGeometry());
  }
}
