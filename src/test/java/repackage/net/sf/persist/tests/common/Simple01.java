
// $Id$

package repackage.net.sf.persist.tests.common;

import org.nkjmlab.sorm4j.annotation.OrmColum;
import org.nkjmlab.sorm4j.annotation.OrmTable;

@OrmTable("simple")
@SuppressWarnings("unused")
public class Simple01 {

  private long id;
  private String stringCol;
  // field name annotation
  @OrmColum("LONG_COL")
  private long intCol;



}

