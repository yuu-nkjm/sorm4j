package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.context.SormContext;

class ParameterizedStringFormatterTest {

  @Test
  void testContainsIgnoreCase() {
    assertThat(
        SormContext.getDefaultCanonicalStringCache()
            .containsCanonicalName(List.of("a", "b"), null));
  }

  @Test
  void testFormat() {
    Object[] params = {};
    assertThat(ParameterizedStringFormatter.LENGTH_256.format("{}", params)).isEqualTo("{}");
    assertThat(ParameterizedStringFormatter.LENGTH_256.format("{}", (Object[]) null))
        .isEqualTo("{}");
  }

  @Test
  void testNewStringStringObjectArray() {
    Object[] params = {null, new Object[] {"a", null, new Object[] {1, null}}};
    assertThat(ParameterizedStringFormatter.LENGTH_256.format("{},{}", params))
        .isEqualTo("null,[a, null, [1, null]]]");
  }
}
