package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MethodInvokerTest {

  @Test
  void testGetSummary() {
    assertThat(MethodInvoker.getSummary(2, "DEBUG")).contains("testGetSummary");
  }

}
