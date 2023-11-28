package org.nkjmlab.sorm4j.util.jts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.locationtech.jts.geom.Geometry;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverter;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;

@Experimental
public class JtsColumnValueToJavaObjectConverter implements ColumnValueToJavaObjectConverter {

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
  public boolean test(Class<?> toType) {
    return isJtsContainer(toType);
  }

  @Override
  public Object convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<?> toType)
      throws SQLException {
    return new GeometryJts((Geometry) resultSet.getObject(columnIndex));
  }
}
