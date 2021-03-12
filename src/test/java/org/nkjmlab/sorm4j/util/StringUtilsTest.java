package org.nkjmlab.sorm4j.util;

import static org.assertj.core.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.core.util.StringUtils;

class StringUtilsTest {
  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.core.util.LoggerFactory.getLogger();

  @Test
  void testContainsIgnoreCase() {
    assertThat(StringUtils.containsIgnoreCase(List.of("a", "b"), null));
  }

  @Test
  void testInvoker() {
    assertThat(log.getName()).contains(StringUtils.class.getSimpleName());
  }

}
