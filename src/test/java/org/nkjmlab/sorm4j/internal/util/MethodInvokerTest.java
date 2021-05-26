package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.internal.extension.MethodInvoker;

class MethodInvokerTest {

  @Test
  void testGetSummary() {
    assertThat(MethodInvoker.getSummary(0, "DEBUG")).contains("MethodInvoker");
    assertThat(MethodInvoker.getSummary(2, "DEBUG")).contains("testGetSummary");
  }

}
