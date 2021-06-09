package repackage.net.sf.persist.tests.common;

import org.nkjmlab.sorm4j.annotation.OrmTable;

@OrmTable("SIMPLE")
@SuppressWarnings("unused")
public class Simple12 {

  private long id;
  private String stringCol;
  private long longCol;

  // no plain constructor -- fail
  public Simple12(long id) {}


}

