
// $Id$

package repackage.net.sf.persist.tests.common;

import org.nkjmlab.sorm4j.annotation.OrmTable;

@OrmTable("SIMPLE")
public class Simple08 {

  private long id;
  private String stringCol;
  private long intCol;

  // invalid return type
  public void getId() {
    return;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getHelloWorld() {
    return "hello world";
  }

  public void setHelloWorld(String stringCol) { /* do nothing */ }

  // conflicting annotations -- will blow
  public long getIntCol() {
    return intCol;
  }

  public void setIntCol(long intCol) {
    this.intCol = intCol;
  }

  // invalid parameter
  public String getStringCol(String str) {
    return stringCol;
  }

  public void setStringCol(String stringCol) {
    this.stringCol = stringCol;
  }

}

