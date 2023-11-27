package org.nkjmlab.sorm4j.table;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public interface Table<T> extends TableMappedOrm<T> {

  /**
   * Gets Sorm objects
   *
   * @return
   */
  @Override
  Sorm getOrm();

  static <T> Table<T> create(Sorm sorm, Class<T> objectClass) {
    return new BasicTable<>(sorm, objectClass);
  }
}
