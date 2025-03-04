// $Id$

package repackage.net.sf.persist.tests.common;

import org.nkjmlab.sorm4j.mapping.annotation.OrmColumn;
import org.nkjmlab.sorm4j.mapping.annotation.OrmTable;

@OrmTable("simple")
public class Simple01 {

  public long id;
  public String stringCol;
  // field name annotation
  @OrmColumn("LONG_COL")
  public long intCol;
}
