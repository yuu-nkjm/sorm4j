package org.nkjmlab.sorm4j.mapping;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ValuesObjectTest {

  @Test
  void test() {
    assertThat(new TableName("test")).isEqualTo(new TableName("test"));
  }

}
