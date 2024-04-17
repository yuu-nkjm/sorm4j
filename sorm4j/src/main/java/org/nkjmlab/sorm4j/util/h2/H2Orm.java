package org.nkjmlab.sorm4j.util.h2;

import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public interface H2Orm {
  /**
   * Gets {@link Orm} object
   *
   * @return
   */
  Orm getOrm();
}
