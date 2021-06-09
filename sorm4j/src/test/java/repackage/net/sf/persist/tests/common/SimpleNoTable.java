
// $Id: Simple.java 7 2007-08-17 19:32:18Z jcamaia $

package repackage.net.sf.persist.tests.common;

public class SimpleNoTable {

  private long id;
  private String stringCol;
  private long longCol;

  public SimpleNoTable() {}

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



  @Override
  public String toString() {
    return "id=" + id + " longCol=" + longCol + " stringCol=" + stringCol;
  }



}

