package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class StringUtilsTest {

  @Test
  void testContainsIgnoreCase() {
    assertThat(containsAsCanonical(List.of("a", "b"), null));
  }


  @Test
  void testFormat() {
    Object[] params = {};
    assertThat(ParameterizedStringFormatter.LENGTH_256.format("{}", params)).isEqualTo("{}");
    assertThat(ParameterizedStringFormatter.LENGTH_256.format("{}", (Object[]) null)).isEqualTo("{}");
  }

}
