package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ParameterizedStringUtilsTest {

  @Test
  void testNewStringStringObjectArray() {
    assertThat(ParameterizedStringUtils.newString("{},{}", null,
        new Object[] {"a", null, new Object[] {1, null}})).isEqualTo("null,[a, null, [1, null]]]");
  }

}
