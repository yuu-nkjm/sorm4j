package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.context.SormContext;

class StringUtilsTest {

  @Test
  void testContainsIgnoreCase() {
    assertThat(SormContext.getDefaultCanonicalStringCache().containsCanonicalName(List.of("a", "b"), null));
  }

  @Test
  void testFormat() {
    Object[] params = {};
    assertThat(ParameterizedStringFormatter.LENGTH_256.format("{}", params)).isEqualTo("{}");
    assertThat(ParameterizedStringFormatter.LENGTH_256.format("{}", (Object[]) null))
        .isEqualTo("{}");
  }
}
