package org.nkjmlab.sorm4j;

import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public interface TableMappedOrmConnection<T> extends TableMappedOrm<T> {

  /**
   * Gets Sorm objects
   *
   * @return
   */
  @Override
  OrmConnection getOrm();

}
