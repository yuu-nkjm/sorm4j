package org.nkjmlab.sorm4j.sqlstatement;

import java.util.List;
import org.nkjmlab.sorm4j.result.LazyResultSet;

public interface Query<T> {

  T readOne();

  T readFirst();

  LazyResultSet<T> readLazy();

  List<T> readList();

}
