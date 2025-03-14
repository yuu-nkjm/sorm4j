package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MethodInvokerInfoUtilsTest {

  @Test
  void testGetInvoker() {
    assertThat(MethodInvokerInfoUtils.getInvokerInfo(-1, new Throwable().getStackTrace()))
        .contains(MethodInvokerInfoUtilsTest.class.getName());
    assertThat(
            MethodInvokerInfoUtils.getInvokerInfo(
                Integer.MAX_VALUE, new Throwable().getStackTrace()))
        .isNotNull();
    assertThat(MethodInvokerInfoUtils.getOutsideInvoker(""));
  }
}
