package org.nkjmlab.sorm4j.table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.chars;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.decimal;

import org.junit.jupiter.api.Test;

class TableSchemaKeywordTest {

  @Test
  void testChars() {
    assertThat(chars(1)).isEqualTo(" char(1) ");
  }

  @Test
  void testDecimalInt() {
    assertThat(decimal(1)).isEqualTo(" decimal(1) ");
  }

  @Test
  void testDecimalIntInt() {
    assertThat(decimal(1, 2)).isEqualTo(" decimal(1,2) ");
  }
}
