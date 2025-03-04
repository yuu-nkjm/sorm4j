package org.nkjmlab.sorm4j.util.datatype.jts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.locationtech.jts.geom.Geometry;
import org.nkjmlab.sorm4j.common.Experimental;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverter;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;

@Experimental
public class JtsColumnValueToJavaObjectConverter implements ColumnValueToJavaObjectConverter {

  private final Map<Class<?>, Boolean> jtsContainer = new ConcurrentHashMap<>();

  @Override
  public boolean test(Class<?> toType) {
    return jtsContainer.computeIfAbsent(
        (Class<?>) toType,
        key ->
            org.nkjmlab.sorm4j.util.datatype.jts.GeometryJts.class.isAssignableFrom(toType)
                || org.nkjmlab.sorm4j.util.datatype.jts.GeometryJts.class.isAssignableFrom(
                    ArrayUtils.getInternalComponentType(toType)));
  }

  @Override
  public <T> T convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<T> toType)
      throws SQLException {
    return toType.cast(new GeometryJts((Geometry) resultSet.getObject(columnIndex)));
  }
}
