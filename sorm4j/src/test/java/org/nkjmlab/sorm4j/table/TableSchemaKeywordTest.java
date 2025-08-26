package org.nkjmlab.sorm4j.table;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.sql.statement.SqlStringUtils;

class TableSchemaKeywordTest {

  @Test
  void testChars() {
    assertThat(SqlStringUtils.chars(1)).isEqualTo(" char(1) ");
  }

  @Test
  void testDecimalInt() {
    assertThat(SqlStringUtils.decimal(1)).isEqualTo(" decimal(1) ");
  }

  @Test
  void testDecimalIntInt() {
    assertThat(SqlStringUtils.decimal(1, 2)).isEqualTo(" decimal(1,2) ");
  }
}
