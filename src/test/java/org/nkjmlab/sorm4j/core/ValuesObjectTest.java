package org.nkjmlab.sorm4j.core;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.core.util.LoggerFactory;
import org.nkjmlab.sorm4j.extension.Column;
import org.nkjmlab.sorm4j.extension.FieldName;
import org.nkjmlab.sorm4j.extension.TableName;

class ValuesObjectTest {

  @Test
  void testTableName() {
    System.out.println(LoggerFactory.class.getName());
    TableName v1 = new TableName("test");
    TableName v2 = new TableName("test");
    verify(v1, v2);

  }

  private void verify(Object v1, Object v2) {
    assertThat(v1.toString().equals("test")).isTrue();
    assertThat(v1.equals(v1)).isTrue();
    assertThat(v1.equals(v2)).isTrue();
    assertThat(v1.equals("test")).isFalse();
    assertThat(v1.hashCode()).isEqualTo(v2.hashCode());

  }

  @Test
  void testFieldName() {
    FieldName v1 = new FieldName("test");
    FieldName v2 = new FieldName("test");
    verify(v1, v2);
  }

  @Test
  void testColumn() {
    Column v1 = new Column("test");
    Column v2 = new Column("test");
    verify(v1, v2);

  }

}
