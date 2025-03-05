package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ParameterizedStringUtilsTest {

  @Test
  void testNewStringStringObjectArray() {
    Object[] params = {null, new Object[] {"a", null, new Object[] {1, null}}};
    assertThat(ParameterizedStringFormatter.LENGTH_256.format("{},{}", params))
        .isEqualTo("null,[a, null, [1, null]]]");
  }
}
