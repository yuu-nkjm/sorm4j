package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class StringUtilsTest {

  @Test
  void testContainsIgnoreCase() {
    assertThat(StringUtils.containsAsCanonical(List.of("a", "b"), null));
  }


  @Test
  void testFormat() {
    assertThat(StringUtils.format("{}")).isEqualTo("{}");
    assertThat(StringUtils.format("{}", null)).isEqualTo("{}");
  }

}
