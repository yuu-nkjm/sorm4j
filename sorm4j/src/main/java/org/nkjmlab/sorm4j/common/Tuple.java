package org.nkjmlab.sorm4j.common;

import java.util.Objects;
import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * Represents a tuple of objects, which typically represents joined rows.
 *
 * @author yuu_nkjm
 *
 */
@Experimental
public final class Tuple {
  private Tuple() {}

  public static <T1> Tuple1<T1> of(T1 t1) {
    return new Tuple1<>(t1);
  }

  public static <T1, T2> Tuple2<T1, T2> of(T1 t1, T2 t2) {
    return new Tuple2<>(t1, t2);
  }

  public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 t1, T2 t2, T3 t3) {
    return new Tuple3<>(t1, t2, t3);
  }

  public static class Tuple1<T1> {

    private final T1 t1;

    Tuple1(T1 t1) {
      this.t1 = t1;
    }

    /**
     * Gets a t1.
     *
     * @return
     */
    public T1 getT1() {
      return t1;
    }


    @Override
    public String toString() {
      return "(" + getT1() + ")";
    }

    @Override
    public int hashCode() {
      return Objects.hash(t1);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (!(obj instanceof Tuple1))
        return false;
      Tuple1<?> other = (Tuple1<?>) obj;
      return Objects.equals(t1, other.t1);
    }


  }

  /**
   * Represents a tuple of objects
   *
   * @author nkjm
   *
   * @param <T1>
   * @param <T2>
   */
  public static class Tuple2<T1, T2> extends Tuple1<T1> {

    private final T2 t2;

    Tuple2(T1 t1, T2 t2) {
      super(t1);
      this.t2 = t2;
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
      return "(" + getT1() + ", " + getT2() + ")";
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + Objects.hash(t2);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (!super.equals(obj))
        return false;
      if (!(obj instanceof Tuple2))
        return false;
      Tuple2<?, ?> other = (Tuple2<?, ?>) obj;
      return Objects.equals(t2, other.t2);
    }

  }
  /**
   * Represents a tuple of objects
   *
   * @author nkjm
   *
   * @param <T1>
   * @param <T2>
   * @param <T3>
   */
  public static class Tuple3<T1, T2, T3> extends Tuple2<T1, T2> {

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
      return "(" + getT1() + ", " + getT2() + ", " + getT3() + ")";
    }


    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + Objects.hash(t3);
      return result;
    }


    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (!super.equals(obj))
        return false;
      if (!(obj instanceof Tuple3))
        return false;
      Tuple3<?, ?, ?> other = (Tuple3<?, ?, ?>) obj;
      return Objects.equals(t3, other.t3);
    }

  }
}
