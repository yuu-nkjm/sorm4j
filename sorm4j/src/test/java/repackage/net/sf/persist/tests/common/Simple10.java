package repackage.net.sf.persist.tests.common;

import org.nkjmlab.sorm4j.annotation.OrmTable;

@OrmTable("SIMPLE")
public class Simple10 {

  private long id;
  public String stringCol;
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

  // wrong setter: has no parameter -- will use field directly
  public void setStringCol() {}

  public long getLongCol() {
    return longCol;
  }

  public void setLongCol(long longCol) {
    this.longCol = longCol;
  }

}

