package org.nkjmlab.sorm4j.extension.datatype.jts;

import java.util.Objects;

import org.locationtech.jts.geom.Geometry;
import org.nkjmlab.sorm4j.common.annotation.Experimental;

@Experimental
public class GeometryJts {

  private final Geometry geometry;

  public GeometryJts(Geometry geometry) {
    this.geometry = geometry;
  }

  public Geometry getGeometry() {
    return geometry;
  }

  @Override
  public String toString() {
    return geometry.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(geometry);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof GeometryJts)) return false;
    GeometryJts other = (GeometryJts) obj;
    return Objects.equals(geometry, other.geometry);
  }
}
