package org.nkjmlab.sorm4j.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.internal.sql.TableName;

class TableNameTest {

  @Test
  void testTableName() {
    TableName t1 = TableName.of("test");
    TableName t2 = TableName.of("test");
    assertThat(t1.equals(t1)).isTrue();
    assertThat(t1.equals("1")).isFalse();

    assertThat(t1.toString().equals("test")).isTrue();
    assertThat(t1.equals(t1)).isTrue();
    assertThat(t1.equals(t2)).isTrue();
    assertThat(t1.equals("test")).isFalse();
    assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
  }

  @Test
  void testCompare() {
    assertThat(TableName.of("a").compareTo(TableName.of("b"))).isEqualTo(-1);
    assertThat(TableName.of("a").compareTo(TableName.of("a"))).isEqualTo(0);
    assertThat(TableName.of("b").compareTo(TableName.of("a"))).isEqualTo(1);
  }
}
