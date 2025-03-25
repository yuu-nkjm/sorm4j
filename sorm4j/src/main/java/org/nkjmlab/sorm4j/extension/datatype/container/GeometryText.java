package org.nkjmlab.sorm4j.extension.datatype.container;

/**
 * A container class for storing geometry data in WKT/EWKT (Well-known text representation of
 * geometry) format. This class provides methods for retrieving and creating instances of geometry
 * strings.
 *
 * <p>WKT (Well-known text) is a text markup language for representing vector geometry objects. EWKT
 * (Extended Well-Known Text) is a PostGIS extension of WKT that includes a spatial reference
 * identifier (SRID).
 *
 * @see <a href="https://en.wikipedia.org/wiki/Well-known_text_representation_of_geometry">
 *     Well-known text representation of geometry - Wikipedia</a>
 * @author nkjm
 */
public record GeometryText(String text) {

  /**
   * Returns the stored geometry string in WKT/EWKT format.
   *
   * @return the geometry string representation.
   */
  public String text() {
    return text;
  }

  @Override
  public String toString() {
    return text();
  }

  /**
   * Creates a new {@code GeometryText} instance from a given WKT/EWKT string.
   *
   * @param text the geometry string in WKT/EWKT format.
   * @return a new instance of {@code GeometryText}.
   */
  public static GeometryText of(String text) {
    return new GeometryText(text);
  }
}
