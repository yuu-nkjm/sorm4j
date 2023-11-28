package org.nkjmlab.sorm4j.util.datatype;

import java.util.Objects;
import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * @see <a href=
 *     "https://en.wikipedia.org/wiki/Well-known_text_representation_of_geometry">Well-known text
 *     representation of geometry - Wikipedia</a>
 * @author nkjm
 */
@Experimental
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
