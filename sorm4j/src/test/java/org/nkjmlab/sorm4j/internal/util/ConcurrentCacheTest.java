package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class ConcurrentCacheTest {

  @Test
  void test() {
    ConcurrentCache<String, String> m = new ConcurrentCache<>(10);
    IntStream.range(0, 99).forEach(i -> m.put(String.valueOf(i), String.valueOf(i)));
    assertThat(m.size()).isEqualTo(9);

    m.putAll(Map.of("a", "a"));
    assertThat(m.size()).isEqualTo(10);
    m.putAll(Map.of("b", "b"));
    assertThat(m.size()).isEqualTo(1);
  }
}
