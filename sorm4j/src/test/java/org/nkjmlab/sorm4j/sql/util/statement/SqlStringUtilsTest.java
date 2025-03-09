package org.nkjmlab.sorm4j.sql.util.statement;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.util.sql.SqlStringUtils;

class SqlStringUtilsTest {

  @Test
  void test() {

    assertThat(SqlStringUtils.literal(new String[] {"a", "b", null}))
        .isEqualTo("array ['a', 'b', null]");
    assertThat(SqlStringUtils.literal(List.of("a", "b"))).isEqualTo("'a', 'b'");
    assertThat(SqlStringUtils.literal(1)).isEqualTo(String.valueOf(1));
    assertThat(SqlStringUtils.literal(true)).isEqualTo(String.valueOf(true));

    assertThat(SqlStringUtils.literal(null)).isEqualTo("null");
    assertThat(SqlStringUtils.literal("?")).isEqualTo("?");
  }
}
