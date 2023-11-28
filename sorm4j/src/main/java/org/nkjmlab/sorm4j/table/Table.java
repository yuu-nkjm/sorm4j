package org.nkjmlab.sorm4j.table;

import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public interface Table<T> extends TableMappedOrm<T> {


  static <T> Table<T> create(Orm orm, Class<T> objectClass) {
    return new BasicTable<>(orm, objectClass);
  }
}
