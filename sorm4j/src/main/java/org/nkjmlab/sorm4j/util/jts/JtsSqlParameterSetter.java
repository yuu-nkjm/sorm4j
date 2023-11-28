package org.nkjmlab.sorm4j.util.jts;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.context.SqlParameterSetter;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;

@Experimental
public class JtsSqlParameterSetter implements SqlParameterSetter {
  private final Map<Class<?>, Boolean> jtsContainer = new ConcurrentHashMap<>();

  private boolean isJtsContainer(Class<?> type) {
    return jtsContainer.computeIfAbsent(
        type,
        key ->
            org.nkjmlab.sorm4j.util.jts.GeometryJts.class.isAssignableFrom(type)
                || org.nkjmlab.sorm4j.util.jts.GeometryJts.class.isAssignableFrom(
                    ArrayUtils.getInternalComponentType(type)));
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
