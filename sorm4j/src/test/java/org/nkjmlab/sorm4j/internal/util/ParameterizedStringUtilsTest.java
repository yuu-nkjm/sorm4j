package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ParameterizedStringUtilsTest {

  @Test
  void testNewStringStringObjectArray() {
    Object[] params = {null, new Object[] {"a", null, new Object[] {1, null}}};
    assertThat(ParameterizedStringFormat.DEFAULT.format("{},{}", params))
        .isEqualTo("null,[a, null, [1, null]]]");

    System.out.println(ParameterizedStringFormat.DEFAULT.convertToStringWithType(1, 1L, "hoge",
        LocalDate.now(), new int[] {1, 2}));
    System.out
        .println(ParameterizedStringFormat.DEFAULT.convertToString(1, 1L, "hoge", LocalDate.now()));
  }

}
