package org.nkjmlab.sorm4j.extension.datatype.jts;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.locationtech.jts.geom.Geometry;
import org.nkjmlab.sorm4j.common.annotation.Experimental;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverter;

@Experimental
public class JtsColumnValueToJavaObjectConverter implements ColumnValueToJavaObjectConverter {

  private final JtsSupportTypes cache;

  public JtsColumnValueToJavaObjectConverter() {
    this.cache = new JtsSupportTypes();
  }

  @Override
  public boolean test(Class<?> toType) {
    return cache.isSupport(toType);
  }

  @Override
  public <T> T convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<T> toType)
      throws SQLException {
    return toType.cast(new GeometryJts((Geometry) resultSet.getObject(columnIndex)));
  }
}
