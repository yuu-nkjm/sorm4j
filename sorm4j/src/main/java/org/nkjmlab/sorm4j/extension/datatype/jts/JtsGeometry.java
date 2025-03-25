package org.nkjmlab.sorm4j.extension.datatype.jts;

import org.locationtech.jts.geom.Geometry;

public record JtsGeometry(Geometry geometry) {

  @Override
  public String toString() {
    return geometry.toString();
  }
}
