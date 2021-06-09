
// $Id$

package repackage.net.sf.persist.tests.common;

import org.nkjmlab.sorm4j.annotation.OrmTable;

@OrmTable("SIMPLE")
public class Simple07 {

  private long id;
  // private String stringCol;
  private long longCol;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getHelloWorld() {
    return "hello world";
  }

  public void setHelloWorld(String stringCol) { /* do nothing */ }

  public long getLongCol() {
    return longCol;
  }

  public void setLongCol(long intCol) {
    this.longCol = intCol;
  }

  // won't find a match for foo
  public String getFoo() {
    return "foo";
  }

  public void setFoo(String foo) { /* do nothing */ }

}

