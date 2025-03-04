package repackage.net.sf.persist.tests.common;

import org.nkjmlab.sorm4j.mapping.annotation.OrmTable;

@OrmTable("SIMPLE")
public class Simple12 {

  public long id;
  public String stringCol;
  public long longCol;

  // no plain constructor -- fail
  public Simple12(long id) {}
}
