// $Id$

package repackage.net.sf.persist.tests.common;

import org.nkjmlab.sorm4j.mapping.annotation.OrmTable;

@OrmTable("hello_world") // will blow
@SuppressWarnings("unused")
public class Simple02 {

  private long id;
  private String stringCol;
  private long longCol;
}
