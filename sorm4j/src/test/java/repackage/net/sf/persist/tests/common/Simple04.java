// $Id$

package repackage.net.sf.persist.tests.common;

import org.nkjmlab.sorm4j.mapping.annotation.OrmTable;

@OrmTable("SIMPLE")
public class Simple04 {

  private long id;

  @SuppressWarnings("unused")
  private String stringCol;

  private long longCol;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  // throw
  public String getStringCol() {
    throw new RuntimeException("Sample4");
  }

  // throw
  public void setStringCol(String stringCol) {
    throw new RuntimeException("Sample4");
  }

  // getter and setter have incompatible types. argument type mismatch. -- will blow
  public long getLongCol() {
    return longCol;
  }

  public void setLongCol(boolean intCol) {
    this.longCol = 9999;
  }
}
