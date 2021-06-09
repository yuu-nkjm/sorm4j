package org.nkjmlab.sorm4j.sql.result;

import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * Represents a tuple of objects, which typically represents joined three rows.
 *
 * @author nkjm
 *
 * @param <T1>
 * @param <T2>
 * @param <T3>
 */
@Experimental
public class Tuple3<T1, T2, T3> extends Tuple2<T1, T2> {

  private final T3 t3;

  Tuple3(T1 t1, T2 t2, T3 t3) {
    super(t1, t2);
    this.t3 = t3;
  }


  /**
   * Gets a t3.
   *
   * @return
   */
  public T3 getT3() {
    return t3;
  }

  @Override
  public String toString() {
    return "Tuple3 [t1=" + getT1() + ", t2=" + getT2() + ", t3=" + getT3() + "]";
  }


}
