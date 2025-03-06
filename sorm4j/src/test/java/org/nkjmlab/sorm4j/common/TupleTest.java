package org.nkjmlab.sorm4j.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.container.Tuple;
import org.nkjmlab.sorm4j.container.Tuple.Tuple1;
import org.nkjmlab.sorm4j.container.Tuple.Tuple2;
import org.nkjmlab.sorm4j.container.Tuple.Tuple3;

class TupleTest {

  @Test
  void testOfT1() {
    Tuple1<String> t1 = Tuple.of("t1");
    Tuple1<String> t2 = Tuple.of("t1");

    assertThat(t1.equals(t2)).isTrue();
    assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
    assertThat(t1.toString()).isEqualTo("(t1)");
  }

  @Test
  void testOfT2() {
    Tuple2<String, String> t1 = Tuple.of("t1", "t2");
    Tuple2<String, String> t2 = Tuple.of("t1", "t2");

    assertThat(t1.equals(t2)).isTrue();
    assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
    assertThat(t1.toString()).isEqualTo("(t1, t2)");
  }

  @Test
  void testOfT3() {
    Tuple3<String, String, String> t1 = Tuple.of("t1", "t2", "t3");
    Tuple3<String, String, String> t2 = Tuple.of("t1", "t2", "t3");

    assertThat(t1.equals(t2)).isTrue();
    assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
    assertThat(t1.toString()).isEqualTo("(t1, t2, t3)");
  }
}
