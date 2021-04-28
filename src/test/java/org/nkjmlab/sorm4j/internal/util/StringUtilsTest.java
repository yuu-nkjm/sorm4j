package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class StringUtilsTest {
  private static final org.slf4j.Logger log =
      org.nkjmlab.sorm4j.internal.util.LoggerFactory.getLogger();

  @Test
  void testContainsIgnoreCase() {
    assertThat(StringUtils.containsAsCanonical(List.of("a", "b"), null));
  }

  @Test
  void testInvoker() {
    assertThat(log.getName()).contains(StringUtils.class.getSimpleName());
  }

  @Test
  void testFormat() {
    assertThat(StringUtils.format("{}")).isEqualTo("{}");
    assertThat(StringUtils.format("{}", null)).isEqualTo("{}");
  }

}
