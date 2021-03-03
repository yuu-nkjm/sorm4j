
// $Id$

package repackage.net.sf.persist.tests.common;

import org.nkjmlab.sorm4j.annotation.OrmTable;

@OrmTable("SIMPLE")

public class Simple04 {

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
    // return stringCol;
    throw new RuntimeException("Sample4");
  }

  public void setStringCol(String stringCol) {
    this.stringCol = stringCol;
  }

  // getter and setter have incompatible types. argument type mismatch. -- will blow
  public long getLongCol() {
    return longCol;
  }

  public void setLongCol(boolean intCol) {
    this.longCol = 9999;
  }

}

