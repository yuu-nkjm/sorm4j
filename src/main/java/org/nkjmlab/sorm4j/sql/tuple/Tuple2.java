package org.nkjmlab.sorm4j.sql.tuple;

import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * Represents a tuple of objects, which typically represents joined two rows.
 *
 * @author nkjm
 *
 * @param <T1>
 * @param <T2>
 */
@Experimental
public class Tuple2<T1, T2> {

  private final T1 t1;
  private final T2 t2;

  Tuple2(T1 t1, T2 t2) {
    this.t1 = t1;
    this.t2 = t2;
  }

  /**
   * Gets a t1.
   *
   * @return
   */
  public T1 getT1() {
    return t1;
  }

  /**
   * Gets a t2.
   *
   * @return
   */
  public T2 getT2() {
    return t2;
  }

  @Override
  public String toString() {
    return "Tuple2 [t1=" + t1 + ", t2=" + t2 + "]";
  }

}
