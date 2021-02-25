
// $Id$

package repackage.net.sf.persist.tests.common;

import org.nkjmlab.sorm4j.annotation.OrmTable;

@OrmTable("SIMPLE")
public class Simple09 {

  private long id;
  // private String stringCol;
  private long intCol;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  // getter returns void -- will blow
  public void getStringCol() {}

  public void setStringCol(String stringCol) { /* this.stringCol = stringCol; */ }

  public long getIntCol() {
    return intCol;
  }

  public void setIntCol(long intCol) {
    this.intCol = intCol;
  }

}

