package org.nkjmlab.sorm4j.util.h2;

import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public interface H2Connection extends H2Orm {

  /**
   * Gets {@link OrmConnection} object
   *
   * @return
   */
  @Override
  OrmConnection getOrm();
}
