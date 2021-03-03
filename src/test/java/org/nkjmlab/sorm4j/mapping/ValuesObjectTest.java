package org.nkjmlab.sorm4j.mapping;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.util.LoggerFactory;

class ValuesObjectTest {

  @Test
  void testTableName() {
    System.out.println(LoggerFactory.class.getName());

    TableName t1 = new TableName("test");
    TableName t2 = new TableName("test");
    assertThat(t1.toString()).isEqualTo("test");
    assertThat(t1).isEqualTo(t1);
    assertThat(t1).isEqualTo(t2);
    assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
    assertThat(t1).isNotEqualTo("test");
    assertThat(t1.toString()).isEqualTo("test");
  }

  @Test
  void testFieldName() {
    FieldName t1 = new FieldName("test");
    FieldName t2 = new FieldName("test");
    assertThat(t1.toString()).isEqualTo("test");
    assertThat(t1).isEqualTo(t1);
    assertThat(t1).isEqualTo(t2);
    assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
  }

  @Test
  void testColumn() {
    Column t1 = new Column("test");
    Column t2 = new Column("test");
    assertThat(t1.toString()).isEqualTo("test");
    assertThat(t1).isEqualTo(t1);
    assertThat(t1).isEqualTo(t2);
    assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
  }

}
