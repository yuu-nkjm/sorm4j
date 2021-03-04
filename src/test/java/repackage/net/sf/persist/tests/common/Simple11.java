package repackage.net.sf.persist.tests.common;

import org.nkjmlab.sorm4j.annotation.OrmTable;

@OrmTable("SIMPLE")
public class Simple11 {

  private long id;
  private java.sql.Date stringCol = new java.sql.Date(System.currentTimeMillis());
  private long longCol;


}

