package org.nkjmlab.sorm4j.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JdbcColumnMetaDataTest {

  @Test
  void testCompareTo() {
    assertThat(
            new JdbcColumnMetaDataImpl(
                "a", "b", "c", "d", 0, "e", 10, 10, 0, 1, 0, 255, "YES", "default", "remarks", "NO",
                "NO"))
        .isEqualTo(
            new JdbcColumnMetaDataImpl(
                "a", "b", "c", "d", 0, "e", 10, 10, 0, 1, 0, 255, "YES", "default", "remarks", "NO",
                "NO"));
  }
}
