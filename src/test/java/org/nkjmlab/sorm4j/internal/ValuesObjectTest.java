package org.nkjmlab.sorm4j.internal;

import static org.assertj.core.api.Assertions.*;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.extension.ColumnName;
import org.nkjmlab.sorm4j.extension.FieldName;
import org.nkjmlab.sorm4j.extension.TableName;

class ValuesObjectTest {

  @Test
  void testTableName() {
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
    ColumnName v1 = new ColumnName("test");
    ColumnName v2 = new ColumnName("test");
    verify(v1, v2);

  }

  @Test
  void testColumnOrder() {
    ColumnName v1 = new ColumnName("2test");
    ColumnName v2 = new ColumnName("1test");
    assertThat(List.of(v1, v2).stream().sorted().collect(Collectors.toList()).get(0)).isEqualTo(v2);

  }

}
