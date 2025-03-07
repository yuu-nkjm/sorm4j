package org.nkjmlab.sorm4j.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.container.sql.TableName;

class ValuesObjectTest {

  @Test
  void testTableName() {
    TableName v1 = TableName.of("test");
    TableName v2 = TableName.of("test");
    verify(v1, v2);
  }

  private void verify(Object v1, Object v2) {
    assertThat(v1.toString().equals("test")).isTrue();
    assertThat(v1.equals(v1)).isTrue();
    assertThat(v1.equals(v2)).isTrue();
    assertThat(v1.equals("test")).isFalse();
    assertThat(v1.hashCode()).isEqualTo(v2.hashCode());
  }
}
