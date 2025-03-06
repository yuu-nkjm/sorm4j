package org.nkjmlab.sorm4j.container.datatype;

import java.util.Objects;

/**
 * @see <a href=
 *     "https://en.wikipedia.org/wiki/Well-known_text_representation_of_geometry">Well-known text
 *     representation of geometry - Wikipedia</a>
 * @author nkjm
 */
public class GeometryString {

  private String text;

  /**
   * @param text is quoted string containing a WKT/EWKT (Well-known text representation of geometry)
   */
  public GeometryString(String text) {
    this.text = text;
  }

  /**
   * Quoted string containing a WKT/EWKT (Well-known text representation of geometry)
   *
   * @return
   */
  public String getText() {
    return text;
  }

  @Override
  public String toString() {
    return text;
  }

  @Override
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof GeometryString)) return false;
    GeometryString other = (GeometryString) obj;
    return Objects.equals(text, other.text);
  }
}
