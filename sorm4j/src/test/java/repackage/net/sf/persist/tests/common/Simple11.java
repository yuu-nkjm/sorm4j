package repackage.net.sf.persist.tests.common;

import org.nkjmlab.sorm4j.mapping.annotation.OrmTableName;

@OrmTableName("SIMPLE")
@SuppressWarnings("unused")
public class Simple11 {

  private long id;
  private java.sql.Date stringCol = new java.sql.Date(System.currentTimeMillis());
  private long longCol;
}
