
// $Id$

package repackage.net.sf.persist.tests.common;

import org.nkjmlab.sorm4j.annotation.OrmGetter;
import org.nkjmlab.sorm4j.annotation.OrmSetter;
import org.nkjmlab.sorm4j.annotation.OrmTable;

@OrmTable("SIMPLE")
public class Simple06 {

  private long id;
  private String stringCol;
  private long longLongCol;

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

  @OrmGetter("long_col")
  public long getLongLongCol() {
    return longLongCol;
  }

  @OrmSetter("long_col")
  public void setLongLongCol(long intCol) {
    this.longLongCol = intCol;
  }

}

