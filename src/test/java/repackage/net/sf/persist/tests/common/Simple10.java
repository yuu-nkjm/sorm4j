package repackage.net.sf.persist.tests.common;

import org.nkjmlab.sorm4j.annotation.OrmTable;

@OrmTable("SIMPLE")
public class Simple10 {

  private long id;
  private String stringCol;
  private long longCol;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  // setter has no parameter -- will blow
  public String getStringCol() {
    return stringCol;
  }

  public void setStringCol() {}

  public long getLongCol() {
    return longCol;
  }

  public void setLongCol(long longCol) {
    this.longCol = longCol;
  }

}

