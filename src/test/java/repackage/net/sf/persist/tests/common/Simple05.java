
// $Id$

package repackage.net.sf.persist.tests.common;

// doesn't specify a table and guessed names won't work -- will blow
public class Simple05 {

  private long id;
  private String stringCol;
  private long longCol;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getStringCol() {
    return stringCol;
  }

  public void setStringCol(String stringCol) {
    this.stringCol = stringCol;
  }

  public long getLongCol() {
    return longCol;
  }

  public void setLongCol(long intCol) {
    this.longCol = intCol;
  }

}

